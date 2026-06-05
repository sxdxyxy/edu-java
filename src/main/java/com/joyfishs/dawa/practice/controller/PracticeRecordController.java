package com.joyfishs.dawa.practice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.answer.vo.AnswerVo;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.answer.vo.SubmitPaperParams;
import com.joyfishs.dawa.practice.entity.PracticeRecord;
import com.joyfishs.dawa.practice.service.PracticeService;
import com.joyfishs.dawa.practice.service.XmPracticeRecordService;
import com.joyfishs.dawa.practice.vo.PracticeVo;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@RestController
@RequestMapping("/practice")
@Api(tags = "我的练习")
public class PracticeRecordController extends BaseController {

	@Autowired
	XmPracticeRecordService xmPracticeRecordService;

	@Autowired
	PracticeService practiceService;

	/**
	 * 我的练习列表
	 * @param type 课程分类
	 * @param name 名称
	 * @param classify 分类 1-练习  2-作业
	 * @return
	 */
	@GetMapping("/list")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "我的练习列表", businessType = BusinessType.SELECT)
	public AjaxResult<?> practiceList(String type, String name, @RequestParam(required = false) Integer classify) {
		Map<String, List<PracticeVo>> list = practiceService.findPracticeList(type, name, classify);
		return AjaxResult.success(list);
	}

	@PostMapping("/startAnswer")
	@PreAuthorize("@ss.hasPermi('practice:edit')")
	@Log(title = "我的练习-开始答题", businessType = BusinessType.SELECT)
	public AjaxResult<?> practiceList(Long courseId, Integer type) {
		AnswerVo answerVo = practiceService.getPracticeDetail(courseId, type);

		return AjaxResult.success(answerVo);
	}

	@PostMapping("/executeSubmit")
	@PreAuthorize("@ss.hasPermi('practice:edit')")
	@Log(title = "我的练习-交卷", businessType = BusinessType.INSERT)
	public AjaxResult<?> executeSubmit(@RequestBody SubmitPaperParams paperParams) {
		Long reportId = practiceService.executeSubmit(paperParams);

		return AjaxResult.success().put("reportId", reportId);
	}

	@GetMapping("/record/pageList")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "查询答题记录", businessType = BusinessType.SELECT)
	public TableDataInfo<?> pageList() {
		startPage();
		List<PracticeRecord> list = xmPracticeRecordService.findList();
		return getDataTable(list);
	}

	@GetMapping("/collection/pageList")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "我的收藏", businessType = BusinessType.SELECT)
	public TableDataInfo<?> collectionPageList() {

		startPage();

		List<QuestionVo> list = xmPracticeRecordService.findCollectionList();

		return getDataTable(list);
	}

	@GetMapping("/wrong/pageList")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "错题集", businessType = BusinessType.SELECT)
	public TableDataInfo<?> wrongPageList() {

		startPage();

		List<QuestionVo> list = xmPracticeRecordService.findWrongList(null);

		return getDataTable(list);
	}

	@GetMapping("/wrong/classifyList")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "错题集-课程类别分类", businessType = BusinessType.SELECT)
	public AjaxResult<?> wrongClassifyList() {

		List<Map<String, Object>> list = xmPracticeRecordService.findWrongClassifyList();

		return AjaxResult.success(list);
	}

	@GetMapping("/wrong/classify/pageList")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "错题集-根据课程类别分类-分页查询", businessType = BusinessType.SELECT)
	public TableDataInfo<?> wrongClassifyList(@RequestParam String courseType) {

		startPage();

		List<QuestionVo> list = xmPracticeRecordService.findWrongList(courseType);

		return getDataTable(list);
	}

	@PostMapping("/collection")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "收藏题目", businessType = BusinessType.INSERT)
	public AjaxResult<?> collection(@RequestBody Map<String, Object> params) {
		Long questionId = Long.parseLong(params.get("questionId").toString());
		@SuppressWarnings("unchecked")
		List<String> myAnswerList = (List<String>) params.get("myAnswerList");
		xmPracticeRecordService.executeCollection(questionId, myAnswerList);
		return AjaxResult.success();
	}

	@GetMapping("/cancel/collection")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "取消收藏题目/收藏错题集", businessType = BusinessType.INSERT)
	public AjaxResult<?> cancelCollection(@RequestParam Long questionId, @RequestParam Integer type) {
		xmPracticeRecordService.cancelCollection(questionId, type);
		return AjaxResult.success();
	}

	@GetMapping("/question/detail")
	@PreAuthorize("@ss.hasPermi('practice:list')")
	@Log(title = "收藏题目/收藏错题集 题目详情", businessType = BusinessType.INSERT)
	public AjaxResult<?> questionDetail(@RequestParam Long questionId, @RequestParam Integer type) {
		QuestionVo vo = xmPracticeRecordService.questionDetail(questionId, type);
		return AjaxResult.success(vo);
	}

}
