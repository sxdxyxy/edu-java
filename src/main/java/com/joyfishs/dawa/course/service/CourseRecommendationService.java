package com.joyfishs.dawa.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.mapper.CourseMapper;
import com.joyfishs.dawa.course.entity.RecommendationRule;
import com.joyfishs.dawa.course.mapper.RecommendationRuleMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import com.joyfishs.dawa.violation.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 课程推荐策略引擎 (Course Recommendation Strategy Engine)
 * 基于人员特征进行多维度智能推送
 */
@Service
public class CourseRecommendationService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PersonService personService;

    @Autowired
    private RecommendationRuleMapper ruleMapper;

    @Autowired
    private ViolationRecordService violationService;

    private List<RecommendationRule> activeRules;
    private long lastRulesFetch = 0;

    /** 获取当前启用的规则 */
    private List<RecommendationRule> getEnabledRules() {
        if (activeRules == null || System.currentTimeMillis() - lastRulesFetch > 60000) { // 1分钟缓存
            activeRules = ruleMapper.selectList(new LambdaQueryWrapper<RecommendationRule>().eq(RecommendationRule::getEnabled, 1));
            lastRulesFetch = System.currentTimeMillis();
        }
        return activeRules;
    }

    private boolean isRuleEnabled(String code) {
        return getEnabledRules().stream().anyMatch(r -> r.getRuleCode().equals(code));
    }

    private final Map<Long, List<Course>> userRecommendationCache = new ConcurrentHashMap<>();
    private final Map<Long, Long> userLastRefreshTime = new ConcurrentHashMap<>();
    private static final long ONE_WEEK_MS = 7L * 24L * 60L * 60L * 1000L;

    /**
     * 为单个用户拉取推荐课程列表
     */
    public List<Course> recommendCoursesForUser(Long userId) {
        Long lastRefresh = userLastRefreshTime.get(userId);
        if (lastRefresh != null && System.currentTimeMillis() - lastRefresh < ONE_WEEK_MS) {
            List<Course> cached = userRecommendationCache.get(userId);
            if (cached != null) return cached;
        }

        // 获取人员基本信息
        LambdaQueryWrapper<Person> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Person::getUserId, userId);
        Person person = personService.getOne(wrapper);
        List<Course> recommendations = performStrategy(person);
        
        userRecommendationCache.put(userId, recommendations);
        userLastRefreshTime.put(userId, System.currentTimeMillis());
        
        return recommendations;
    }
    
    /**
     * 为特定学员(Person)拉取推荐课程列表
     */
    public List<Course> recommendCoursesForPersonId(Long personId) {
        Person person = personService.getById(personId);
        if (person == null) return List.of();
        List<ViolationRecord> violations = violationService.listByUserId(person.getUserId());
        return performStrategy(person, violations);
    }

    private List<Course> performStrategy(Person person) {
        if (person == null) {
            return courseMapper.selectList(new LambdaQueryWrapper<Course>());
        }
        List<ViolationRecord> violations = violationService.listByUserId(person.getUserId());
        return performStrategy(person, violations);
    }

    private List<Course> performStrategy(Person person, List<ViolationRecord> violations) {
        // 获取所有在架课程
        List<Course> allCourses = courseMapper.selectList(new LambdaQueryWrapper<Course>());
        if (person == null) {
            return allCourses; // 兜底
        }

        return allCourses.stream()
                .sorted(Comparator.comparingInt(course -> -calculateMatchScore(person, course, violations)))
                .limit(10) // 默认推送最匹配的10门课程
                .collect(Collectors.toList());
    }

    /**
     * 计算单门课程与用户的契合度得分（策略核心）
     */
    private int calculateMatchScore(Person person, Course course, List<ViolationRecord> violations) {
        int score = 0;

        // 1. 岗位匹配 (岗位/工种高度匹配，+50分)
        if (isRuleEnabled("job")) {
            if (course.getJob() != null && course.getJob().equals(person.getWorkType())) {
                score += 50;
            }
        }

        // 2. 项目与单位性质匹配 (本单位规则 - profession/org)
        if (isRuleEnabled("profession") || isRuleEnabled("org")) {
            if (person.getOrgId() != null && person.getOrgId().equals(course.getOrgId())) {
                score += 30; // 优先推本单位创立的课程
            }
        }
        
        // 3. 学历匹配 (degree)
        if (isRuleEnabled("degree")) {
            if (person.getDegreeId() != null) {
                if (person.getDegreeId() > 2 && course.getCourseType() != null && course.getCourseType() == 1) {
                    score += 10;
                }
            }
        }

        // 4. 年龄与性别特点关联 (age, gender)
        if (isRuleEnabled("age")) {
            if (person.getAge() > 50) {
                if (course.getCourseName() != null && course.getCourseName().contains("健康")) {
                    score += 15;
                }
            }
        }
        if (isRuleEnabled("gender")) {
            if (person.getSex() != null && person.getSex() == 2) { // 女性工人
                if (course.getCourseName() != null && course.getCourseName().contains("权益")) {
                    score += 15;
                }
            }
        }

        // 5. 违章记录和隐患 (violation, hidden_danger)
        if (isRuleEnabled("violation") && violations != null && !violations.isEmpty()) {
            // 根据违章严重程度增加相关安全课程权重
            boolean hasCritical = violations.stream().anyMatch(v -> "critical".equals(v.getSeverity()));
            boolean hasMajor = violations.stream().anyMatch(v -> "major".equals(v.getSeverity()));
            
            if (course.getCourseName() != null && 
                (course.getCourseName().contains("违章") || 
                 course.getCourseName().contains("安全规范") || 
                 course.getCourseName().contains("合规"))) {
                score += hasCritical ? 50 : (hasMajor ? 30 : 20);
            }
        }
        
        if (isRuleEnabled("hidden_danger")) {
            // 后续接入隐患排查记录后增加相关课程权重
            if (course.getCourseName() != null && (course.getCourseName().contains("应急") || course.getCourseName().contains("急救"))) {
                score += 20;
            }
        }

        return score;
    }
}
