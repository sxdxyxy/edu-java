package com.joyfishs.dawa.safetycode.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.joyfishs.dawa.safetycode.entity.SafetyCode;
import com.joyfishs.dawa.qualification.entity.Qualification;
import com.joyfishs.dawa.qualification.service.QualificationService;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import com.joyfishs.dawa.violation.service.ViolationRecordService;
import com.joyfishs.dawa.student.service.StudentCourseListService;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesData;
import com.joyfishs.dawa.archives.config.service.PersonArchivesDataService;
import com.joyfishs.dawa.safety.service.SafetyScoreAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 安全码三色评估器
 * <p>
 * 根据违章记录、资质状态、培训情况评估用户安全码颜色
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Slf4j
@Component
public class SafetyCodeEvaluator {

    private final QualificationService qualificationService;
    private final ViolationRecordService violationRecordService;
    private final StudentCourseListService studentCourseListService;
    private final PersonService personService;
    private final PersonArchivesDataService archivesDataService;
    private final SafetyScoreAccountService safetyScoreAccountService;

    @Autowired
    public SafetyCodeEvaluator(
            QualificationService qualificationService,
            ViolationRecordService violationRecordService,
            StudentCourseListService studentCourseListService,
            PersonService personService,
            @Lazy PersonArchivesDataService archivesDataService,
            @Lazy SafetyScoreAccountService safetyScoreAccountService) {
        this.qualificationService = qualificationService;
        this.violationRecordService = violationRecordService;
        this.studentCourseListService = studentCourseListService;
        this.personService = personService;
        this.archivesDataService = archivesDataService;
        this.safetyScoreAccountService = safetyScoreAccountService;
    }

    /**
     * 评估安全码颜色（项目部Scoped版本）
     * <p>
     * 三色规则：
     * - 绿（准许进场）：违章&lt;6分 AND 无未处理critical AND 资质有效 AND 培训完成 AND 档案有效（非草稿/非审核中）
     * - 黄（预警/受限）：违章6-11分 OR 资质30天内过期 OR 档案审核中
     * - 红（禁止进场）：违章&gt;=12分 OR 有未处理critical OR 资质过期 OR 未培训 OR 无档案或已归档
     * </p>
     *
     * @param personId 人员 ID（xm_person.id）
     * @param projectId 项目部 ID
     * @return 颜色标识：green/yellow/red
     */
    public String evaluateColorWithProjectScope(Long personId, Long projectId) {
        if (personId == null || projectId == null) {
            return SafetyCode.COLOR_RED;
        }

        // 优先使用安全积分账户的颜色判定
        try {
            String scoreBasedColor = safetyScoreAccountService.getColorByPersonId(personId);
            if (scoreBasedColor != null && !"UNKNOWN".equals(scoreBasedColor)) {
                log.debug("使用安全积分账户颜色: personId={}, color={}", personId, scoreBasedColor);
                // 但仍需检查其他条件
                if (!SafetyCode.COLOR_GREEN.equals(scoreBasedColor)) {
                    // 非绿码直接返回（积分已触发变色）
                    return scoreBasedColor;
                }
            }
        } catch (Exception e) {
            log.warn("获取安全积分账户颜色失败，降级到传统评估: personId={}", personId, e);
        }

        // 1. 获取人员在当前项目部的违章记分
        Integer violationScore = getViolationScore(personId, projectId);

        // 2. 检查是否有未处理的严重违章
        boolean hasPendingCritical = hasCriticalViolation(personId, projectId);

        // 3. 检查资质状态
        QualificationStatus qualStatus = checkQualificationStatus(personId, projectId);

        // 4. 检查培训状态
        boolean trainingCompleted = checkTrainingStatus(personId, projectId);

        // 5. 检查档案状态
        ArchiveStatus archiveStatus = checkArchivesStatus(personId, projectId);

        // ========== 红色规则（最高优先级）==========
        // 违章>=12
        if (violationScore != null && violationScore >= 12) {
            return SafetyCode.COLOR_RED;
        }
        // 有未处理critical
        if (hasPendingCritical) {
            return SafetyCode.COLOR_RED;
        }
        // 资质过期
        if (qualStatus == QualificationStatus.EXPIRED) {
            return SafetyCode.COLOR_RED;
        }
        // 未培训
        if (!trainingCompleted) {
            return SafetyCode.COLOR_RED;
        }
        // 无档案或已归档
        if (archiveStatus == ArchiveStatus.NO_ARCHIVE || archiveStatus == ArchiveStatus.ARCHIVED) {
            return SafetyCode.COLOR_RED;
        }

        // ========== 黄色规则 ==========
        // 违章6-11
        if (violationScore != null && violationScore >= 6) {
            return SafetyCode.COLOR_YELLOW;
        }
        // 资质30天内过期
        if (qualStatus == QualificationStatus.EXPIRING_SOON) {
            return SafetyCode.COLOR_YELLOW;
        }
        // 档案审核中
        if (archiveStatus == ArchiveStatus.PENDING) {
            return SafetyCode.COLOR_YELLOW;
        }

        // ========== 绿色（默认）==========
        return SafetyCode.COLOR_GREEN;
    }

    /**
     * 评估安全码颜色（用户ID版本，仅用于向后兼容）
     *
     * @param userId 用户 ID
     * @param safetyTrainingPassed 安全培训是否合格（可选，若为 null 则检查培训记录）
     * @return 颜色标识：green/yellow/red
     * @deprecated 使用 {@link #evaluateColor(Long, Long)} 替代
     */
    @Deprecated
    public String evaluateColor(Long userId, Boolean safetyTrainingPassed) {
        if (userId == null) {
            return SafetyCode.COLOR_RED;
        }

        // 获取personId
        Person person = personService.getByUserId(userId);
        if (person == null) {
            return SafetyCode.COLOR_RED;
        }

        // 使用第一个关联项目（兼容旧逻辑）
        Long projectId = null;
        List<PersonArchivesData> archives = archivesDataService.getByPersonIdAndWorkType(person.getId(), null);
        if (archives != null && !archives.isEmpty()) {
            projectId = archives.get(0).getProjectId();
        }

        if (projectId == null) {
            // 降级：使用旧逻辑
            return evaluateColorLegacy(userId, safetyTrainingPassed);
        }

        return evaluateColorWithProjectScope(person.getId(), projectId);
    }

    /**
     * 旧版评估逻辑（仅基于userId，无项目部概念）
     */
    @Deprecated
    private String evaluateColorLegacy(Long userId, Boolean safetyTrainingPassed) {
        // 1. 检查违章记分
        Integer totalScore = violationRecordService.getTotalScore(userId);
        if (totalScore != null && totalScore >= 12) {
            return SafetyCode.COLOR_RED;
        }

        // 检查是否有任何严重违章 (Critical)
        boolean hasCriticalViolation = violationRecordService.listByUserId(userId).stream()
                .anyMatch(v -> "critical".equals(v.getSeverity()));
        if (hasCriticalViolation) {
            return SafetyCode.COLOR_RED;
        }

        // 2. 检查一人一档 (OPOA) 档案完整性
        Person person = personService.getByUserId(userId);
        boolean hasPendingArchive = false;
        if (person == null) {
            return SafetyCode.COLOR_RED;
        }

        List<PersonArchivesData> archiveDataList = archivesDataService.getByPersonIdAndWorkType(person.getId(), null);
        if (archiveDataList == null || archiveDataList.isEmpty()) {
            return SafetyCode.COLOR_RED;
        }

        boolean hasApprovedArchive = archiveDataList.stream()
                .anyMatch(a -> "approved".equals(a.getStatus()) || "archived".equals(a.getStatus()));

        if (!hasApprovedArchive) {
            hasPendingArchive = archiveDataList.stream().anyMatch(a -> "pending".equals(a.getStatus()));
            if (!hasPendingArchive) {
                return SafetyCode.COLOR_RED;
            }
        }

        // 3. 检查培训状态
        if (safetyTrainingPassed != null && !safetyTrainingPassed) {
            return SafetyCode.COLOR_RED;
        }

        // 4. 检查资质证件
        List<Qualification> qualifications = qualificationService.listByUserId(userId);
        boolean hasExpiring = false;
        if (qualifications != null && !qualifications.isEmpty()) {
            for (Qualification qual : qualifications) {
                if ("expired".equals(qual.getStatus()) || isExpired(qual.getExpiryDate())) {
                    return SafetyCode.COLOR_RED;
                }
                if ("expiring".equals(qual.getStatus()) || isExpiringSoon(qual.getExpiryDate())) {
                    hasExpiring = true;
                }
            }
        }

        // 5. 判定黄色码
        if ((totalScore != null && totalScore >= 6) || hasExpiring || hasPendingArchive) {
            return SafetyCode.COLOR_YELLOW;
        }

        // 6. 检查培训是否完成
        if (safetyTrainingPassed == null) {
            Integer completionCount = studentCourseListService.getTotalCourseCount(person.getId(), 3);
            if (completionCount == null || completionCount <= 0) {
                return SafetyCode.COLOR_YELLOW;
            }
        }

        return SafetyCode.COLOR_GREEN;
    }

    /**
     * 实现向下兼容的评估接口
     * @deprecated 使用 {@link #evaluateColor(Long, Long)} 替代
     */
    @Deprecated
    public String evaluateColor(Long userId, Integer violationScore, Boolean safetyTrainingPassed) {
        return evaluateColor(userId, safetyTrainingPassed);
    }

    /**
     * 评估安全码颜色（简化版，仅基于违章分数）
     *
     * @param violationScore 违章扣分
     * @return 颜色标识：green/yellow/red
     */
    public String evaluateColorByScore(Integer violationScore) {
        if (violationScore == null || violationScore <= 0) {
            return SafetyCode.COLOR_GREEN;
        } else if (violationScore < 6) {
            return SafetyCode.COLOR_YELLOW;
        } else {
            return SafetyCode.COLOR_RED;
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取人员在指定项目部的违章记分
     * <p>
     * 使用 listPage 查询该人员在当前项目部的所有违章记录并累计分数
     * </p>
     *
     * @param personId 人员 ID
     * @param projectId 项目部 ID
     * @return 累计违章分数（不含pending状态的申诉中记录）
     */
    private Integer getViolationScore(Long personId, Long projectId) {
        // 获取userId用于查询
        Long userId = getUserIdByPersonId(personId);
        if (userId == null) {
            return 0;
        }

        // 使用 listPage 查询该项目部下该人员的所有违章
        List<ViolationRecord> violations = violationRecordService.listPage(userId, projectId, null, null, null);

        if (violations == null || violations.isEmpty()) {
            return 0;
        }

        // 累计分数（排除appealed状态的申诉中记录）
        return violations.stream()
                .filter(v -> v.getScore() != null)
                .filter(v -> !"appealed".equals(v.getStatus())) // 申诉中的不计入
                .mapToInt(ViolationRecord::getScore)
                .sum();
    }

    /**
     * 检查是否有未处理的严重违章
     * <p>
     * 严重违章（critical）且状态为 pending（未处理）则返回 true
     * </p>
     *
     * @param personId 人员 ID
     * @param projectId 项目部 ID
     * @return true-有未处理critical，false-无
     */
    private boolean hasCriticalViolation(Long personId, Long projectId) {
        Long userId = getUserIdByPersonId(personId);
        if (userId == null) {
            return false;
        }

        // 查询该项目部下该人员的严重违章
        List<ViolationRecord> violations = violationRecordService.listPage(userId, projectId, "critical", null, null);

        if (violations == null || violations.isEmpty()) {
            return false;
        }

        // 检查是否有 pending 状态的 critical 违章
        return violations.stream()
                .anyMatch(v -> "pending".equals(v.getStatus()));
    }

    /**
     * 根据人员ID获取用户ID
     */
    private Long getUserIdByPersonId(Long personId) {
        if (personId == null) {
            return null;
        }
        Person person = personService.getById(personId);
        return person != null ? person.getUserId() : null;
    }

    /**
     * 资质状态枚举
     */
    private enum QualificationStatus {
        VALID,           // 有效
        EXPIRING_SOON,    // 30天内过期
        EXPIRED           // 已过期
    }

    /**
     * 检查资质状态
     * <p>
     * 检查该人员在当前项目部的资质证件状态
     * </p>
     *
     * @param personId 人员 ID
     * @param projectId 项目部 ID
     * @return 资质状态
     */
    private QualificationStatus checkQualificationStatus(Long personId, Long projectId) {
        Long userId = getUserIdByPersonId(personId);
        if (userId == null) {
            return QualificationStatus.EXPIRED; // 找不到用户，视为无资质
        }

        List<Qualification> qualifications = qualificationService.listByUserId(userId);

        if (qualifications == null || qualifications.isEmpty()) {
            return QualificationStatus.EXPIRED; // 无资质 → 红
        }

        boolean hasExpiring = false;
        for (Qualification qual : qualifications) {
            String status = qual.getStatus();
            LocalDate expiryDate = qual.getExpiryDate();

            // 资质过期 → 红
            if (Qualification.STATUS_EXPIRED.equals(status) || isExpired(expiryDate)) {
                return QualificationStatus.EXPIRED;
            }

            // 资质30天内过期 → 黄
            if (Qualification.STATUS_EXPIRING.equals(status) || isExpiringSoon(expiryDate)) {
                hasExpiring = true;
            }
        }

        return hasExpiring ? QualificationStatus.EXPIRING_SOON : QualificationStatus.VALID;
    }

    /**
     * 检查培训状态
     * <p>
     * 检查该人员是否已完成安全培训（type=3的结业课程）
     * </p>
     *
     * @param personId 人员 ID
     * @param projectId 项目部 ID（当前未使用，预留）
     * @return true-已完成培训，false-未完成
     */
    private boolean checkTrainingStatus(Long personId, Long projectId) {
        if (personId == null) {
            return false;
        }

        // 查询结业课程数 (type 3)
        Integer completionCount = studentCourseListService.getTotalCourseCount(personId, 3);

        return completionCount != null && completionCount > 0;
    }

    /**
     * 档案状态枚举
     */
    private enum ArchiveStatus {
        APPROVED,    // 已审批通过（有效）
        PENDING,     // 审核中（预警）
        ARCHIVED,    // 已归档（禁止）
        NO_ARCHIVE   // 无档案（禁止）
    }

    /**
     * 检查档案状态
     * <p>
     * 检查该人员在当前项目部的一人一档状态
     * </p>
     *
     * @param personId 人员 ID
     * @param projectId 项目部 ID
     * @return 档案状态
     */
    private ArchiveStatus checkArchivesStatus(Long personId, Long projectId) {
        if (personId == null) {
            return ArchiveStatus.NO_ARCHIVE;
        }

        List<PersonArchivesData> archiveDataList = archivesDataService.getByPersonIdAndWorkType(personId, null);

        if (archiveDataList == null || archiveDataList.isEmpty()) {
            return ArchiveStatus.NO_ARCHIVE; // 无档案 → 红
        }

        // 按状态分类
        boolean hasApproved = archiveDataList.stream()
                .anyMatch(a -> "approved".equals(a.getStatus()));
        boolean hasPending = archiveDataList.stream()
                .anyMatch(a -> "pending".equals(a.getStatus()));
        boolean hasArchived = archiveDataList.stream()
                .anyMatch(a -> "archived".equals(a.getStatus()));

        // 已归档 → 红（即使有审批通过的，归档也意味着不能再进场）
        if (hasArchived) {
            return ArchiveStatus.ARCHIVED;
        }

        // 无档案或仅有草稿
        if (!hasApproved && !hasPending) {
            return ArchiveStatus.NO_ARCHIVE;
        }

        // 有审批通过 → 绿（需结合其他条件）
        if (hasApproved) {
            return ArchiveStatus.APPROVED;
        }

        // 有待审核 → 黄
        if (hasPending) {
            return ArchiveStatus.PENDING;
        }

        // 理论上不会走到这里（前面已覆盖所有情况），但以防万一
        return ArchiveStatus.NO_ARCHIVE;
    }

    /**
     * 检查资质是否即将过期（30 天内）
     *
     * @param expiryDate 到期日期
     * @return true-即将过期，false-未即将过期
     */
    public boolean isExpiringSoon(LocalDate expiryDate) {
        if (expiryDate == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        LocalDate threshold = now.plusDays(30);

        return expiryDate.isAfter(now) && expiryDate.isBefore(threshold);
    }

    /**
     * 检查资质是否已过期
     *
     * @param expiryDate 到期日期
     * @return true-已过期，false-未过期
     */
    public boolean isExpired(LocalDate expiryDate) {
        if (expiryDate == null) {
            return false;
        }

        return expiryDate.isBefore(LocalDate.now());
    }
}
