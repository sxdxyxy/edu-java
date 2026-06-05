package com.joyfishs.dawa.student.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.vo.AnswerVo;
import com.joyfishs.dawa.exam.service.ExamService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 模拟测试service
 */
@Slf4j
@Service
public class SimulateTestService {


	@Autowired
	ExamService examService;

	@Autowired
    ProjectService projectService;


	/**
	 * 获取列表
	 * @param personId
	 * @param projectId
	 * @return
	 */
	public List<Map<String, Object>> getMockTestList(Long projectId, Long personId) {
		Project project = projectService.getById(projectId);
		List<Map<String, Object>> list = new ArrayList<>();
		if ("1".equals(project.getMockExam())) {
			List<AnswerReport> reports = examService.findMockTestList(projectId,personId);
			for (AnswerReport report : reports) {
				Map<String, Object> map = new HashMap<>();
				map.put("id",report.getId());
				map.put("reportId",report.getId());
				map.put("testName", project.getProjectName());
				map.put("assessMode", "机考"); //考核模式
				map.put("testManner", "闭卷"); //考试方式
				map.put("submitWay", "网络提交"); //提交方式
				map.put("timeSlot", DateUtil.format(report.getSubmitTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
				map.put("examTime", report.getAnswerTime());
				map.put("score",report.getScore());
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 获取题目
	 * @param projectId
	 * @param personId
	 * @return
	 */
	public AnswerVo getQuestions(Long projectId, Long personId) {
		return examService.testPaper(projectId, personId, 1);
	}


}
