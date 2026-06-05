package com.joyfishs.dawa.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.student.domain.vo.StatisticsVo;
import com.joyfishs.dawa.student.service.StudyStatisticsService;
import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 课程统计
 */
@Slf4j
@RestController
@RequestMapping("/studyStatistics")
public class StudyStatisticsController {

	@Autowired
	StudyStatisticsService studyStatisticsService;

	/**
	 * 课程学习统计
	 * @param personId
	 * @param projectId
	 * @return
	 */
	@GetMapping("/getMyCourseList")
	public AjaxResult<?> getMyCourseList(@RequestParam Long personId,
	                                  @RequestParam Long projectId){
		StatisticsVo vo = studyStatisticsService.getStatistic(personId, projectId);
		return AjaxResult.success(vo);
	}

}
