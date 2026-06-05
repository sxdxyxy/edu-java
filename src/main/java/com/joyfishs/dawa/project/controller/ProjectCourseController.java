package com.joyfishs.dawa.project.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.project.entity.ProjectRelate;
import com.joyfishs.dawa.project.service.ProjectRelateService;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@RestController
@Api(tags = "项目课程管理")
@RequestMapping("/project/course")
public class ProjectCourseController extends BaseController {

	@Autowired
	ProjectService projectService;

	@Autowired
    ProjectRelateService relateService;

	@Autowired
    ProjectRelateService projectRelateService;

	@Autowired
    CourseService courseService;

	@GetMapping("/list")
	@PreAuthorize("@ss.hasPermi('project:list')")
	@ApiOperation(value = "查询项目关联课程详情")
	public AjaxResult<?> list(@RequestParam Long id) {
		//课程列表
		return AjaxResult.success(courseService.getListByProjectId(id));
	}

	/**
	 * 更新排序
	 * @param sortStr 操作字符（down---下移，up---上移）
	 * @param  id 记录id
	 * @return
	 */
	@RequestMapping(value = "/updateSort",method = RequestMethod.GET)
	public AjaxResult<?> updateSort(String sortStr, Long id) {
		if (StrUtil.equals("down",sortStr)) {
			relateService.moveDown(id);
		} else if (StrUtil.equals("up",sortStr)) {
			relateService.moveUp(id);
		}
		return AjaxResult.success();
	}

	@PostMapping("/listByCourseIds")
	@PreAuthorize("@ss.hasPermi('project:list')")
	@ApiOperation(value = "课程列表查询")
	public AjaxResult<?> list(@RequestParam String ids,@RequestParam(required = false) String courseName) {
		String[] split = ids.split(",");
		List<String> collect = Arrays.stream(split).collect(Collectors.toList());
		return AjaxResult.success(courseService.getListByIds(collect,courseName));
	}

	@PostMapping("/add")
	@PreAuthorize("@ss.hasPermi('project:edit')")
	@ApiOperation(value = "新增项目")
	public AjaxResult<?> add(@RequestBody ProjectRelate courseIds) {
		log.info("XmProjectCourseCourseController - add id:{}", courseIds);
		//课程列表
		Long projectId = courseIds.getProjectId();
		if(projectId == null) {
			return AjaxResult.error("项目ID不能为空");
		}
		List<ProjectRelate> lessonIds = courseIds.getList();
		List<ProjectRelate> collect = lessonIds.stream().map(r -> {
			r.setType(2);
			r.setProjectId(projectId);
			r.setCreateBy(SecurityUtil.getUserId());
			r.setCreateTime(new Date());
			return r;
		}).collect(Collectors.toList());
		return AjaxResult.success(relateService.saveBatch(collect));
	}
}
