package com.joyfishs.dawa.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.project.service.ProjectDictService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "培训种类")
@RequestMapping("/project/dict")
public class DictController extends BaseController {

	@Autowired
    ProjectDictService dictService;

	@GetMapping("/get")
	@ApiOperation(value = "加载培训种类")
	public AjaxResult<?> get(@RequestParam Integer type,@RequestParam(required = false) String code ) {
		log.info("XmDictController - get type:{}", type);
		return AjaxResult.success(dictService.getList(code,type));
	}
}
