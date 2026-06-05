package com.joyfishs.dawa.student.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.service.AnswerReportService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.dawa.student.domain.CourseDetail;
import com.joyfishs.dawa.student.domain.MyCourseList;
import com.joyfishs.dawa.student.domain.StudentCourseCatalogue;
import com.joyfishs.dawa.student.domain.vo.StudyRecordVo;
import com.joyfishs.dawa.student.domain.vo.TrainPlanVo;
import com.joyfishs.dawa.student.entity.PersonCourseRelate;
import com.joyfishs.dawa.student.service.PersonCourseRelateService;
import com.joyfishs.dawa.student.service.StudentCourseCatalogueService;
import com.joyfishs.dawa.student.service.StudentCourseListService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 我的课程
 */
@Slf4j
@RestController
@Api(tags = "我的课程")
@RequestMapping("/myCourse")
public class StudentCourseListController extends BaseController {

	@Autowired
	StudentCourseListService studentCourseListService;

	@Autowired
	StudentCourseCatalogueService catalogueService;

	@Autowired
	PersonCourseRelateService personCourseRelateService;

	@Autowired
	AnswerReportService reportService;

	@Autowired
    ProjectService projectService;


	/**
	 * 首页-我的课程列表，课程搜索
	 *
	 * @param personId  人员id
	 * @param name      课程名
	 * @param type      null-是课程
	 * @param trainType 1-必修课  2-选修课
	 * @param status    1-未开始  2-进行中  3-已完成
	 * @return
	 */
	@GetMapping("/getMyCourseList")
	@ApiOperation(value = "首页-我的课程列表，课程搜索")
	public TableDataInfo<?> getMyCourseList(@RequestParam Long personId,
	                                     @RequestParam(required = false) Integer status,
	                                     @RequestParam(required = false) String name,
	                                     @RequestParam(required = false) Integer type,
	                                     @RequestParam(value = "trainWay", required = false) Integer trainType) {

		projectService.refreshProjectStatus();
		startPage();
		List<MyCourseList> list = studentCourseListService.getMyCourseList(personId, name, type, trainType, status, null);
		return getDataTable(list);
	}

	/**
	 * 培训计划列表
	 *
	 * @param personId 人员id
	 * @param type     2-年度培训计划  3-单位培训计划
	 * @return
	 */
	@GetMapping("/getMyTrainPlanList")
	@ApiOperation(value = "培训计划列表")
	public TableDataInfo<?> getMyTrainPlanList(@RequestParam Long personId, @RequestParam Integer type) {
		startPage();
		List<TrainPlanVo> list = studentCourseListService.queryTrainPlanList(personId, type);
		TableDataInfo<?> dataTable = getDataTable(list);
		return dataTable;
	}

	/**
	 * 培训计划中项目列表
	 *
	 * @param trainPlanId 培训计划id
	 * @return
	 */
	@GetMapping("/getMyTrainPlanCourse")
	@ApiOperation(value = "培训计划中项目列表")
	public TableDataInfo<?> getMyTrainPlan(@RequestParam Long trainPlanId, @RequestParam Long personId) {
		startPage();
		List<MyCourseList> list = studentCourseListService.getTrainPlan(trainPlanId, personId, null);
		TableDataInfo<?> dataTable = getDataTable(list);

		return dataTable;
	}

	/**
	 * 添加我的课程
	 *
	 * @param personId
	 * @param projectId
	 * @return
	 */
	@GetMapping("/addMyCourse")
	@ApiOperation(value = "添加到我的课程")
	@Log(title = "添加我的课程", businessType = BusinessType.UPDATE)
	public AjaxResult<?> addMyCourse(@RequestParam Long personId,
	                              @RequestParam Long projectId){

		Project project = projectService.getById(projectId);
		PersonCourseRelate record = personCourseRelateService.createMyCourse(project, personId);
		personCourseRelateService.saveOrUpdate(record);
		return AjaxResult.success();
	}

	/**
	 * 	我的课程详情
	 * @param id
	 * @return
	 */
	@GetMapping("/getCourseDetail")
	@ApiOperation(value = "我的课程详情")
	public AjaxResult<?> getCourseDetail(@RequestParam Long id){
		CourseDetail detail = studentCourseListService.getMyCourseDetail(id);
		detail.setStatus(personCourseRelateService.getStatus(id, SecurityUtil.getPersonId()));
		return AjaxResult.success(detail);
	}

	/**
	 * 获取课程目录
	 * @param personId
	 * @param projectId
	 * @return
	 */
	@GetMapping("/getCourseCatalogue")
	@ApiOperation(value = "获取课程目录")
	public AjaxResult<?> getCourseCatalogue(@RequestParam Long personId,
	                                     @RequestParam Long projectId){
		List<StudentCourseCatalogue> list = catalogueService.getCourseTree(projectId, personId); //获取课程列表树
		return AjaxResult.success(list);
	}

	/**
	 * 学生端-我的学习记录
	 * @param personId
	 * @return
	 */
	@GetMapping("/getStudyRecord")
	@ApiOperation(value = "我的学习记录")
	public TableDataInfo<?> getStudyRecord(@RequestParam Long personId){
		startPage();
		List<StudyRecordVo> list = personCourseRelateService.getStudyRecord(personId);
		return getDataTable(list);
	}

	/**
	 * 查询结业凭证列表
	 * @param personId
	 * @return
	 */
	@GetMapping("/getGraduationList")
	@ApiOperation(value = "结业凭证列表")
	public TableDataInfo<?> getGraduationList(Long personId){
		startPage();
		List<Map<String,String>> list =  personCourseRelateService.getGraduationList(personId);
		return getDataTable(list);
	}

	/**
	 * 结业凭证数据
	 * @param personId
	 * @return
	 */
	@GetMapping("/getGraduationCert")
	@ApiOperation(value = "结业凭证")
	public AjaxResult<?> getGraduationCert(@RequestParam Long personId,@RequestParam Long projectId){
		Map<String,Object> res = new HashMap<>();

		/** 人员信息 */
		Person personInfo = personCourseRelateService.getPersonInfo(personId);
		personInfo.setJobsName("");//字段改了用途没用，改名兼容
		Project project = projectService.getById(projectId);
		int trainWay = project.getTrainWay();
		Boolean isExam = false;
		if (trainWay == 2 || trainWay == 4) {
			isExam = true;
		}

		List<StudentCourseCatalogue> courseTree = catalogueService.getCourseTree(projectId, personId);
		StringBuilder leveContent = new StringBuilder();
		for (StudentCourseCatalogue studentCourseCatalogue : courseTree) {

			if(leveContent.length() != 0) {
                leveContent.append(",");
            }
			leveContent.append(studentCourseCatalogue.getName());

			/** 20220527 by天天： @- @🐜圈圈🌾  我看到这个记录了~
			 * 要不结业凭证，内容摘要只显示课程名，不显示课件标题。
			 * 这样避免内容的重复，而且有的课程下有很多个课件，都加上的话会超长
 			 */
//			List<XmStudentCourseCagelogue> courseList = xmStudentCourseCagelogue.getCourseList();
//			for (XmStudentCourseCagelogue studentCourseCagelogue : courseList) {
//				if(leveContent.length() != 0)leveContent.append(",");
//				leveContent.append(studentCourseCagelogue.getName());
//			}
		}
		String content = leveContent.toString();
		res.put("leveContent",content);
		res.put("projectName", project.getProjectName());
		//项目类型 1-培训 2-考试 3-培训-练习 4-培训-练习-考试
		res.put("trainWay",project.getTrainWay());

		res.put("personInfo",personInfo);
		res.put("courseDetail",courseTree);

		Date start = personCourseRelateService.getStudyDate("start",personId,projectId);
		Date end = personCourseRelateService.getStudyDate("end",personId,projectId);

		res.put("startDate", DateUtil.format(start, DatePattern.NORM_DATE_PATTERN));
		res.put("endDate",DateUtil.format(end, DatePattern.NORM_DATE_PATTERN));
		res.put("content",isExam?"成绩合格，予以结业。":"学时达标，予以结业。");

		//不是考试类型的项目，查最大的学习时间
		if(!isExam){
			PersonCourseRelate record = personCourseRelateService.getRecord(projectId, personId);
			res.put("learnHours",record.getLearnHours());
		} else { //不是考试类型的项目，查最大的考试时间
			AnswerReport report = reportService.getMaxScore(personId,projectId);
			res.put("score",report.getScore());
//			res.put("answerTime",report.getAnswerTime());
		}
		return AjaxResult.success(res);
	}
}
