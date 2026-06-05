package com.joyfishs.dawa.safety.controller;

import com.joyfishs.dawa.safety.entity.ViolationTypeConfig;
import com.joyfishs.dawa.safety.service.ViolationTypeConfigService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 违章类型配置控制器
 *
 * @author safe-edu
 * @since 2026-05-30
 */
@Slf4j
@RestController
@Component
@RequestMapping("/safety/violation-type")
@Api(tags = "违章类型配置")
public class ViolationTypeConfigController {

    @Autowired
    private ViolationTypeConfigService violationTypeConfigService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('safety:violationtype:list')")
    @ApiOperation("查询违章类型列表")
    public AjaxResult<List<ViolationTypeConfig>> list() {
        List<ViolationTypeConfig> list = violationTypeConfigService.list();
        return AjaxResult.success(list);
    }

    @GetMapping("/enabled")
    @ApiOperation("获取启用的违章类型（下拉框用）")
    public AjaxResult<List<ViolationTypeConfig>> enabled() {
        List<ViolationTypeConfig> list = violationTypeConfigService.getAllEnabled();
        return AjaxResult.success(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('safety:violationtype:list')")
    @ApiOperation("获取违章类型详情")
    public AjaxResult<ViolationTypeConfig> getById(@PathVariable Long id) {
        ViolationTypeConfig config = violationTypeConfigService.getById(id);
        return AjaxResult.success(config);
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('safety:violationtype:edit')")
    @ApiOperation("新增违章类型")
    public AjaxResult<Void> add(@RequestBody ViolationTypeConfig config) {
        violationTypeConfigService.save(config);
        return AjaxResult.success("新增成功");
    }

    @PutMapping
    @PreAuthorize("@ss.hasPermi('safety:violationtype:edit')")
    @ApiOperation("更新违章类型")
    public AjaxResult<Void> update(@RequestBody ViolationTypeConfig config) {
        violationTypeConfigService.updateById(config);
        return AjaxResult.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('safety:violationtype:delete')")
    @ApiOperation("删除违章类型")
    public AjaxResult<Void> delete(@PathVariable Long id) {
        violationTypeConfigService.removeById(id);
        return AjaxResult.success("删除成功");
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("@ss.hasPermi('safety:violationtype:edit')")
    @ApiOperation("启用/禁用违章类型")
    public AjaxResult<Void> toggle(@PathVariable Long id) {
        ViolationTypeConfig config = violationTypeConfigService.getById(id);
        if (config == null) return AjaxResult.error("记录不存在");
        config.setStatus("enabled".equals(config.getStatus()) ? "disabled" : "enabled");
        violationTypeConfigService.updateById(config);
        return AjaxResult.success("操作成功");
    }
}
