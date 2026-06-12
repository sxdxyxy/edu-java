package com.joyfishs.dawa.safety.controller;

import com.joyfishs.dawa.safety.entity.SafetyConfig;
import com.joyfishs.dawa.safety.service.SafetyConfigService;
import com.joyfishs.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 安全配置 REST API
 * <p>
 * 提供按 group 查 / 按 key 改 两种入口.
 * 前端 DashboardHeader / SafetyEntryGuard 启动时拉一次, 缓存到 Vuex.
 * </p>
 *
 * @author safe-edu
 * @since 2026-06-10
 */
@Slf4j
@RestController
@RequestMapping("/safety/config")
@Api(tags = "安全配置管理")
public class SafetyConfigController {

    @Autowired
    private SafetyConfigService safetyConfigService;

    @GetMapping("/group/{group}")
    @PreAuthorize("@ss.hasPermi('safety:config:query')")
    @ApiOperation("按分组获取配置 (返回 Map<key, value>)")
    public AjaxResult<Map<String, String>> getByGroup(@PathVariable String group) {
        return AjaxResult.success(safetyConfigService.getByGroup(group));
    }

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('safety:config:query')")
    @ApiOperation("获取全部配置项 (含元数据: description/valueType)")
    public AjaxResult<List<SafetyConfig>> list() {
        return AjaxResult.success(safetyConfigService.list());
    }

    @GetMapping("/key/{key}")
    @PreAuthorize("@ss.hasPermi('safety:config:query')")
    @ApiOperation("按 key 查单条")
    public AjaxResult<SafetyConfig> getByKey(@PathVariable String key) {
        return AjaxResult.success(
                safetyConfigService.getOne(
                        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SafetyConfig>()
                                .eq("config_key", key)
                )
        );
    }

    @PutMapping("/key/{key}")
    @PreAuthorize("@ss.hasPermi('safety:config:edit')")
    @ApiOperation("按 key 更新 (系统内置项会被拒)")
    public AjaxResult<Boolean> updateByKey(
            @PathVariable String key,
            @ApiParam("新值") @RequestParam String value) {
        boolean ok = safetyConfigService.updateValue(key, value);
        return ok ? AjaxResult.success(true) : AjaxResult.error("更新失败 (键不存在或为系统内置)");
    }
}
