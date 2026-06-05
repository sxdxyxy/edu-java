package com.joyfishs.dawa.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.project.entity.ProjectTestPaperTactics;

/**
 * @Entity gen.domain.XmProjectTestPaperTactics
 */
@Mapper
public interface ProjectTestPaperTacticsMapper extends BaseMapper<ProjectTestPaperTactics> {

	/**
	 * 删除所有组卷策略
	 * @param projectId
	 */
	@Delete("delete from xm_project_test_paper_tactics where project_id = #{projectId}")
	void removeByProjectId(@Param("projectId") Long projectId);

	/**
	 * 查询项目下的组卷详情
	 * @param projectId
	 * @return
	 */
	@Select(" select id, project_id, topic_num, topic_type, topic_score " +
			" from xm_project_test_paper_tactics " +
			" where project_id = #{id} " +
			"  and is_delete = 0 ")
	List<ProjectTestPaperTactics> getByProjectId(@Param("id") Long projectId);
}




