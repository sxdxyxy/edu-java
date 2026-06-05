package com.joyfishs.dawa.project.controller;

import com.joyfishs.dawa.project.entity.ProjectDict;
import com.joyfishs.dawa.project.service.ProjectDictService;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author ykfnb
 */
@Slf4j
@RestController
@Api(tags = "培训项目种类管理")
@RequestMapping("/project/species")
public class ProjectSpeciesController extends BaseController {

    @Autowired
    ProjectDictService dictService;
    @Autowired
    ProjectService projectService;

    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('project:edit')")
    @ApiOperation(value = "新增种类")
    public AjaxResult<?> add(@RequestBody @Validated ProjectDict species) {
        // 检查种类名称是否已存在
        if (dictService.isNameExists(species.getName(), species.getType())) {
            return AjaxResult.error("此名称已存在");
        }
        return AjaxResult.success(dictService.save(species));
    }

    @DeleteMapping("/del")
    @PreAuthorize("@ss.hasPermi('project:edit')")
    @ApiOperation(value = "删除种类")
    public AjaxResult<?> del(@RequestParam Long id) {
        if (Objects.isNull(id)) {
            return AjaxResult.success();
        }
        //检查是否被项目关联使用
        if (projectService.countByTrainClass(id) > 0) {
            return AjaxResult.error("此种类已被项目关联使用，不能删除");
        }
        return toAjax(dictService.del(id));
    }

    @GetMapping("/pageList")
    @ApiOperation(value = "分页查询种类列表")
    public TableDataInfo<?> pageList(@RequestParam(required = false) String code) {
        startPage();
        List<ProjectDict> list = dictService.getList(code, 1);
        return getDataTable(list);
    }

}
