package com.joyfishs.dawa.course.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.course.entity.CourseRelationCourseware;

/**
 *
 */
@Mapper
public interface CourseRelationCoursewareMapper extends BaseMapper<CourseRelationCourseware> {

    /**
     * 通过课件ID 删除关联关系 (软删除)
     * @param id
     */
    @Select("UPDATE xm_course_relation_courseware SET is_delete = 1 WHERE courseware_id = #{coursewareId}")
    void deleteByCoursewareId(@Param("coursewareId") Long id);

    /**
     * 通过课程ID获取课件关联
     * @param id 课程ID
     * @return
     */
    @Select("SELECT courseware_id FROM xm_course_relation_courseware WHERE course_id=#{courseId} and is_delete=0")
    List<Long> getCoursewareRelationByCourseId(@Param("courseId") Long id);

    /** 课程下课件列表 */
    List<Map<String, Object>> getCoursewareList(@Param("courseId") Long courseId,
                                                @Param("projectId") Long projectId,
                                                @Param("personId") Long personId);
    /**
     * 统计课件总课时
     * @param courseId
     * @return
     */
    BigDecimal getTotalHoursForCourse(@Param("courseId") Long courseId);

    long getTotalDuration(@Param("courseId") Long courseId);

    /**
     * 查询项目总课时
     * @param projectId
     * @return
     */
    BigDecimal getTotalHours(@Param("projectId") Long projectId);
}
