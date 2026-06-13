package com.joyfishs.dawa.plan.controller;

import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.plan.service.TrainPlanService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "培训计划管理")
@RequestMapping("/trainPlan")
public class TrainPlanController extends BaseController {

	@Autowired
	TrainPlanService trainPlanService;

	@Autowired
	ProjectService projectService;

	@PostMapping("/add")
	@ApiOperation(value = "新增培训计划")
	public AjaxResult<?> add(@RequestBody TrainPlan trainPlan) {
		return toAjax(trainPlanService.add(trainPlan));
	}

	@GetMapping("/list")
	@ApiOperation(value = "分页查询培训计划列表")
	public TableDataInfo<?> list(@RequestParam String type,
	                          @RequestParam(required = false) String name,
	                          @RequestParam(required = false) Integer year,
	                          @RequestParam(required = false) Long projectId) {
		Long orgId = SecurityUtil.getOrgId();
        if(SecurityUtil.getLoginUser().ifAdmin()) {
            orgId = null;
        }
		Integer t = 2;
		if("unit".equals(type)) {
			t = 3;
		}
		startPage();
		return getDataTable(trainPlanService.list(t,orgId, name, year, projectId));
	}

	@GetMapping("/get")
	@ApiOperation(value = "查询培训计划详情")
	public AjaxResult<?> get(@ApiParam(value = "培训计划id", required = true) @RequestParam(required = true) Long id) {
		log.info("XmTrainPlanController - list id:{}",id);
		return AjaxResult.success(trainPlanService.get(id));
	}

	@DeleteMapping("/del")
	@ApiOperation(value = "删除培训计划")
	public AjaxResult<?> del(@ApiParam(value = "培训计划id", required = true) @RequestParam(required = true) Long id) {
		// D23-4: company_manager 越权检查 — plan → projects[*] → orgId 比对 currentOrgId
		// admin / platform_manager 跳过此检查
		if (SecurityUtil.isCompanyManager()) {
			TrainPlan plan = trainPlanService.get(id);
			if (plan == null) {
				throw new CustomException("培训计划不存在", 404);
			}
			java.util.List<Long> projectIds = plan.getProjectIds();
			if (projectIds == null || projectIds.isEmpty()) {
				throw new CustomException("培训计划无关联项目，不可删除", 500);
			}
			Long currentOrgId = SecurityUtil.getOrgId();
			for (Long pid : projectIds) {
				Project p = projectService.get(pid);
				if (p == null || p.getOrgId() == null || !p.getOrgId().equals(currentOrgId)) {
					throw new CustomException("无权操作其他公司数据", 403);
				}
			}
		}
		trainPlanService.del(id);
		return AjaxResult.success();
	}

}
