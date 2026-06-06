package com.joyfishs.dawa.violation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safety.entity.AppealRecord;
import com.joyfishs.dawa.safety.service.AppealRecordService;
import com.joyfishs.dawa.safety.service.SafetyPostDeductService;
import com.joyfishs.dawa.safety.service.SafetyRetrainingService;
import com.joyfishs.dawa.safety.service.SafetyScoreService;
import com.joyfishs.dawa.safety.service.ViolationTypeConfigService;
import com.joyfishs.dawa.safety.service.WorkTypeMappingService;
import com.joyfishs.dawa.safety.service.SafetyScoreAccountService;
import com.joyfishs.dawa.safety.service.SafetyNotificationService;
import com.joyfishs.dawa.violation.dto.ViolationAndDeductResult;
import com.joyfishs.dawa.violation.dto.ViolationRecordResult;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import com.joyfishs.dawa.violation.mapper.ViolationRecordMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.mapper.ProjectMapper;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.mapper.SysUserMapper;
import com.joyfishs.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 违章记录服务类
 *
 * @author safe-edu
 * @since 2026-03-29
 */
@Slf4j
@Service
public class ViolationRecordService extends ServiceImpl<ViolationRecordMapper, ViolationRecord> {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    @Lazy
    private PersonService personService;

    @Autowired
    @Lazy
    private ViolationTypeConfigService violationTypeConfigService;

    @Autowired
    @Lazy
    private SafetyCodeService safetyCodeService;

    @Autowired
    @Lazy
    private SafetyScoreService safetyScoreService;

    @Autowired
    @Lazy
    private SafetyRetrainingService retrainingService;

    @Autowired
    @Lazy
    private SafetyPostDeductService postDeductService;

    @Autowired
    @Lazy
    private WorkTypeMappingService workTypeMappingService;

    @Autowired
    @Lazy
    private SafetyNotificationService notificationService;

    @Autowired
    @Lazy
    private AppealRecordService appealRecordService;
    /**
     * 根据用户 ID 查询违章记录
     */
    public List<ViolationRecord> listByUserId(Long userId) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("created_at");
        return list(wrapper);
    }

    /**
     * 根据用户 ID 和项目 ID 查询违章记录
     */
    public List<ViolationRecord> listByUserIdAndProject(Long userId, Long projectId) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
        wrapper.orderByDesc("created_at");
        return list(wrapper);
    }

    /**
     * 根据项目 ID 查询违章记录
     */
    public List<ViolationRecord> listByProjectId(Long projectId) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
               .orderByDesc("created_at");
        return list(wrapper);
    }

    /**
     * 查询用户的累计记分
     */
    public Integer getTotalScore(Long userId) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("status", "processed")
               .select("SUM(score)");
        ViolationRecord result = getOne(wrapper);
        return result != null ? result.getScore() : 0;
    }

    /**
     * 查询用户在某项目的累计记分
     */
    public Integer getTotalScore(Long userId, Long projectId) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("status", "processed");
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
        wrapper.select("SUM(score)");
        ViolationRecord result = getOne(wrapper);
        return result != null ? result.getScore() : 0;
    }

    /**
     * 记录违章行为
     */
    @Transactional(rollbackFor = Exception.class)
    public ViolationRecordResult recordViolation(ViolationRecord violation) {
        violation.setStatus("pending");
        violation.setCreatedAt(new Date());

        // 自动设置 org_id：从关联的项目获取 org_id
        if (violation.getOrgId() == null && violation.getProjectId() != null) {
            Project project = projectMapper.selectById(violation.getProjectId());
            if (project != null && project.getOrgId() != null) {
                violation.setOrgId(project.getOrgId());
            }
        }

        // 根据违章类型自动设置记分（如果未设置）
        if (violation.getScore() == null && violation.getViolationType() != null) {
            Integer deductScore = getDeductScoreByViolationType(violation.getViolationType());
            violation.setScore(deductScore);
        }

        save(violation);

        ScoreChangeResult scoreResult = null;
        // 记录违章后，自动扣减安全积分
        if (violation.getUserId() != null && violation.getViolationType() != null) {
            try {
                scoreResult = safetyScoreService.deductScore(
                    violation.getUserId(),
                    violation.getViolationType(),
                    SecurityUtil.getUserId()
                );
                if (scoreResult != null) {
                    log.info("安全积分扣减完成: userId={}, violationType={}, afterScore={}, color={}, needTraining={}, retrainingRecordId={}",
                        violation.getUserId(),
                        violation.getViolationType(),
                        scoreResult.getAfterScore(),
                        scoreResult.getColor(),
                        scoreResult.getNeedTraining(),
                        scoreResult.getRetrainingRecordId());
                }
            } catch (Exception e) {
                log.error("安全积分扣减失败: userId={}, violationType={}", violation.getUserId(), violation.getViolationType(), e);
            }
        }

        // 触发该项目安全码重评估
        if (violation.getUserId() != null && violation.getProjectId() != null) {
            safetyCodeService.reevaluateColor(violation.getUserId(), violation.getProjectId(), null, null);
        }

        return new ViolationRecordResult(violation, scoreResult);
    }

    // ==================== 违章即扣分（Phase 2 核心方法）====================

    /**
     * 违章即扣分（原子操作）
     * <p>
     * 违章录入的同时自动执行扣分，所有后续影响并行触发：
     * - 自动扣分
     * - 闸机联动（红码禁止通行）
     * - 生成再培训记录（如需要）
     * - 锁定课程准入
     * - 推送通知
     * - 刷新安全码颜色
     * </p>
     *
     * @param personId 人员ID（xm_person.id）
     * @param projectId 项目ID
     * @param violationCode 违章代码
     * @param description 违章描述
     * @param evidencePhotos 证据照片URL列表（JSON）
     * @param location 违章地点
     * @param operatorId 操作人ID（录入人）
     * @return 完整处理结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ViolationAndDeductResult recordViolationAndDeduct(
            Long personId,
            Long projectId,
            String violationCode,
            String description,
            String evidencePhotos,
            String location,
            Long operatorId) {
        log.info("违章即扣分: personId={}, projectId={}, violationCode={}", personId, projectId, violationCode);

        // 1. 获取违章类型配置（获取扣分分值和是否触发培训）
        var config = violationTypeConfigService.getByViolationCode(violationCode);
        if (config == null) {
            log.warn("违章类型不存在: {}", violationCode);
            return ViolationAndDeductResult.error("违章类型不存在: " + violationCode);
        }

        // 2. 获取人员信息（用于填充违章记录）
        Person person = personService.getById(personId);
        if (person == null) {
            return ViolationAndDeductResult.error("人员不存在: personId=" + personId);
        }


        // 4. 构建违章记录
        ViolationRecord violation = new ViolationRecord();
        violation.setPersonId(personId);
        violation.setUserId(person.getUserId());
        violation.setProjectId(projectId);
        violation.setViolationType(violationCode);
        violation.setSeverity(config.getViolationLevel());
        violation.setDeductAmount(config.getDeductScore());
        violation.setScore(config.getDeductScore());
        violation.setDescription(description);
        violation.setEvidencePhotos(evidencePhotos);
        violation.setLocation(location);
        violation.setOperatorId(operatorId);
        violation.setStatus("pending");
        violation.setCreatedAt(new Date());

        // 自动设置 org_id
        if (violation.getOrgId() == null && projectId != null) {
            Project project = projectMapper.selectById(projectId);
            if (project != null && project.getOrgId() != null) {
                violation.setOrgId(project.getOrgId());
            }
        }

        // 5. 保存违章记录
        save(violation);
        log.info("违章记录已保存: id={}, personId={}", violation.getId(), personId);

        // 6. 执行扣分
        ScoreChangeResult scoreResult = safetyScoreService.deductScore(personId, violationCode, operatorId);
        if (scoreResult == null) {
            log.error("扣分失败，回滚违章记录: violationId={}", violation.getId());
            throw new RuntimeException("扣分失败，请检查违章类型是否正确");
        }

        log.info("扣分完成: personId={}, deduct={}, afterScore={}, color={}, needTraining={}, retrainingRecordId={}",
                personId, config.getDeductScore(), scoreResult.getAfterScore(), scoreResult.getColor(),
                scoreResult.getNeedTraining(), scoreResult.getRetrainingRecordId());

        // 7. 更新违章记录的扣分分值
        violation.setTriggerRetraining(scoreResult.getNeedTraining());
        if (scoreResult.getRetrainingRecordId() != null) {
            violation.setRetrainingRecordId(scoreResult.getRetrainingRecordId());
        }
        // @Version 乐观锁:并发扣分时,第二个 update 会失败抛 OptimisticLockingFailureException
        try {
            updateById(violation);
        } catch (org.springframework.dao.OptimisticLockingFailureException oe) {
            log.error("乐观锁冲突,扣分结果回填失败: violationId={}, personId={}",
                    violation.getId(), personId, oe);
            // 不阻断后续流程,扣分和安全码计算已经完成
        }

        // 8. 并行触发后续影响
        postDeductService.triggerAll(
                personId,
                projectId,
                config.getViolationName(),
                config.getDeductScore(),
                scoreResult,
                scoreResult.getRetrainingRecordId());

        // 9. 构建结果
        ViolationAndDeductResult result = ViolationAndDeductResult.success(
                violation,
                scoreResult,
                scoreResult.getRetrainingRecordId(),
                scoreResult.getNeedTraining());
        result.setViolationId(violation.getId());

        // 设置人员姓名（非DB字段）
        violation.setPersonName(person.getName());
        violation.setWorkTypeName(workTypeMappingService.getWorkTypeName(person.getWorkType()));

        log.info("违章即扣分完成: violationId={}, personId={}, color={}, retrainingRecordId={}",
                violation.getId(), personId, scoreResult.getColor(), scoreResult.getRetrainingRecordId());

        return result;
    }

    /**
     * 根据违章类型获取扣分数值
     */
    private Integer getDeductScoreByViolationType(String violationType) {
        // 使用违章类型代码映射扣分
        switch (violationType) {
            case "NO_HELMET":
            case "NO_SAFETY_BELT":
            case "TRAINING_ABSENT":
            case "NO_PROTECTIVE_EQUIPMENT":
            case "SMOKING_IN_NO_SMOKING_AREA":
                return 2;
            case "NO_CERT":
            case "VIOLATION_OPERATION":
            case "UNAUTHORIZED_ENTRY":
            case "HIDING_ACCIDENT":
            case "REFUSING_SAFETY_INSPECTION":
                return 6;
            case "SAFETY_ACCIDENT":
            case "INTENTIONAL_VIOLATION":
            case "DESTROYING_SAFETY_FACILITIES":
            case "DRUNK_WORK":
                return 12;
            default:
                return 1;
        }
    }

    /**
     * 处理违章记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ViolationRecord processViolation(Long violationId, Long handlerId, String status) {
        ViolationRecord violation = getById(violationId);
        if (violation == null) {
            throw new RuntimeException("违章记录不存在");
        }
        
        violation.setHandlerId(handlerId);
        violation.setProcessedAt(new Date());
        violation.setStatus(status);
        updateById(violation);
        
        // 违章处理后，重估用户的安全码
        if (violation.getUserId() != null) {
            safetyCodeService.reevaluateColor(violation.getUserId(), violation.getProjectId(), null, null);
        }
        
        return violation;
    }

    /**
     * 发起申诉（作业人员操作）
     * <p>
     * 申诉不阻断扣分生效，违章状态变为 appealed。
     * 同时在 t_safety_appeal_record 表创建一条申诉记录,供后续审批追溯。
     * </p>
     *
     * @param violationId    违章记录ID
     * @param appealReason  申诉理由
     * @param appealEvidence 申诉补充证据（JSON数组字符串）
     * @return 更新后的违章记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ViolationRecord appealViolation(Long violationId, String appealReason, String appealEvidence) {
        ViolationRecord violation = getById(violationId);
        if (violation == null) {
            throw new RuntimeException("违章记录不存在");
        }

        // 只有 pending 状态的记录可以发起申诉
        if (!"pending".equals(violation.getStatus())) {
            throw new RuntimeException("该记录当前状态不支持申诉");
        }

        // 在 t_safety_appeal_record 表创建申诉记录（service 内部已含 "已存在则拒绝重复提交" 逻辑）
        if (violation.getPersonId() == null) {
            // 老数据没 personId,fallback 用 userId
            log.warn("违章记录缺失 person_id, fallback 用 userId: violationId={}", violationId);
        }
        appealRecordService.createAppeal(
                violationId,
                violation.getPersonId() != null ? violation.getPersonId() : violation.getUserId(),
                appealReason,
                appealEvidence);

        // 申诉中不计入记分，所以也要重新评估安全码
        if (violation.getUserId() != null) {
            safetyCodeService.reevaluateColor(violation.getUserId(), violation.getProjectId(), null, null);
        }

        log.info("申诉发起成功: violationId={}, personId={}, reason={}", violationId, violation.getPersonId(), appealReason);
        return violation;
    }

    /**
     * 审批申诉（安全主管操作）
     * <p>
     * 通过时：返还积分 + 更新违章状态为 appeal_approved + 刷新安全码 + 通知
     * 驳回时：保持违章状态为 appeal_rejected + 通知
     * </p>
     * <p>
     * 实现说明：实际审批逻辑委托给 {@link com.joyfishs.dawa.safety.service.AppealRecordService}，
     * 后者会同步更新 t_safety_appeal_record 表（审批结果/审批人/积分返还时间等）。
     * </p>
     *
     * @param violationId   违章记录ID
     * @param approved     是否通过
     * @param handlerId     审批人ID（安全主管）
     * @param remarks       审批意见
     * @return 更新后的违章记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ViolationRecord reviewAppeal(Long violationId, boolean approved, Long handlerId, String remarks) {
        ViolationRecord violation = getById(violationId);
        if (violation == null) {
            throw new RuntimeException("违章记录不存在");
        }

        if (!"appealed".equals(violation.getStatus())) {
            throw new RuntimeException("该记录不处于申诉状态");
        }

        // 找到对应的 t_safety_appeal_record 记录
        AppealRecord appeal = appealRecordService
                .listByPersonId(violation.getPersonId() != null ? violation.getPersonId() : violation.getUserId())
                .stream()
                .filter(a -> violationId.equals(a.getViolationRecordId()))
                .filter(a -> AppealRecord.RESULT_PENDING.equals(a.getReviewResult()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到对应的待审批申诉记录"));

        // 委托 AppealRecordService 完成审批（更新 t_safety_appeal_record + 返还积分 + 通知）
        appealRecordService.reviewAppeal(appeal.getId(), handlerId, approved, remarks);

        // 重新评估安全码（积分已变更，需刷新颜色）
        if (violation.getUserId() != null) {
            safetyCodeService.reevaluateColor(violation.getUserId(), violation.getProjectId(), null, null);
        }

        // 重新查询违章记录(appealRecordService 已改 status)
        return getById(violationId);
    }

    /**
     * 查询待处理的违章记录
     */
    public List<ViolationRecord> listPending() {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "pending")
               .orderByAsc("created_at");
        return list(wrapper);
    }

    /**
     * 查询严重违章（major 和 critical）
     */
    public List<ViolationRecord> listSevereViolations() {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.in("severity", "major", "critical")
               .orderByDesc("created_at");
        return list(wrapper);
    }
    
    /**
     * 获取违章记分排行榜TOP10
     */
    public List<Map<String, Object>> getTop10ViolatorsByScore(Long orgId) {
        return baseMapper.selectTop10ViolatorsByScore(orgId);
    }
    
    /**
     * 批量删除违章记录
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteViolations(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        return removeBatchByIds(ids);
    }

    /**
     * 批量处理违章
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchProcessViolations(List<Long> ids, Long handlerId, String status) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        List<ViolationRecord> violations = listByIds(ids);
        if (violations == null || violations.isEmpty()) {
            return false;
        }

        for (ViolationRecord violation : violations) {
            violation.setHandlerId(handlerId);
            violation.setProcessedAt(new Date());
            violation.setStatus(status);
        }

        return updateBatchById(violations);
    }

    /**
     * 批量导入违章数据
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ViolationRecord> importViolations(List<ViolationRecord> violations) {
        if (violations == null || violations.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        List<ViolationRecord> results = new java.util.ArrayList<>();
        for (ViolationRecord violation : violations) {
            // 如果设置了ID且数据库中存在，则更新；否则新增
            if (violation.getId() != null && getById(violation.getId()) != null) {
                updateById(violation);
                results.add(violation);
            } else {
                // 设置默认状态和时间
                if (violation.getStatus() == null) {
                    violation.setStatus("pending");
                }
                if (violation.getCreatedAt() == null) {
                    violation.setCreatedAt(new Date());
                }
                save(violation);
                results.add(violation);
            }
        }

        return results;
    }

    /**
     * 分页查询违章记录（带用户信息）
     */
    public List<ViolationRecord> listPageWithUser(Map<String, Object> params) {
        // 获取当前登录用户有权限管理的机构 ID 列表
        java.util.List<Long> managedOrgIds = SecurityUtil.getManagedOrgIds();

        List<ViolationRecord> list = listPage(
            params.containsKey("userId") ? (Long) params.get("userId") : null,
            params.containsKey("projectId") ? (Long) params.get("projectId") : null,
            params.containsKey("severity") ? (String) params.get("severity") : null,
            params.containsKey("status") ? (String) params.get("status") : null,
            managedOrgIds
        );

        // 关联查询用户信息和项目信息
        for (ViolationRecord record : list) {
            if (record.getUserId() != null) {
                SysUser user = sysUserMapper.selectById(record.getUserId());
                if (user != null) {
                    record.setUserName(user.getName());
                    record.setPhone(user.getPhone());
                    record.setIdCardNo(user.getIdCardNo());
                }
            }
            if (record.getProjectId() != null) {
                Project project = projectMapper.selectById(record.getProjectId());
                if (project != null) {
                    record.setProjectName(project.getProjectName());
                }
            }
        }

        // 过滤搜索条件（用户姓名、手机号）
        return list.stream().filter(record -> {
            if (params.containsKey("userName")) {
                String userName = (String) params.get("userName");
                if (record.getUserName() == null || !record.getUserName().contains(userName)) {
                    return false;
                }
            }
            if (params.containsKey("phone")) {
                String phone = (String) params.get("phone");
                if (record.getPhone() == null || !record.getPhone().contains(phone)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * 分页查询违章记录
     */
    public List<ViolationRecord> listPage(Long userId, Long projectId, String severity, String status, java.util.List<Long> managedOrgIds) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        // 显式选择数据库中的字段，排除扩展字段（userName, phone, idCardNo, projectName）
        wrapper.select(
            "id", "user_id", "project_id", "violation_type", "severity", "score",
            "description", "evidence_photos", "location", "handler_id", "processed_at",
            "status", "created_at"
        );
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
        if (severity != null && !severity.trim().isEmpty()) {
            wrapper.eq("severity", severity);
        }
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq("status", status);
        }
        // 如果有权限管理的机构 ID 列表，过滤出这些机构管辖的项目
        if (managedOrgIds != null && !managedOrgIds.isEmpty()) {
            // 先查询这些机构下的项目ID列表
            QueryWrapper<Project> projectWrapper = new QueryWrapper<>();
            projectWrapper.select("id").in("org_id", managedOrgIds);
            List<Project> projectList = projectMapper.selectList(projectWrapper);
            if (projectList != null && !projectList.isEmpty()) {
                List<Long> projectIds = projectList.stream().map(Project::getId).collect(Collectors.toList());
                wrapper.in("project_id", projectIds);
            } else {
                // 如果没有项目，返回空结果
                wrapper.eq("1", "0");
            }
        }
        wrapper.orderByDesc("created_at");
        return list(wrapper);
    }

    /**
     * 统计所有违章记录数
     */
    public long countAll() {
        return count();
    }

    /**
     * 根据状态统计违章记录数
     */
    public long countByStatus(String status) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        return count(wrapper);
    }

    /**
     * 根据严重程度统计违章记录数
     */
    public long countBySeverity(String severity) {
        QueryWrapper<ViolationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("severity", severity);
        return count(wrapper);
    }

    /**
     * 更新违章记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ViolationRecord updateViolation(ViolationRecord violation) {
        if (violation.getId() == null) {
            throw new RuntimeException("违章记录 ID 不能为空");
        }
        updateById(violation);
        return violation;
    }

    /**
     * 统计指定日期的违章数量
     */
    public long countByDate(java.time.LocalDate date, Long orgId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ViolationRecord> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.apply("DATE(created_at) = {0}", date);
        if (orgId != null) {
            wrapper.apply("(SELECT p.org_id FROM xm_project p WHERE p.id = project_id) = {0}", orgId);
        }
        return count(wrapper);
    }

    /**
     * 统计指定类型的违章数量
     */
    public long countByType(String type, Long orgId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ViolationRecord> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ViolationRecord::getViolationType, type);
        if (orgId != null) {
            wrapper.apply("(SELECT p.org_id FROM xm_project p WHERE p.id = project_id) = {0}", orgId);
        }
        return count(wrapper);
    }
}
