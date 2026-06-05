package com.joyfishs.dawa.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safety.entity.CourseAdmissionRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程准入规则 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Mapper
public interface CourseAdmissionRuleMapper extends BaseMapper<CourseAdmissionRule> {

    /**
     * 根据课程ID查询所有启用的规则
     */
    @Select("SELECT * FROM t_course_admission_rule WHERE course_id = #{courseId} AND status = 'enabled' ORDER BY sort_order ASC")
    List<CourseAdmissionRule> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程ID查询所有规则（不分状态）
     */
    @Select("SELECT * FROM t_course_admission_rule WHERE course_id = #{courseId} ORDER BY sort_order ASC")
    List<CourseAdmissionRule> selectAllByCourseId(@Param("courseId") Long courseId);
}
