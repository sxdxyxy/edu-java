package com.joyfishs.dawa.student.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.dawa.course.service.CourseCoursewareService;
import com.joyfishs.dawa.student.entity.ProjectPersonStudyRecord;
import com.joyfishs.dawa.student.mapper.ProjectPersonStudyRecordMapper;
import com.joyfishs.system.enums.YesOrNoState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 * @description: 学习记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectPersonStudyRecordService extends ServiceImpl<ProjectPersonStudyRecordMapper, ProjectPersonStudyRecord> {

	private final CourseCoursewareService courseCoursewareService;
	private final PersonCourseRelateService personCourseRelateService;

	public void add(ProjectPersonStudyRecord record){
		this.save(record);
	}

	/**
	 * 保存学习通过记录
	 * @param personId
	 * @param projectId
	 * @param courseId
	 * @param coursewareId
	 * @param type type:1-课中检测  2-直接增加
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateOrSaveData(Long personId, Long projectId, Long courseId, Long coursewareId, Integer type, Long userId) {
		Integer num = baseMapper.getRecordCount(personId, projectId, courseId,coursewareId);
		if(num > 0){
			return false;
		}

		Courseware courseware = courseCoursewareService.getById(coursewareId);
		//添加学习通过记录
		ProjectPersonStudyRecord studyRecord = new ProjectPersonStudyRecord();
		studyRecord.setProjectId(projectId)
				.setPersonId(personId)
				.setCourseId(courseId)
				.setCoursewareId(coursewareId)
				.setStatus(2)
				.setStudyHours(type == 1 ? courseware.getLearnHours() : BigDecimal.ZERO)
				.setCredit(type == 1 ? courseware.getLearnScore() : BigDecimal.ZERO);

		studyRecord.setCreateBy(userId);
		studyRecord.setCreateTime(new Date());
		studyRecord.setIsDelete(YesOrNoState.NO.getState());

		this.save(studyRecord);
		//如果已经添加过学习记录，就不更新学时学分了
		personCourseRelateService.updateLearnHours(type, projectId, personId, coursewareId);
		return true;
	}

	/**
	 * 查询已得到课时
	 *
	 * @param projectId
	 * @param personId
	 * @return
	 */
	public BigDecimal getObtainHours(Long projectId, Long personId) {
		return baseMapper.getObtainHours(projectId, personId);
	}
}
