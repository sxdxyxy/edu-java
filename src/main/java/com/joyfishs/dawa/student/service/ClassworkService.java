package com.joyfishs.dawa.student.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyfishs.dawa.answer.vo.AnswerVo;
import com.joyfishs.dawa.answer.vo.SubmitPaperParams;
import com.joyfishs.dawa.exam.service.ExamService;
import com.joyfishs.dawa.practice.service.PracticeService;
import com.joyfishs.dawa.student.domain.vo.MyClassworkListVo;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 作业服务
 */
@Slf4j
@Service
public class ClassworkService {
	@Autowired
	PracticeService practiceService;

	@Autowired
    ExamService examService;

	/**
	 * 获取题目
	 * @param projectId
	 * @param personId
	 * @return
	 */
	public AnswerVo getQuestions(Long projectId, Long personId) {
		//获取作业题目
		AnswerVo vo = examService.getClassworkQuestions(projectId, personId);
		return vo;
	}

	/**
	 * 作业提交
	 * @param paperParams
	 * @return
	 */
	public Long submitClasswork(SubmitPaperParams paperParams) {
		return examService.submitClasswork(paperParams);
	}

	/** 获取作业列表 */
	public List<MyClassworkListVo> getClassworkList(Long personId, Long projectId) {
		return examService.getClassworkList(personId, projectId);
	}
}
