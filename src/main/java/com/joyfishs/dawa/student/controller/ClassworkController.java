package com.joyfishs.dawa.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.answer.vo.AnswerVo;
import com.joyfishs.dawa.answer.vo.SubmitPaperParams;
import com.joyfishs.dawa.student.domain.vo.MyClassworkListVo;
import com.joyfishs.dawa.student.service.ClassworkService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 作业接口
 */
@Slf4j
@RestController
@RequestMapping("/classwork")
public class ClassworkController extends BaseController {

	@Autowired
	ClassworkService classworkService;

	/**
	 * 一．作业模块来自课程管理的题库，每次上传课件都要上传相应的题库。每个课程只要有答题练习就行。不需要对着原型的做。
	 * （例如：点击作业直接进入答题界面，15个题提交一次）。
	 * 沟通结果：
	 *  点了提交后在判断正确错误
	 *  每个项目下不给他设置作业，直接是点击作业就开始答题。
	 *  不设置作业数量
	 *  就在项目题库里随机排列组合给他做
	 * 获取作业详情，返回每个课程的抽题
	 * @param projectId
	 * @param personId
	 * @return
	 */
	@GetMapping("/get")
	public AjaxResult<?> get(@RequestParam Long projectId, @RequestParam Long personId){
		log.info("XmClassworkController - getList params( projectId:{},personId:{})",projectId,personId);
		log.info("XmProjectPersonStudyRecordController - getLearning params( projectId:{},personId:{})",projectId,personId);
		AnswerVo questions = classworkService.getQuestions(projectId, personId);
		return AjaxResult.success(questions);
	}

	/**
	 * 作业提交
	 * @return
	 */
	@PostMapping("/submit")
	public AjaxResult<?> submit(@RequestBody SubmitPaperParams paperParams){
		log.info("XmClassworkController - getList params( paperParams:{})",paperParams);
		Long reportId = classworkService.submitClasswork(paperParams);
		return AjaxResult.success().put("reportId", reportId);
	}

	/**
	 * 作业列表，答题之后才有的列表
	 * @return
	 */
	@GetMapping("/list")
	public TableDataInfo<?> list(@RequestParam Long personId,@RequestParam Long projectId){
		log.info("XmClassworkController - getList params( personId:{})",personId);
		if(personId == null) personId = SecurityUtil.getPersonId();
		startPage();
		List<MyClassworkListVo> list = classworkService.getClassworkList(personId,projectId);
		return getDataTable(list);
	}

}
