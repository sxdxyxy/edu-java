package com.joyfishs.dawa.course.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.course.entity.CourseTag;

/**
 * @Author xiaodai
 * @create 2021/11/15 19:02
 */

@Mapper
public interface CourseTagMapper extends BaseMapper<CourseTag> {

    @Select("UPDATE xm_course_tag SET is_delete=1 WHERE course_id = #{courseId}")
    void getUpdateByCourseId(@Param("courseId") Long courseId);

    @Select("SELECT * FROM xm_course_tag WHERE course_id=#{courseId} and is_delete=0")
    List<CourseTag> selectListByCourseId(@Param("courseId") Long id);
}
