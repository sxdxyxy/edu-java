package com.joyfishs.dawa.plan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.project.entity.Project;

/**
* @Entity gen.domain.XmProjectDict
*/
@Mapper
public interface TrainPlanMapper extends BaseMapper<TrainPlan> {


	/** 查询培训计划列表 */
	List<TrainPlan> list(@Param("type") Integer type, @Param("orgId") Long orgId, @Param("name") String name, @Param("year") Integer year, @Param("projectId") Long projectId);

	/** 查询培训计划下的课程包含人数 */
	List<Project> getProjectListByPlanId(Long id);

	/** 查询培训计划下的课程包含人数 */
	List<Long> getProjectIdList(Long id);
}
