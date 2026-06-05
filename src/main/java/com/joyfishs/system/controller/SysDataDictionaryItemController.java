package com.joyfishs.system.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.system.config.validation.Update;
import com.joyfishs.system.entity.SysDataDictionaryItem;
import com.joyfishs.system.service.SysDataDictionaryItemService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "系统词典管理")
@Slf4j
@RestController
@RequestMapping("sys/dictionary/item")
public class SysDataDictionaryItemController extends BaseController {

    @Autowired
    private SysDataDictionaryItemService sysDataDictionaryItemService;

    @ApiOperation(value = "词典项新增")
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:item:edit')")
    public AjaxResult<?> add(@RequestBody @Validated SysDataDictionaryItem sysDataDictionaryItem) {
        return toAjax(sysDataDictionaryItemService.saveOrUpdate(sysDataDictionaryItem));
    }

    @ApiOperation(value = "词典项详情")
    @PostMapping("/get")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:item:get')")
    public AjaxResult<?> get(@RequestParam Long id) {
        return AjaxResult.success(sysDataDictionaryItemService.getById(id));
    }

    @ApiOperation(value = "词典项修改保存")
    @PostMapping("/edit")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:item:edit')")
    public AjaxResult<?> edit(@RequestBody @Validated(Update.class) SysDataDictionaryItem sysDataDictionaryItem) {
        return toAjax(sysDataDictionaryItemService.saveOrUpdate(sysDataDictionaryItem));
    }

    @ApiOperation(value = "词典项删除")
    @PostMapping("/del")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:item:del')")
    public AjaxResult<?> del(@RequestParam String ids, @RequestParam String deleteReason) {
        if (StringUtils.isEmpty(ids)) return AjaxResult.success();
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Long.parseLong(idStr)).collect(Collectors.toList());
        return toAjax(sysDataDictionaryItemService.del(idList, deleteReason));
    }

    @ApiOperation(value = "词典项分页列表")
    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:item:list')")
    public TableDataInfo<?> pageList(SysDataDictionaryItem sysDataDictionaryItem) {
        startPage();
        List<SysDataDictionaryItem> list = sysDataDictionaryItemService.findList(sysDataDictionaryItem);
        return getDataTable(list);
    }

    @ApiOperation(value = "词典项列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:item:list')")
    public AjaxResult<?> list(SysDataDictionaryItem sysDataDictionaryItem) {
        List<SysDataDictionaryItem> list = sysDataDictionaryItemService.findList(sysDataDictionaryItem);
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "查询最大词典项值")
    @GetMapping("/findMaxValue")
    @PreAuthorize("@ss.hasPermi('sys:dictionary:item:list')")
    public AjaxResult<?> findMaxValue(SysDataDictionaryItem sysDataDictionaryItem) {
        if (StringUtils.isEmpty(sysDataDictionaryItem.getDictionaryCode()))
            return AjaxResult.error("词典项标识不能为空");
        Integer maxValue = sysDataDictionaryItemService.findMaxValueByCode(sysDataDictionaryItem.getDictionaryCode());
        return AjaxResult.success(maxValue);
    }

    @ApiOperation(value = "词典项下拉选")
    @GetMapping("/dropDown")
    public AjaxResult<?> dropDown(@ApiParam(value = "编码", required = true) @RequestParam String dictionaryCode) {
        List<Dict> list = sysDataDictionaryItemService.listByCode(dictionaryCode);
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "移动端词典项下拉选")
    @GetMapping("/mobile/dropDown")
    public AjaxResult<?> dropDownNoToken(@ApiParam(value = "编码", required = true) @RequestParam String dictionaryCode) {
        List<Dict> list = sysDataDictionaryItemService.listByCode(dictionaryCode);
        return AjaxResult.success(list);
    }
}
