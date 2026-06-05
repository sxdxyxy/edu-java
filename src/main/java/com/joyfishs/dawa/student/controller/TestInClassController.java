package com.joyfishs.dawa.student.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.course.service.CourseQuestionService;
import com.joyfishs.dawa.student.service.ProjectPersonStudyRecordService;
import com.joyfishs.dawa.student.service.TestInClassRecordService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 * @description: 课中检测
 */

@Slf4j
@RestController
@RequestMapping("/testInClass")
@RequiredArgsConstructor
public class TestInClassController extends BaseController {

	private final ProjectPersonStudyRecordService projectPersonStudyRecordService;
	private final TestInClassRecordService testInClassRecordService;
	private final CourseQuestionService courseQuestionService;

	/**
	 * 获取答题题目，添加记录
	 * @param courseId 课程id
	 * @param coursewareId 课件id
	 * @param personId  人员id
	 * @param projectId  项目id
	 * @param count  剩余次数 //第二次答题带回来
	 * @param question 题目实体，原封不动返回
	 * @return
	 */
	@PostMapping("/add")
	@ApiOperation(value = "课中检测")
	public AjaxResult<?> add(@RequestParam Long courseId,
	                      @RequestParam Long coursewareId,
	                      @RequestParam Long personId,
	                      @RequestParam Long projectId,
	                      @RequestParam(required = false) Integer count,
	                      @RequestBody(required = false) QuestionVo question) {
		log.info("XmTestInClassController - add params( question:{} )", question);

		if (count == null) {
            count = 2;
        }
		//第一次获取题目
		if (question == null || question.getQuestionId() == null) {
			//返回题目就可以，count= 2
			question = testInClassRecordService.getQuestion(coursewareId, courseId, null);
			return AjaxResult.success(question).put("count", count);
		}
		//对比答案 true正确  false错误
		boolean correctness = courseQuestionService.compareAnswer(question.getQuestionId(), question.getAnswerList());

		// 保存考试记录
		testInClassRecordService.saveRecord(personId, projectId, courseId, coursewareId, question.getQuestionId(), question.getAnswerList()==null?null:question.getAnswerList().toString(), correctness ? 1 : 0);
		//添加学习记录
		//回答错误也需要添加学习记录
		projectPersonStudyRecordService.updateOrSaveData(personId, projectId, courseId, coursewareId, 1, SecurityUtil.getUserId());
		//回答错误
		if (!correctness) {
			count--;
			// 答题次数用完了
			if (count == 0) {
                return AjaxResult.success().put("count", 0).put("correctness", false).put("data", null);
            }

			//重新获取题目
			Long id = question.getQuestionId();
			List<Long> ids = new ArrayList<>();
			ids.add(id);
			question = testInClassRecordService.getQuestion(coursewareId, courseId, ids);
		} else { //回答正确
			question = null;
		}
		return AjaxResult.success()
				//次数
				.put("count", count)
				//true正确  false错误
				.put("correctness", correctness)
				//题目对象，次数用完后返回null
				.put("data", question);
	}


	@GetMapping("/addRecord")
	public AjaxResult<?> addRecord(@RequestParam Long courseId,
	                            @RequestParam Long coursewareId,
	                            @RequestParam Long personId,
	                            @RequestParam Long projectId) {
		//添加学习记录
		projectPersonStudyRecordService.updateOrSaveData(personId, projectId, courseId, coursewareId, 2, SecurityUtil.getUserId());
		return AjaxResult.success();
	}

}
