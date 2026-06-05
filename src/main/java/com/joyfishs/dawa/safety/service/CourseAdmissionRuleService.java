package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.entity.CourseAdmissionRule;
import com.joyfishs.dawa.safety.mapper.CourseAdmissionRuleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程准入规则服务类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Service
public class CourseAdmissionRuleService extends ServiceImpl<CourseAdmissionRuleMapper, CourseAdmissionRule> {

    /**
     * 规则类型常量
     */
    public static final String RULE_TYPE_SAFETY_SCORE = "safety_score";
    public static final String RULE_TYPE_SPECIAL_CERT = "special_cert";
    public static final String RULE_TYPE_TRAINING_COMPLETED = "training_completed";

    /**
     * 根据课程ID获取所有启用的规则
     */
    public List<CourseAdmissionRule> getEnabledRulesByCourseId(Long courseId) {
        return baseMapper.selectByCourseId(courseId);
    }

    /**
     * 根据课程ID获取所有规则
     */
    public List<CourseAdmissionRule> getAllRulesByCourseId(Long courseId) {
        return baseMapper.selectAllByCourseId(courseId);
    }

    /**
     * 获取课程的安全积分规则
     */
    public CourseAdmissionRule getSafetyScoreRule(Long courseId) {
        List<CourseAdmissionRule> rules = getEnabledRulesByCourseId(courseId);
        return rules.stream()
                .filter(r -> RULE_TYPE_SAFETY_SCORE.equals(r.getRuleType()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取课程的特种证书规则
     */
    public List<CourseAdmissionRule> getSpecialCertRules(Long courseId) {
        List<CourseAdmissionRule> rules = getEnabledRulesByCourseId(courseId);
        return rules.stream()
                .filter(r -> RULE_TYPE_SPECIAL_CERT.equals(r.getRuleType()))
                .toList();
    }

    /**
     * 获取课程的前置培训规则
     */
    public List<CourseAdmissionRule> getTrainingCompletedRules(Long courseId) {
        List<CourseAdmissionRule> rules = getEnabledRulesByCourseId(courseId);
        return rules.stream()
                .filter(r -> RULE_TYPE_TRAINING_COMPLETED.equals(r.getRuleType()))
                .toList();
    }
}
