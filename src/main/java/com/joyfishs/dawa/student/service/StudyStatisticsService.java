package com.joyfishs.dawa.student.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyfishs.dawa.course.service.CourseRelationCoursewareService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.dawa.student.domain.StudentCourseCatalogue;
import com.joyfishs.dawa.student.domain.vo.StatisticsVo;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 学习统计service
 */
@Slf4j
@Service
public class StudyStatisticsService {

	@Autowired
    ProjectService projectService;
	@Autowired
    StudentCourseCatalogueService catalogueService;
	@Autowired
	private CourseRelationCoursewareService courseRelationCoursewareService;
	@Autowired
	ProjectPersonStudyRecordService personStudyRecordService;

	/**
	 * 查询当前课程的学习情况
	 * @param personId
	 * @param projectId
	 * @return
	 */
	public StatisticsVo getStatistic(Long personId, Long projectId) {
		Project p = projectService.getById(projectId);
		if(p == null) {
            throw new CustomException("课程id有误");
        }
		StatisticsVo vo = new StatisticsVo();
		vo.setProjectId(p.getId());
		vo.setProjectName(p.getProjectName());
		vo.setTrainWay(p.getTrainWay());
		vo.setStartDate(p.getTrainSdate());
		vo.setEndDate(p.getTrainEdate());

		vo.setTotalHours(courseRelationCoursewareService.getTotalHours(projectId));
		vo.setLearnedHours(personStudyRecordService.getObtainHours(projectId,personId));
		vo.setStatus(vo.getTotalHours().intValue()== vo.getLearnedHours().intValue()?"已完成":"学习中");
		//获取课程列表树
		List<StudentCourseCatalogue> list = catalogueService.getCourseTree(projectId,personId);
		vo.setDetails(list);
		return vo;
	}
}
