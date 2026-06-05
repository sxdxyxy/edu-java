package com.joyfishs.dawa.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.course.entity.CourseQuestion;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.course.service.RequiredQuestionService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "必选题管理")
@RequestMapping("/courseManagement/requiredQuestion")
public class CoursewareRequiredQuestionController extends BaseController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private RequiredQuestionService requiredQuestionService;

    @GetMapping("/listPage")
    @PreAuthorize("@ss.hasPermi('requiredQuestion:list')")
    @Log(title = "必选题列表", businessType = BusinessType.SELECT)
    public TableDataInfo<?> listPage(@RequestParam(required = true)Long courseId) {
        log.info("RequiredQuestionController - listPage courseId:{}",courseId);

        startPage();
        //获取所有题目
        List<CourseQuestion> questionList = courseService.findQuestionList(courseId);
        log.info("RequiredQuestionController - listPage questionList:{}",questionList);
        return getDataTable(questionList);
    }

    @GetMapping("/requiredQuestionId")
    @PreAuthorize("@ss.hasPermi('requiredQuestion:requiredQuestionId')")
    @Log(title = "必选题ID", businessType = BusinessType.SELECT)
    public AjaxResult<?> requiredQuestionId(@RequestParam(required = true)Long coursewareId) {
        log.info("RequiredQuestionController - requiredQuestionId coursewareId:{}",coursewareId);
        //课件下的必选题ID
        List<Long> required = requiredQuestionService.findRequiredQuestion(coursewareId);
        log.info("RequiredQuestionController - requiredQuestionId required:{}",required);
        return AjaxResult.success(required);
    }

    @PostMapping("/addOrUpdate")
    @PreAuthorize("@ss.hasPermi('requiredQuestion:addOrUpdate')")
    @Log(title = "必选题新增", businessType = BusinessType.INSERT)
    public AjaxResult<?> saveCoursewareQuestion(@RequestParam String ids, @RequestParam Long coursewareId) {
        log.info("RequiredQuestionController - saveCoursewareQuestion ids:{}",ids);
        log.info("RequiredQuestionController - saveCoursewareQuestion coursewareId:{}",coursewareId);
        requiredQuestionService.saveOrUpdateRequired(ids,coursewareId);

        return AjaxResult.success();
    }

    @GetMapping("/testing")
    @PreAuthorize("@ss.hasPermi('requiredQuestion:testing')")
    @Log(title = "课中检测", businessType = BusinessType.SELECT)
    public AjaxResult<?> testing(@RequestParam(required = true)Long coursewareId) {
        log.info("RequiredQuestionController - testing coursewareId:{}",coursewareId);
        //课件下的必选题--课中检测题目
        List<CourseQuestion> questionList = requiredQuestionService.testing(coursewareId);
        log.info("RequiredQuestionController - testing questionList:{}",questionList);
        return AjaxResult.success(questionList);
    }
}
