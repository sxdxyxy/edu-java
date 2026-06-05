package com.joyfishs.dawa.student.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.answer.vo.AnswerVo;
import com.joyfishs.dawa.student.service.SimulateTestService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/simulateTest")
public class SimulateTestController extends BaseController {

	@Autowired
	SimulateTestService simulateTestService;


	/**
	 * 获取模拟测试列表
	 * @param projectId
	 * @param personId
	 * @return
	 */
	@PostMapping("/list")
	public AjaxResult<?> getSimulateTestList(@RequestParam Long projectId,@RequestParam Long personId){
		log.info("XmSimulateTestController - getSimulateTestList params( projectId:{},personId:{})",projectId,personId);
		List<Map<String, Object>> list = simulateTestService.getMockTestList(projectId, personId);
		return AjaxResult.success(list);
	}

	/**
	 * 获取模拟测试题目数据
	 * @param projectId
	 * @param personId
	 * @return
	 */
	@PostMapping("/getQuestions")
	public AjaxResult<?> getQuestions(@RequestParam Long projectId,
	                               @RequestParam Long personId,
	                               @RequestParam(required = false) Long examId){
		log.info("XmSimulateTestController - getQuestions params( projectId:{},personId:{})",projectId,personId);
		AnswerVo questions = simulateTestService.getQuestions(projectId, personId);
		return AjaxResult.success(questions);
	}

	/**
	 * 提交使用小代的接口
	 */
}
