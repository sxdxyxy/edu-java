package com.joyfishs.dawa.project.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.project.domain.vo.ProjectList;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Api(tags = "项目管理")
public class ProjectController extends BaseController {

	private final ProjectService projectService;

	@PostMapping("/addOrUpdate")
	@PreAuthorize("@ss.hasPermi('project:edit')")
	@ApiOperation(value = "新增项目")
	public AjaxResult<?> add(@RequestBody @Validated Project project) {
		return AjaxResult.success(projectService.saveOrUpdateProject(project));
	}

	@PostMapping("/del")
	@PreAuthorize("@ss.hasPermi('project:edit')")
	@ApiOperation(value = "删除项目")
	public AjaxResult<?> del(@RequestParam String id, @RequestParam(required = false) String deleteReason) {
		if (StringUtils.isEmpty(id)) {
			return AjaxResult.success();
		}
		List<Long> idList = Arrays.stream(id.split(",")).map(Long::parseLong).collect(Collectors.toList());
		return toAjax(projectService.del(idList, deleteReason));
	}

	@GetMapping("/pageList")
	@PreAuthorize("@ss.hasPermi('project:list')")
	@ApiOperation(value = "分页查询项目列表")
	public TableDataInfo<?> pageList(@RequestParam(required = false) String projectName,
	                              @RequestParam(required = false) Integer status,
	                              @RequestParam(required = false) String startDate,
	                              @RequestParam(required = false) String endDate,
	                              @RequestParam(required = false) Integer trainWay,
	                              @RequestParam(required = false) String type,
	                              @RequestParam(required = false) Long orgId) {
		projectService.refreshProjectStatus(); //刷新一次状态
		startPage();
		List<ProjectList> list = projectService.findList(projectName,status,startDate,endDate,trainWay,type,orgId);
		return getDataTable(list);
	}

	@GetMapping("/get")
	@PreAuthorize("@ss.hasPermi('project:list')")
	@ApiOperation(value = "查询项目详情")
	public AjaxResult<?> get(@RequestParam Long id) {
		Project project = projectService.get(id);
		return AjaxResult.success(project);
	}

	@PostMapping("/release")
	@PreAuthorize("@ss.hasPermi('project:release')")
	@ApiOperation(value = "项目发布")
	public AjaxResult<?> release(@RequestParam Long id) {
		log.info("SysRoleController - release id:{}", id);
		return toAjax(projectService.release(id));
	}

	@PostMapping("/stop")
	@PreAuthorize("@ss.hasPermi('project:release')")
	@ApiOperation(value = "中止项目")
	public AjaxResult<?> stop(@RequestParam Long id) {
		log.info("ProjectController - stop id:{}", id);
		return toAjax(projectService.stop(id));
	}
}
