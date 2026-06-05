package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.mapper.CourseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 再培训课程匹配服务
 * <p>
 * 违章触发再培训时，自动从现有培训计划中匹配安全再培训课程
 * </p>
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Slf4j
@Service
public class CourseMatchingService {

    @Autowired
    @Lazy
    private CourseMapper courseMapper;

    /**
     * 匹配再培训课程
     * <p>
     * 策略：按课程名称包含"安全"或"再培训"关键词，优先选结业类型课程
     * </p>
     *
     * @param personWorkType 人员工种编码（1-19）
     * @return 匹配到的课程，未找到返回 null
     */
    public Course matchRetrainingCourse(Integer personWorkType) {
        log.info("匹配再培训课程: personWorkType={}", personWorkType);

        // 1. 优先查找名称含"安全再培训"的课程
        Course course = findByKeyword("安全再培训");
        if (course != null) {
            log.info("匹配到安全再培训课程: courseId={}, name={}", course.getId(), course.getCourseName());
            return course;
        }

        // 2. 查找名称含"安全"的课程
        course = findByKeyword("安全");
        if (course != null) {
            log.info("匹配到安全课程: courseId={}, name={}", course.getId(), course.getCourseName());
            return course;
        }

        // 3. 查找名称含"再培训"的课程
        course = findByKeyword("再培训");
        if (course != null) {
            log.info("匹配到再培训课程: courseId={}, name={}", course.getId(), course.getCourseName());
            return course;
        }

        // 4. 降级：查找任意课程（至少保证能创建培训记录）
        course = findAnyActiveCourse();
        if (course != null) {
            log.warn("未找到安全再培训课程，降级使用任意课程: courseId={}, name={}", course.getId(), course.getCourseName());
            return course;
        }

        log.warn("未匹配到任何课程: personWorkType={}", personWorkType);
        return null;
    }

    /**
     * 按关键词查找课程（名称模糊匹配）
     */
    private Course findByKeyword(String keyword) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword)
               .orderByDesc("id")
               .last("LIMIT 1");
        List<Course> list = courseMapper.selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 降级：查找任意有效课程
     */
    private Course findAnyActiveCourse() {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id")
               .last("LIMIT 1");
        List<Course> list = courseMapper.selectList(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }
}
