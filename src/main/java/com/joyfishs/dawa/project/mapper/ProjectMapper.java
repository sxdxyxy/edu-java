package com.joyfishs.dawa.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.project.domain.vo.ProjectList;
import com.joyfishs.dawa.project.domain.vo.ProjectPersonList;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.syscourse.domain.SysRecommendCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Entity gen.domain.XmProject
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 删除项目数据
     *
     * @param idList
     * @param deleteBy
     * @param deleteReason
     */
    void remove(@Param("idList") List<Long> idList, @Param("deleteBy") Long deleteBy, @Param("deleteReason") String deleteReason);

    /**
     * 查询项目列表
     */
    List<ProjectList> queryList(@Param("projectName") String projectName,
                                @Param("status") Integer status,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate,
                                @Param("trainWay") Integer trainWay,
                                @Param("type") Integer type,
                                @Param("userId") Long userId,
                                @Param("orgIds") List<Long> orgIds,
                                @Param("orgId") Long orgId);

    /**
     * 查询人员信息
     */
    @Select("<script>" +
            "select id,name,user_name,org_id from xm_person where is_delete = 0 and id in " +
            "<foreach item='id' collection='idList' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<ProjectPersonList> getPersonList(@Param("idList") List<String> idList);

    /**
     * 将状态改为已发布
     */
    @Update("update xm_project set project_status = 2 where id = #{id}")
    Integer release(@Param("id") Long id);

    /**
     * 将状态改为已中止
     */
    @Update("update xm_project set project_status = 5 where id = #{id}")
    Integer stop(@Param("id") Long id);

    /* 查询系统推荐课程 详情 (lecturer/coverImage fields are placeholders for future feature implementation) */
    List<SysRecommendCourse> findSysRecommendList(@Param("projectId") Long projectId,
                                                  @Param("projectType") Integer projectType,
                                                  @Param("personId") Long personId);

    List<Project> getReminDList(@Param("personId") Long personId);


    int getHourCount(@Param("projectId") Long projectId);

    /**
     * 标记删除培训计划下的项目
     */
    @Update("update xm_project set is_delete = 1,delete_time = now(),delete_by = #{userId} where is_delete = 0 and train_plan_id = #{trainPlanId}")
    void delByTrainPlanId(Long trainPlanId, Long userId);

    /**
     * 调用存储过程
     */
    @Update(" call refresh_project_status() ")
    void refreshProjectStatus();

    @Select("select count(*) from xm_project where is_delete = 0 AND train_class=#{trainClass}")
    int countByTrainClass(@Param("trainClass") Long trainClass);
}
