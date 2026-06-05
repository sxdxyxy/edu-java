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
import com.joyfishs.system.entity.PasswordBody;
import com.joyfishs.system.params.AddUserParam;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.params.SysUserGrantRoleParam;
import com.joyfishs.system.service.SysUserService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/user")
public class SysUserController extends BaseController{

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('sys:user:add')")
    @Log(title = "系统用户新增", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Valid AddUserParam param){
        SysUser sysUser = new SysUser();
        sysUser.setName(param.getName());
        sysUser.setUserName(param.getUserName());
        sysUser.setPassword(param.getPassword());
        sysUser.setNickName(param.getNickName());
        sysUser.setEmail(param.getEmail());
        sysUser.setPhone(param.getPhone());
        sysUser.setSex(param.getSex() != null ? param.getSex() : 0);
        sysUser.setAvatar(param.getAvatar());
        sysUser.setIdCardNo(param.getIdCardNo());
        log.info("SysUserController - add sysUser:{}", sysUser);

        return toAjax(sysUserService.saveOrUpdate(sysUser));
    }

    @PostMapping("/edit")
    @PreAuthorize("@ss.hasPermi('sys:user:edit')")
    @Log(title = "系统用户修改", businessType = BusinessType.UPDATE)
    public AjaxResult<?> edit(@RequestBody @Validated(Update.class) SysUser sysUser){
        log.info("SysUserController - edit sysUser:{}", sysUser);

        return toAjax(sysUserService.saveOrUpdate(sysUser));
    }

    @PostMapping("/del")
    @PreAuthorize("@ss.hasPermi('sys:user:del')")
    @Log(title = "系统用户删除", businessType = BusinessType.DELETE)
    public AjaxResult<?> del(String ids, String deleteReason){
        log.info("SysUserController - del ids:{}", ids);
        log.info("SysUserController - del deleteReason:{}", deleteReason);

        if(StringUtils.isEmpty(ids)) return AjaxResult.success();

        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Long.parseLong(idStr)).collect(Collectors.toList());

        return toAjax(sysUserService.del(idList, deleteReason));
    }

    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('sys:user:list')")
    @Log(title = "系统用户查询", businessType = BusinessType.SELECT)
    public TableDataInfo<?> pageList(SysUser sysUser){
        log.info("SysUserController - pageList sysUser:{}", sysUser);

        startPage();
        List<SysUser> list = sysUserService.findList(sysUser);

        return getDataTable(list);
    }


    // 查询用户拥有角色
    @PostMapping("/ownRole")
    @PreAuthorize("@ss.hasPermi('sys:user:ownRole')")
    @Log(title = "用户_拥有角色", businessType = BusinessType.SELECT)
    public AjaxResult<?> ownRole(Long id){
        log.info("SysUserController - ownMenu id:{}", id);

        return AjaxResult.success(sysUserService.ownRole(id));
    }

    // 用户授权角色
    @PostMapping("/grantRole")
    @PreAuthorize("@ss.hasPermi('sys:user:grantRole')")
    @Log(title = "用户授权角色", businessType = BusinessType.SELECT)
    public AjaxResult<?> grantRole(@RequestBody @Validated SysUserGrantRoleParam param){
        log.info("SysUserController - grantMenu id:{}, grantRoleIdList{}", param.getId(), param.getGrantRoleIdList());

        sysUserService.grantRole(param.getId(), param.getGrantRoleIdList());
        return AjaxResult.success();
    }

    // 重置密码
    @GetMapping("/resetPwd")
    @PreAuthorize("@ss.hasPermi('sys:user:resetPwd')")
    @Log(title = "重置为默认密码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> resetPwd(@RequestParam Long id) {
        log.info("SysUserController - resetPwd id:{}", id);

        return toAjax(sysUserService.resetPwd(id));
    }

    @GetMapping("/changePwd")
    @Log(title = "修改密码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> changePwd(@RequestParam String oldPassword, @RequestParam("password") String password) {
        // 安全修复: 此端点已在URL中暴露密码,建议改用POST /resetPassword 方式
        log.warn("changePwd called with password in query string - consider using POST /resetPassword");
        return toAjax(sysUserService.changePwd(SecurityUtil.getUserId(), oldPassword, password));
    }

    @PostMapping("/resetPassword")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> resetPassword(@RequestBody @Validated PasswordBody body) {
        SysUser sysUser = sysUserService.getById(SecurityUtil.getUserId());
        return toAjax(sysUserService.changePassword(sysUser, body.getPassword()));
    }
}
