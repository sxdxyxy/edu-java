package com.joyfishs.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.config.validation.Update;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.params.SysRoleGrantMenuParam;
import com.joyfishs.system.service.SysRoleService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    // 新增角色
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('sys:role:add')")
    @Log(title = "新增角色", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Validated SysRole sysRole){
        log.info("SysRoleController - add sysRole:{}", sysRole);

        return toAjax(sysRoleService.saveOrUpdate(sysRole));
    }

    // 编辑角色
    @PostMapping("/edit")
    @PreAuthorize("@ss.hasPermi('sys:role:edit')")
    @Log(title = "编辑角色", businessType = BusinessType.UPDATE)
    public AjaxResult<?> edit(@RequestBody @Validated(Update.class) SysRole sysRole){
        log.info("SysRoleController - edit sysRole:{}", sysRole);

        return toAjax(sysRoleService.saveOrUpdate(sysRole));
    }

    // 删除角色
    @PostMapping("/del")
    @PreAuthorize("@ss.hasPermi('sys:role:del')")
    @Log(title = "删除角色", businessType = BusinessType.DELETE)
    public AjaxResult<?> del(@RequestParam Long id){
        log.info("SysRoleController - del id:{}", id);

        return toAjax(sysRoleService.del(id));
    }

    // 角色分页列表
    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('sys:role:list')")
    @Log(title = "查询角色", businessType = BusinessType.SELECT)
    public TableDataInfo<?> pageList(SysRole sysRole){
        log.info("SysRoleController - pageList sysRole:{}", sysRole);

        startPage();
        List<SysRole> list = sysRoleService.findList(sysRole);
        log.info("SysRoleController - pageList list:{}", list);

        return getDataTable(list);
    }

    // 角色列表
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sys:role:list')")
    @Log(title = "查询角色", businessType = BusinessType.SELECT)
    public AjaxResult<?> list(){
        log.info("SysRoleController - list ");

        List<SysRole> list = sysRoleService.findList(new SysRole());
        log.info("SysRoleController - list list:{}", list);

        return AjaxResult.success(list);
    }

    // 查询角色拥有菜单
    @PostMapping("/ownMenu")
    @PreAuthorize("@ss.hasPermi('sys:role:ownMenu')")
    @Log(title = "角色拥有菜单", businessType = BusinessType.SELECT)
    public AjaxResult<?> ownMenu(Long id){
        return AjaxResult.success(sysRoleService.ownMenu(id));
    }

    // 角色授权菜单
    @PostMapping("/grantMenu")
    @PreAuthorize("@ss.hasPermi('sys:role:grantMenu')")
    @Log(title = "角色授权菜单", businessType = BusinessType.SELECT)
    public AjaxResult<?> grantMenu(@RequestBody @Validated SysRoleGrantMenuParam sysRoleGrantMenuParam){
        log.info("SysRoleController - grantMenu id:{}, grantMenuIdList{}", sysRoleGrantMenuParam.getId(), sysRoleGrantMenuParam.getGrantMenuIdList());

        sysRoleService.grantMenu(sysRoleGrantMenuParam.getId(), sysRoleGrantMenuParam.getGrantMenuIdList());
        return AjaxResult.success();
    }

}
