package com.joyfishs.dawa.course.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.project.domain.vo.ProjectCourseList;

/**
 * <p>
 * 用户课程基本信息表 Mapper 接口
 * </p>
 *
 * @author xiaodai
 * @since 2021-08-17
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    List<Course> getCourseList(@Param("t") Course t);

    @Select("SELECT * FROM xm_course WHERE third_party_id = #{thirdPartyId} LIMIT 1")
    Course selectByThirdPartyId(@Param("thirdPartyId") Long thirdPartyId);

    /**
     * 通过ID列表查询所有课程
     *
     * @param courseIdList
     * @return
     */
    List<ProjectCourseList> getListByIds(@Param("list") List<String> courseIdList, @Param("courseName") String courseName);

    List<ProjectCourseList> getListByProjectId(@Param("projectId") Long projectId);

    /**
     * 分类查询
     */
    List<Course> getCourseListByClass(@Param("classType") Integer classType,
                                      @Param("name") String name,
                                      @Param("tags") List<Integer> tags,
                                      @Param("type") Integer type);

}
