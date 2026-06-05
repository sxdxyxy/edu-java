package com.joyfishs.dawa.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.project.entity.ProjectRelate;
import com.joyfishs.dawa.project.mapper.ProjectRelateMapper;
import com.joyfishs.dawa.student.domain.StudentCourseCatalogue;

import cn.hutool.core.util.ObjUtil;

/**
* 项目关联服务
 * 包含  课程，人员
 * @author ykfnb
 */
@Service
public class ProjectRelateService extends ServiceImpl<ProjectRelateMapper, ProjectRelate> {

	/**
	 * 删除项目下的所有关联数据  （硬性删除）
	 * @param projectId
	 */
	public void removeByProjectId(Long projectId) {
		baseMapper.removeByProjectId(projectId);
	}

	/**
	 * 查询项目下所有关联数据
	 * @param id
	 * @return
	 */
	public List<ProjectRelate> getByProjectId(Long id) {
		return baseMapper.getByProjectId(id);
	}

	/**
	 * 查询项目课程关联数据
	 * @param id
	 * @return
	 */
	public List<ProjectRelate> getCourseByProjectId(Long id) {
		return baseMapper.getCourseByProjectId(id);
	}

	/** 查询项目下的课程目录
	 * @param projectId
	 * @return
	 */
	public List<StudentCourseCatalogue>  getCourseList(Long projectId) {
		return baseMapper.getCourseList(projectId);
	}
	public void moveUp(Long id) {
		//获取要上移的那条数据的信息
		ProjectRelate pr = baseMapper.selectById(id);

		//查询上一条记录
		ProjectRelate casePrev = baseMapper.moveUp(pr.getProjectId(),pr.getSort());

		//最上面的记录不能上移
		if (ObjUtil.isNull(casePrev)) {
			return;
		}
		//交换两条记录的sort值
		Integer temp = pr.getSort();
		pr.setSort(casePrev.getSort());
		casePrev.setSort(temp);

		//更新到数据库
		baseMapper.updateById(pr);
		baseMapper.updateById(casePrev);
	}
	public void moveDown(Long id) {
		//获取要下移的那条数据的信息
		ProjectRelate previousCase = baseMapper.selectById(id);

		//查询下一条记录
		ProjectRelate caseNext = baseMapper.moveDown(previousCase.getProjectId(),previousCase.getSort());

		//最下面的记录不能下移
		if (ObjUtil.isNull(caseNext )) {
			return;
		}
		//交换两条记录的sort值
		Integer temp = previousCase.getSort();
		previousCase.setSort(caseNext.getSort());
		caseNext.setSort(temp);

		//更新到数据库
		baseMapper.updateById(previousCase);
		baseMapper.updateById(caseNext);
	}

	public void removeByCourseId(Long courseId) {
		baseMapper.removeByCourseId(courseId.toString());
	}
}
