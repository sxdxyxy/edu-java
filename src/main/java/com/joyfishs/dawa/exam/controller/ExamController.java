package com.joyfishs.dawa.exam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.answer.vo.AnswerVo;
import com.joyfishs.dawa.answer.vo.SubmitPaperParams;
import com.joyfishs.dawa.exam.service.ExamService;
import com.joyfishs.dawa.exam.service.StudentSubscribeExamService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.SpringUtil;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 考试控制器
 */

@Slf4j
@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private ExamService examService;
    @Autowired
    private StudentSubscribeExamService studentSubscribeExamService;


    /**
     * 我的考试-可预约考试列表
     * @param examClassify 项目类型--培训种类id
     * @param status  状态  1-未开始 2-已结束
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('exam:list')")
    @Log(title = "我的考试-预约考试列表", businessType = BusinessType.SELECT)
    public TableDataInfo<?> subscribeList(@RequestParam(required = false) Integer examClassify,
                                       @RequestParam(required = false) Integer status) {
        startPage();
        List<Project> projectList = examService.findExamAll(examClassify,status);
        return getDataTable(projectList);
    }


    @GetMapping("/subscribe")
    @PreAuthorize("@ss.hasPermi('exam:subscribe')")
    @Log(title = "我的考试-预约考试", businessType = BusinessType.INSERT)
    public AjaxResult<?> subscribeExam(@RequestParam Long projectId){
         return AjaxResult.success(studentSubscribeExamService.subscribeExam(projectId));
    }


    /**
     * 获取题目-参加考试
     * @param projectId
     * @param personId
     * @param examClassify 1-模拟  2-正式
     * @return
     */
    @GetMapping("/exam")
    @PreAuthorize("@ss.hasPermi('exam:question')")
    @Log(title = "我的考试-进入考试", businessType = BusinessType.OTHER)
    public AjaxResult<?> attendExam(@RequestParam Long projectId,
                                 @RequestParam Long personId,
                                 @RequestParam(required = false) Integer examClassify) {
        AnswerVo answerVo = examService.getExamQuestionList(projectId,personId,0);
        return AjaxResult.success(answerVo);
    }

    @PostMapping("/submit")
    @PreAuthorize("@ss.hasPermi('exam:submit')")
    @Log(title = "我的考试-交卷", businessType = BusinessType.OTHER)
    public AjaxResult<?> submitExam(@RequestBody SubmitPaperParams paperParams) {
        Long reportId = examService.submitExam(paperParams);

        return AjaxResult.success().put("reportId", reportId);
    }


    /** type 报告类型 1:课程 2:考试 3:练习 4:作业 **/
    @GetMapping("/attend")
    @PreAuthorize("@ss.hasPermi('exam:attend')")
    @Log(title = "我的考试-已参加的考试", businessType = BusinessType.SELECT)
    public TableDataInfo<?> attendExam(@RequestParam(required = false) Integer examClassify,
                                    @RequestParam(required = false) Integer type) {
        startPage();
        List<Project> projectList = examService.findAttendExamAll(examClassify,type);
        return getDataTable(projectList);
    }


    // 查看报告
    @GetMapping("/viewReport")
    @PreAuthorize("@ss.hasAnyPermi('exam:viewReport')")
    @Log(title = "考试-查看答题报告", businessType = BusinessType.SELECT)
    public AjaxResult<?> viewReport(@RequestParam Long projectId) {
        return AjaxResult.success(examService.viewReport(projectId));
    }


    // 查看解析
    @GetMapping("/viewResolution")
    @PreAuthorize("@ss.hasAnyPermi('exam:viewResolution')")
    @Log(title = "考试-查看答题解析", businessType = BusinessType.SELECT)
    public AjaxResult<?> viewResolution(@RequestParam Long projectId) {
        return AjaxResult.success(examService.viewResolution(projectId));
    }




    @GetMapping("/mobileExamList")
    @PreAuthorize("@ss.hasAnyPermi('exam:mobileExamList')")
    @Log(title = "我的考试 - 移动端", businessType = BusinessType.SELECT)
    public TableDataInfo<?> mobileExamList(Integer examClassify,Integer status) {
        Person person = SpringUtil.getBean(PersonService.class).getById(SecurityUtil.getPersonId());
        startPage();
        List<Project> projectList = examService.findMobileExamList(examClassify,status,person);
        return getDataTable(projectList);
    }


    @GetMapping("/mobileRecord")
    @PreAuthorize("@ss.hasAnyPermi('exam:mobileRecord')")
    @Log(title = "答题记录 - 试卷", businessType = BusinessType.SELECT)
    public TableDataInfo<?> mobileRecord(Integer examClassify) {
        startPage();
        List<Project> projectList = examService.findAttendExamAll(examClassify,2);
        return getDataTable(projectList);
    }


}
