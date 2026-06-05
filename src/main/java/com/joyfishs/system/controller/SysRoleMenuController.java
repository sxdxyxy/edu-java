package com.joyfishs.system.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.entity.SysMenu;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.service.SysRoleMenuService;
import com.joyfishs.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/role/menu")
public class SysRoleMenuController extends BaseController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

//    @PostMapping("add")
//    @PreAuthorize("@ss.hasPermi('sys:role:menu:edit')")
//    @Log(title = "系统角色菜单新增", businessType = BusinessType.INSERT)
//    public AjaxResult<?> add(@RequestParam Long roleId, @RequestParam String menuIds){
//        log.info("SysRoleMenuController - add roleId:{}", roleId);
//        log.info("SysRoleMenuController - add menuIds:{}", menuIds);
//
//        if(StringUtils.isNull(roleId)) return AjaxResult.error("角色ID不能为空");
//
//        List<Long> MenuIdList;
//        if(StringUtils.isNotNull(menuIds))
//            MenuIdList = Arrays.asList(menuIds.split(",")).stream().map(menuIdStr -> Long.parseLong(menuIdStr)).collect(Collectors.toList());
//        else
//            MenuIdList = new ArrayList<>();
//
//        return toAjax(sysRoleMenuService.save(roleId, MenuIdList));
//    }

    @GetMapping("/findMenuList")
    @PreAuthorize("@ss.hasPermi('sys:role:menu:list')")
    @Log(title = "系统角色菜单查询-根据角色ID查询菜单", businessType = BusinessType.SELECT)
    public List<SysMenu> findMenuList(@RequestParam Long roleId){
        log.info("SysRoleMenuController - findMenu roleId:{}", roleId);

        if(StringUtils.isNull(roleId)) return new ArrayList<>();

        return sysRoleMenuService.findMenuListByRoleId(roleId);
    }

//    @GetMapping("/findRoleList")
//    @PreAuthorize("@ss.hasPermi('sys:role:menu:list')")
//    @Log(title = "系统角色菜单查询-根据菜单ID查询角色", businessType = BusinessType.SELECT)
//    public List<SysRole> findRoleList(@RequestParam Long menuId){
//        log.info("SysRoleMenuController - findRole menuId:{}", menuId);
//
//        if(StringUtils.isNull(menuId)) return new ArrayList<>();
//
//        return sysRoleMenuService.findRoleListByMenuId(menuId);
//    }



}
