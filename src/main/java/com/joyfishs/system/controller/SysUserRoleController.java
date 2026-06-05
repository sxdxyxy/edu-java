package com.joyfishs.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.service.SysUserRoleService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/user/role")
public class SysUserRoleController extends BaseController {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    // 新增
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('sys:user:role:edit')")
    @Log(title = "系统用户角色新增", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestParam Long userId, @RequestParam String roleIds){
        log.info("SysUserRoleController - add userId:{}", userId);
        log.info("SysUserRoleController - add roleIds:{}", roleIds);

        if(StringUtils.isEmpty(roleIds)) return AjaxResult.error("角色ID不能为空");

        List<Long> roleIdList = Arrays.asList(roleIds.split(",")).stream().map(roleIdStr -> Long.parseLong(roleIdStr)).collect(Collectors.toList());

        return toAjax(sysUserRoleService.save(userId, roleIdList));
    }

    // 删除
    @PostMapping("/del")
    @PreAuthorize("@ss.hasPermi('sys:user:role:edit')")
    @Log(title = "系统用户角色删除", businessType = BusinessType.DELETE)
    public AjaxResult<?> del(@RequestParam Long userId, @RequestParam String roleIds){
        log.info("SysUserRoleController - del userId:{}", userId);
        log.info("SysUserRoleController - del roleIds:{}", roleIds);

        if(StringUtils.isNull(roleIds)) return AjaxResult.error("角色ID不能为空");

        List<Long> roleIdList = Arrays.asList(roleIds.split(",")).stream().map(roleIdStr -> Long.parseLong(roleIdStr)).collect(Collectors.toList());

        return toAjax(sysUserRoleService.delByUserIdAndRoleId(userId, roleIdList));
    }

    // 根据用户ID查询角色信息
    @GetMapping("/findRoleList")
    @PreAuthorize("@ss.hasPermi('sys:user:role:list')")
    @Log(title = "系统用户角色查询-根据角色ID查询角色", businessType = BusinessType.SELECT)
    public List<SysRole> findRoleList(@RequestParam Long userId){
        log.info("SysUserRoleController - findRoleList userId:{}", userId);

        if(StringUtils.isNull(userId)) return new ArrayList<>();

        return sysUserRoleService.findRoleListByUserId(userId);
    }

    // 根据角色ID查询用户信息
    @GetMapping("/findUserList")
    @PreAuthorize("@ss.hasPermi('sys:user:role:list')")
    @Log(title = "系统用户角色查询-根据角色ID查询用户", businessType = BusinessType.SELECT)
    public List<SysUser> findUserList(@RequestParam Long roleId){
        log.info("SysUserRoleController - findUserList roleId:{}", roleId);

        if(StringUtils.isNull(roleId)) return new ArrayList<>();

        return sysUserRoleService.findUserListByRoleId(roleId);
    }

}
