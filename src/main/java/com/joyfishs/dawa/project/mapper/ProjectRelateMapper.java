package com.joyfishs.dawa.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.project.entity.ProjectRelate;
import com.joyfishs.dawa.student.domain.StudentCourseCatalogue;

/**
 * @Entity gen.domain.XmProjectRelate
 */
@Mapper
public interface ProjectRelateMapper extends BaseMapper<ProjectRelate> {

    /**
     * 删除项目下所有关联数据
     *
     * @param projectId
     */
    @Delete("delete from xm_project_relate where project_id = #{projectId}")
    void removeByProjectId(@Param("projectId") Long projectId);

    /**
     * 查询项目受训单位和人员关联数据
     *
     * @param projectId
     * @return
     */
    List<ProjectRelate> getByProjectId(@Param("projectId") Long projectId);

    /**
     * 查询项目课程关联数据
     *
     * @param projectId
     * @return
     */
    List<ProjectRelate> getCourseByProjectId(@Param("projectId") Long projectId);

    /**
     * 项目下的课程目录
     */
    List<StudentCourseCatalogue> getCourseList(@Param("projectId") Long projectId);

    /**
     * 上移
     *
     * @param sort
     * @return
     */
    ProjectRelate moveUp(@Param("projectId") Long projectId, @Param("sort") Integer sort);

    /**
     * 下移
     *
     * @param sort
     * @return
     */
    ProjectRelate moveDown(@Param("projectId") Long projectId, @Param("sort") Integer sort);

    /**
     * 根据课程ID删除关联关系
     * @param id 课程ID
     */
    @Delete("delete from xm_project_relate where type = 2 and relate_ids = #{courseId}")
    void removeByCourseId(@Param("courseId") String id);

}
