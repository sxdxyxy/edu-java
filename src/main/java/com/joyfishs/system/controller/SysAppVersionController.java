package com.joyfishs.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.entity.SysAppVersion;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.service.SysAppVersionService;
import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/appVersion")
public class SysAppVersionController extends BaseController {

    @Autowired
    private SysAppVersionService sysAppVersionService;

    @PostMapping("/getVersionNumByAppType")
    @Log(title = "根据app类型获取版本号", businessType = BusinessType.INSERT)
    public AjaxResult<?> getVersionNumByAppType(@RequestParam Integer appType, @RequestParam String currentVersion) {
        return AjaxResult.success("操作成功", sysAppVersionService.getVersionCodeInfo(appType,currentVersion));
    }

    /** 清空Redis缓存 */
    @PostMapping("/clearVersionCache")
    @Log(title = "app版本管理清空Redis缓存", businessType = BusinessType.UPDATE)
    public AjaxResult<?> clearVersionCache(@RequestParam Integer appType) {
        sysAppVersionService.clearRedisCache(appType);
        return AjaxResult.success();
    }

    @GetMapping("/{id}")
    @Log(title = "app版本管理获取一条记录", businessType = BusinessType.SELECT)
    public AjaxResult<?> get(@PathVariable Long id) {
        return AjaxResult.success(sysAppVersionService.getById(id));
    }

    @PostMapping
    @Log(title = "app版本管理新增", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody SysAppVersion t) {
        return toAjax(sysAppVersionService.executeSave(t));
    }

    @PutMapping
    @Log(title = "app版本管理编辑", businessType = BusinessType.UPDATE)
    public AjaxResult<?> edit(@RequestBody SysAppVersion t) {
        return toAjax(sysAppVersionService.executeUpdate(t));
    }

}
