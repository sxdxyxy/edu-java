package com.joyfishs.dawa.safety.service;

import com.joyfishs.dawa.qualification.entity.Qualification;
import com.joyfishs.dawa.qualification.service.QualificationService;
import com.joyfishs.dawa.safety.dto.AdmissionCheckResult;
import com.joyfishs.dawa.safety.entity.CourseAdmissionRule;
import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import com.joyfishs.dawa.student.service.StudentCourseListService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程准入检查服务类
 * 检查学员是否符合课程报名条件
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Slf4j
@Service
public class AdmissionCheckService {

    @Autowired
    private CourseAdmissionRuleService courseAdmissionRuleService;

    @Autowired
    @Lazy
    private SafetyScoreAccountService safetyScoreAccountService;

    @Autowired
    @Lazy
    private SafetyScoreService safetyScoreService;

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private PersonService personService;

    @Autowired
    @Lazy
    private SafetyRetrainingService retrainingService;

    @Autowired
    private StudentCourseListService studentCourseListService;

    /**
     * 检查人员是否符合课程报名条件
     *
     * @param personId 人员ID
     * @param courseId 课程ID
     * @return 准入检查结果
     */
    public AdmissionCheckResult checkAdmission(Long personId, Long courseId) {
        log.info("开始课程准入检查: personId={}, courseId={}", personId, courseId);

        AdmissionCheckResult result = new AdmissionCheckResult();
        result.setPassed(true);
        result.setWarnings(new ArrayList<>());

        // 1. 检查是否有未完成的安全再培训
        if (hasPendingRetraining(personId)) {
            result.setPassed(false);
            result.setReason("存在未完成的安全再培训，请先完成培训");
            log.info("准入检查不通过: personId={}, courseId={}, reason=存在未完成的安全再培训", personId, courseId);
            return result;
        }

        // 2. 获取课程准入规则
        List<CourseAdmissionRule> rules = courseAdmissionRuleService.getEnabledRulesByCourseId(courseId);
        if (rules.isEmpty()) {
            log.info("课程无准入规则: courseId={}", courseId);
            return result;
        }

        // 3. 逐项检查规则
        for (CourseAdmissionRule rule : rules) {
            RuleCheckResult ruleResult = checkRule(personId, rule);
            if (!ruleResult.passed) {
                if (rule.getIsMandatory()) {
                    result.setPassed(false);
                    result.setReason(ruleResult.message);
                    log.info("准入检查不通过: personId={}, courseId={}, rule={}, reason={}", personId, courseId, rule.getRuleType(), ruleResult.message);
                    return result;
                } else {
                    result.getWarnings().add(ruleResult.message);
                }
            }
        }

        // 4. 设置安全码信息到结果
        SafetyScoreAccount account = safetyScoreAccountService.getByPersonId(personId);
        if (account != null) {
            result.setCurrentScore(account.getCurrentScore());
            result.setSafetyCodeColor(safetyScoreService.getSafetyCodeColor(personId));
        }

        if (result.getPassed()) {
            log.info("准入检查通过: personId={}, courseId={}", personId, courseId);
        }

        return result;
    }

    /**
     * 检查是否有未完成的安全再培训
     */
    private boolean hasPendingRetraining(Long personId) {
        return retrainingService.hasPendingRetraining(personId);
    }

    /**
     * 检查单条规则
     */
    private RuleCheckResult checkRule(Long personId, CourseAdmissionRule rule) {
        switch (rule.getRuleType()) {
            case CourseAdmissionRuleService.RULE_TYPE_SAFETY_SCORE:
                return checkSafetyScoreRule(personId, rule);
            case CourseAdmissionRuleService.RULE_TYPE_SPECIAL_CERT:
                return checkSpecialCertRule(personId, rule);
            case CourseAdmissionRuleService.RULE_TYPE_TRAINING_COMPLETED:
                return checkTrainingCompletedRule(personId, rule);
            default:
                return new RuleCheckResult(true, null);
        }
    }

    /**
     * 检查安全积分规则
     */
    private RuleCheckResult checkSafetyScoreRule(Long personId, CourseAdmissionRule rule) {
        SafetyScoreAccount account = safetyScoreAccountService.getByPersonId(personId);
        if (account == null) {
            return new RuleCheckResult(false, "未开通安全积分账户");
        }

        String currentColor = safetyScoreService.getSafetyCodeColor(personId);
        String requiredColor = rule.getRuleCondition();

        // GREEN=绿码, YELLOW=黄码, RED=红码
        int currentLevel = getColorLevel(currentColor);
        int requiredLevel = getColorLevel(requiredColor);

        if (currentLevel > requiredLevel) {
            return new RuleCheckResult(false, rule.getErrorMessage() != null ?
                    rule.getErrorMessage() : "安全码状态不符合要求，当前为" + currentColor + "，要求为" + requiredColor);
        }

        return new RuleCheckResult(true, null);
    }

    /**
     * 获取颜色的严重程度数值（数值越大越严重）
     */
    private int getColorLevel(String color) {
        if ("GREEN".equals(color)) return 0;
        if ("YELLOW".equals(color)) return 1;
        if ("RED".equals(color)) return 2;
        return 3; // UNKNOWN 或其他
    }

    /**
     * 检查特种证书规则
     */
    private RuleCheckResult checkSpecialCertRule(Long personId, CourseAdmissionRule rule) {
        String requiredCertType = rule.getRuleValue();
        String certStatus = rule.getRuleCondition(); // cert_valid, cert_expiring, cert_expired

        Long userId = resolveUserIdByPersonId(personId);
        List<Qualification> qualifications = (userId == null)
                ? java.util.Collections.emptyList()
                : qualificationService.listByUserId(userId);
        if (qualifications == null || qualifications.isEmpty()) {
            if ("cert_valid".equals(certStatus) || "cert_expiring".equals(certStatus)) {
                return new RuleCheckResult(false, "缺少必要的特种作业证书");
            }
        }

        // TODO: 实现证书状态检查逻辑

        return new RuleCheckResult(true, null);
    }

    /**
     * 解析 personId → userId. 找不到 person 记录或该 person 未绑定 user 时返回 null(此时按"无资质"处理)
     */
    private Long resolveUserIdByPersonId(Long personId) {
        if (personId == null) {
            return null;
        }
        try {
            Person person = personService.getById(personId);
            return (person == null) ? null : person.getUserId();
        } catch (Exception e) {
            log.warn("resolveUserIdByPersonId 失败: personId={}", personId, e);
            return null;
        }
    }

    /**
     * 检查前置培训完成规则
     */
    private RuleCheckResult checkTrainingCompletedRule(Long personId, CourseAdmissionRule rule) {
        String requiredCourseId = rule.getRuleValue();
        Integer completedCount = studentCourseListService.getTotalCourseCount(personId, 3);

        if (completedCount == null || completedCount <= 0) {
            return new RuleCheckResult(false, rule.getErrorMessage() != null ?
                    rule.getErrorMessage() : "未完成必需的培训课程");
        }

        return new RuleCheckResult(true, null);
    }

    /**
     * 规则检查结果内部类
     */
    private static class RuleCheckResult {
        boolean passed;
        String message;

        RuleCheckResult(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }
    }
}
