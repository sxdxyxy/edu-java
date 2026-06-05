package com.joyfishs.dawa.student.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.course.service.CourseQuestionService;
import com.joyfishs.dawa.course.service.RequiredQuestionService;
import com.joyfishs.dawa.student.entity.TestInClassRecord;
import com.joyfishs.dawa.student.mapper.TestInClassRecordMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * @program: dawa-java
 * @description: 课中检测
 * @author: Yjhon
 * @create: 2022-01-07 16:26
 */
@Service
@Slf4j
public class TestInClassRecordService extends ServiceImpl<TestInClassRecordMapper, TestInClassRecord> {

	@Autowired
    CourseQuestionService courseQuestionService;

	@Autowired
	RequiredQuestionService requiredQuestionService; //必选题

	/**
	 * 获取考试的题目
	 *
	 * @param coursewareId
	 * @return
	 */
	public QuestionVo getQuestion(Long coursewareId, Long courseId, List<Long> excludeIds) {
		List<Long> questionIdList = requiredQuestionService.getQuestionByCoursewareId(coursewareId, excludeIds); //首选必选题抽
		if (questionIdList == null || questionIdList.size() == 0)
			questionIdList = courseQuestionService.getQuestionIds(courseId, excludeIds,true); //若没有必选题，就在课程里抽
		if (questionIdList == null || questionIdList.size() == 0) throw new CustomException("该课程下没有可供抽选的题目");

		//随机抽取一个题目
		int index=(int)(Math.random()*questionIdList.size());
		log.info("XmTestInClassRecordService - getQuestion random index:{}", index);
		if(index == questionIdList.size()) index = questionIdList.size() - 1;
		Long id = questionIdList.get(index);
//		XmCourseQuestion question = courseQuestionService.details(id);
		QuestionVo question = courseQuestionService.getQuestionVo(id);
//		question.setRightAnswers(null);
		return question;
	}

	/**
	 * 保存答题记录
	 *
	 * @param personId
	 * @param projectId
	 * @param courseId
	 * @param coursewareId
	 * @param questionId
	 * @param answer
	 * @param correct
	 */
	public void saveRecord(Long personId, Long projectId, Long courseId, Long coursewareId, Long questionId, String answer, Integer correct) {
		TestInClassRecord record = new TestInClassRecord();
		record.setPersonId(personId)
				.setProjectId(projectId)
				.setCourseId(courseId)
				.setCoursewareId(coursewareId)
				.setQuestionId(questionId)
				.setCorrect(correct)
				.setAnswer(answer)
				.setIsDelete(0);
		record.setCreateBy(SecurityUtil.getUserId());
		record.setCreateTime(new Date());
		this.save(record);
	}
}
