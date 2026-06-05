package com.joyfishs.dawa.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.project.entity.ProjectTestPaperTactics;
import com.joyfishs.dawa.project.mapper.ProjectTestPaperTacticsMapper;

/**
 *  组卷策略详情
 * @author ykfnb
 */
@Service
public class ProjectTestPaperTacticsService extends ServiceImpl<ProjectTestPaperTacticsMapper, ProjectTestPaperTactics> {

	/**
	 * 删除项目下所有组卷策略（硬删除）
	 * @param projectId 项目ID
	 */
	public void removeByProjectId(Long projectId) {
		baseMapper.removeByProjectId(projectId);
	}

	/**
	 * 查询项目下的组卷策略详情
	 * @param id
	 * @return
	 */
	public List<ProjectTestPaperTactics> getByProjectId(Long id) {
		return baseMapper.getByProjectId(id);
	}
}




