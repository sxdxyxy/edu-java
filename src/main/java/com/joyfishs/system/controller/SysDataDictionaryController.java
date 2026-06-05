package com.joyfishs.system.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.config.validation.Update;
import com.joyfishs.system.entity.SysDataDictionary;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.service.SysDataDictionaryService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/dictionary")
public class SysDataDictionaryController extends BaseController {

    @Autowired
    private SysDataDictionaryService sysDataDictionaryService;

    // 新增
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:edit')")
    @Log(title = "词典分类新增", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Validated SysDataDictionary sysDataDictionary){
        log.info("SysDataDictionaryController - add sysDataDictionary:{}", sysDataDictionary);

        return toAjax(sysDataDictionaryService.saveOrUpdate(sysDataDictionary));
    }

    // 详情
    @PostMapping("/get")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:get')")
    @Log(title = "词典分类详情", businessType = BusinessType.UPDATE)
    public AjaxResult<?> get(@RequestParam Long id){
        log.info("SysDataDictionaryController - get id:{}", id);

        return AjaxResult.success(sysDataDictionaryService.getById(id));
    }

    // 修改
    @PostMapping("/edit")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:edit')")
    @Log(title = "词典分类修改", businessType = BusinessType.UPDATE)
    public AjaxResult<?> edit(@RequestBody @Validated(Update.class) SysDataDictionary sysDataDictionary){
        log.info("SysDataDictionaryController - edit sysDataDictionary:{}", sysDataDictionary);

        return toAjax(sysDataDictionaryService.saveOrUpdate(sysDataDictionary));
    }

    // 删除
    @PostMapping("/del")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:del')")
    @Log(title = "词典分类删除", businessType = BusinessType.DELETE)
    public AjaxResult<?> del(@RequestParam String ids, @RequestParam String deleteReason){
        log.info("SysDataDictionaryController - del ids:{}", ids);
        log.info("SysDataDictionaryController - del deleteReason:{}", deleteReason);

        if(StringUtils.isEmpty(ids)) return AjaxResult.success();

        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Long.parseLong(idStr)).collect(Collectors.toList());

        return toAjax(sysDataDictionaryService.del(idList, deleteReason));
    }

    // 列表
    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:list')")
    @Log(title = "词典分类列表", businessType = BusinessType.SELECT)
    public TableDataInfo<?> list(SysDataDictionary sysDataDictionary){
        log.info("SysDataDictionaryController - list sysDataDictionary:{}", sysDataDictionary);

        startPage();
        List<SysDataDictionary> list = sysDataDictionaryService.findList(sysDataDictionary);

        return getDataTable(list);
    }

}
