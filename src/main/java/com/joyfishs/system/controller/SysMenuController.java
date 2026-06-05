package com.joyfishs.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.config.validation.Update;
import com.joyfishs.system.entity.SysMenu;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.service.SysMenuService;
import com.joyfishs.utils.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "菜单管理")
@Slf4j
@RestController
@RequestMapping("sys/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;

    @PostMapping("/add")
    @ApiOperation(value = "新增菜单")
    @PreAuthorize("@ss.hasPermi('sys:menu:add')")
    @Log(title = "新增系统菜单", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Validated SysMenu sysMenu){
        return toAjax(sysMenuService.add(sysMenu));
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑保存菜单")
    @PreAuthorize("@ss.hasPermi('sys:menu:edit')")
    @Log(title = "编辑系统菜单", businessType = BusinessType.UPDATE)
    public AjaxResult<?> edit(@RequestBody @Validated(Update.class) SysMenu sysMenu){
        return toAjax(sysMenuService.edit(sysMenu));
    }

    @PostMapping("/del")
    @ApiOperation(value = "删除菜单")
    @PreAuthorize("@ss.hasPermi('sys:menu:del')")
    @Log(title = "删除系统菜单", businessType = BusinessType.DELETE)
    public AjaxResult<?> del(@RequestParam Long id, @RequestParam String deleteReason){
        return toAjax(sysMenuService.del(id, deleteReason));
    }

    @GetMapping("/list")
    @ApiOperation(value = "菜单列表")
    @PreAuthorize("@ss.hasPermi('sys:menu:list')")
    public AjaxResult<?> menuList(SysMenu sysMenu){
        log.info("SysMenuController - list sysMenu:{}", sysMenu);
        List<SysMenu> list = sysMenuService.queryList(sysMenu);
        // 由于children属性存在，前端菜单树会加载children，故此处不处理，前端处理树结构，如果没有子节点，会删掉children属性
//        list = sysMenuService.buildMenuTree(list, 0L);
        return AjaxResult.success(list);
    }

    @GetMapping("/tree")
    @ApiOperation(value = "菜单树")
    @PreAuthorize("@ss.hasPermi('sys:menu:list')")
    public AjaxResult<?> tree(){
        List<SysMenu> menuTree = sysMenuService.tree();
        return AjaxResult.success(menuTree);
    }

    @ApiOperation(value = "角色授权菜单树")
    @GetMapping("/treeForGrant")
    @PreAuthorize("@ss.hasPermi('sys:menu:list')")
    public AjaxResult<?> treeForGrant(){
        List<SysMenu> menuTree = sysMenuService.treeForGrant();
        return AjaxResult.success(menuTree);
    }
}
