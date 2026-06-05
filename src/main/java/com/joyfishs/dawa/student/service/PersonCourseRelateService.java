package com.joyfishs.dawa.student.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.dawa.course.service.CourseCoursewareService;
import com.joyfishs.dawa.person.domain.param.PersonListQueryRequest;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.plan.domain.TrainPlanCreateEvent;
import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.student.domain.vo.StudyRecordVo;
import com.joyfishs.dawa.student.entity.PersonCourseRelate;
import com.joyfishs.dawa.student.mapper.PersonCourseRelateMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 人员课程关联服务
 */
@Slf4j
@Service
public class PersonCourseRelateService extends ServiceImpl<PersonCourseRelateMapper, PersonCourseRelate> {

	@Autowired
    CourseCoursewareService coursewareService;

	@Autowired
    PersonService personService;

	/**
	 * 添加到我的课程
	 * @param project
	 * @param personId
	 */
	public PersonCourseRelate createMyCourse(Project project, Long personId) {
		if(project == null || project.getIsDelete() == 1) throw new CustomException("项目未找到或已删除");
		PersonCourseRelate record = this.getRecord(project.getId(),personId);
		if(record == null || record.getId() == null) { //没有的话就创建
			record = new PersonCourseRelate();
			record.setPersonId(personId)
					.setProjectId(project.getId())
					.setIsSign(2)  //并且设置为已签到
					.setIsDelete(YesOrNoState.NO.getState());
			record.setCreateBy(SecurityUtil.getUserId());
			record.setCreateTime(DateUtil.date());
			//this.save(record);
		} else {
			record.setIsSign(2);
			record.setUpdateBy(SecurityUtil.getUserId());
			record.setUpdateTime(new Date());
			//this.updateById(record);
		}
		return record;
	}

	/**
	 * 监听培训计划创建事件自动添加必修课程
	 *
	 */
	@EventListener
	@Transactional(rollbackFor = Exception.class)
	public void autoAddMyCourse(TrainPlanCreateEvent event) {
		TrainPlan trainPlan = event.getTrainPlan();
		PersonListQueryRequest req = new PersonListQueryRequest();
		req.setOrgId(trainPlan.getOrgId());
		req.setIsAdmin(2);
		List<Person> personList = personService.findList(req);
		List<Project> projects = trainPlan.getProjects();
		List<PersonCourseRelate> records = Lists.newArrayList();
		for (Project project : projects) {
			if (project.getTrainType().equals(2)) {//2-选修课
				continue;
			}
			for (Person person : personList) {
				//自动添加必修课程
				records.add(this.createMyCourse(project, person.getId()));
			}
		}
		this.saveBatch(records);
	}

	/**
	 * 项目id  人员id查询记录
	 * @param projectId
	 * @param personId
	 * @return
	 */
	public PersonCourseRelate getRecord(Long projectId, Long personId) {
		return baseMapper.getRecord(projectId,personId);
	}

	/**
	 * 更新学时,学分
	 * @param projectId 项目id
	 * @param personId 人员id
	 * @param coursewareId 课件id
	 * @param type 1-课中检测来的  2-选修课不用课中检测
	 */
	public void updateLearnHours(Integer type,Long projectId, Long personId, Long coursewareId) {
		log.info("XmPersonCourseRelateService - updateLearnHours params:(projectId:{},personId:{},coursewareId:{})"
				,projectId,personId,coursewareId);

		Courseware courseware = coursewareService.getById(coursewareId);
//		BizAssert.notNull("课件未找到",courseware,courseware.getCourseId());
		if(courseware == null) throw new CustomException("课件未找到");
		PersonCourseRelate record = getRecord(projectId, personId);
		if(record == null || record.getId() == null) throw new CustomException("学时记录未找到");

		if(record.getIsSign() == 1) record.setIsSign(2);
		if(type == 1){
			BigDecimal learnHours = record.getLearnHours() == null?BigDecimal.ZERO:record.getLearnHours();
			BigDecimal learnScore = record.getLearnScore() == null?BigDecimal.ZERO:record.getLearnScore();
			record.setLearnHours(learnHours.add(courseware.getLearnHours()));
			record.setLearnScore(learnScore.add(courseware.getLearnScore()));
		} else {
			record.setLearnHours(BigDecimal.ZERO);
			record.setLearnScore(BigDecimal.ZERO);
		}

		//更新学习完成状态
		if(baseMapper.getStudyStatus(personId,projectId) == 100){
			record.setIsSign(3);
		}

		updateById(record);
	}

	/**
	 * 学习记录列表
	 * @param personId
	 * @return
	 */
	public List<StudyRecordVo> getStudyRecord(Long personId) {
		return baseMapper.getStudyRecord(personId);
	}

	public void removeByProjectId(Long id) {
		baseMapper.removeByProjectId(id);
		baseMapper.removeRecordByProjectId(id);
	}

	/**
	 *  查询结业证书人员信息
	 * @param personId
	 * @return
	 */
	public Person getPersonInfo(Long personId) {
		Person person = personService.get(personId);
		return person;
	}

	/**
	 * 查询学习时间
	 * @param tag start-开始时间  end-结束时间
	 * @param personId
	 * @param projectId
	 * @return
	 */
	public Date getStudyDate(String tag, Long personId, Long projectId) {
		return baseMapper.getStudyDate(tag,personId,projectId);
	}

	/** 查询结业凭证列表 */
	public List<Map<String, String>> getGraduationList(Long personId) {
		return baseMapper.getGraduationList(personId);
	}

	/**
	 * 查询人学习项目的状态
	 * @param projectId
	 * @param personId
	 * @return
	 */
	public Integer getStatus(Long projectId, Long personId) {
		PersonCourseRelate record = baseMapper.getRecord(projectId, personId);
		if(record == null) return 1;
		else return record.getIsSign();
	}
}
