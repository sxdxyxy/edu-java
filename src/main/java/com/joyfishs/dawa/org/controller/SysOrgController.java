package com.joyfishs.dawa.org.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.entity.SysOrgRole;
import com.joyfishs.dawa.org.service.SysOrgRoleService;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.config.validation.Update;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

@Api(tags = "组织机构管理")
@Slf4j
@RestController
@RequestMapping("sys/org")
public class SysOrgController extends BaseController {

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SysOrgRoleService sysOrgRoleService;

    @ApiOperation(value = "新增组织机构")
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('sys:org:edit')")
    @Log(title = "新增组织机构", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody @Validated SysOrg sysOrg){
        return toAjax(sysOrgService.add(sysOrg));
    }

    @ApiOperation(value = "编辑保存组织机构")
    @PostMapping("/edit")
    @PreAuthorize("@ss.hasPermi('sys:org:edit')")
    @Log(title = "编辑组织机构", businessType = BusinessType.UPDATE)
    public AjaxResult<?> edit(@RequestBody @Validated(Update.class) SysOrg sysOrg){
        return toAjax(sysOrgService.edit(sysOrg));
    }

    @ApiOperation(value = "创建机构小程序码")
    @PostMapping("/createWxaCode")
    @PreAuthorize("@ss.hasPermi('sys:org:edit')")
    @Log(title = "创建机构小程序码", businessType = BusinessType.UPDATE)
    public R<String> createWxaCode(@RequestParam Long id) throws WxErrorException {
        return R.ok("创建小程序码完成", sysOrgService.createWxaCode(id));
    }

    @ApiOperation(value = "删除组织机构")
    @PostMapping("/del")
    @PreAuthorize("@ss.hasPermi('sys:org:del')")
    @Log(title = "删除组织机构", businessType = BusinessType.DELETE)
    public AjaxResult<?> del(@RequestParam Long id, @RequestParam(required = false) String deleteReason){
        return toAjax(sysOrgService.del(id, deleteReason));
    }

    @ApiOperation(value = "分页查询组织机构")
    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('sys:org:page')")
    @Log(title = "分页查询组织机构", businessType = BusinessType.SELECT)
    public TableDataInfo<?> pageList(SysOrg sysOrg){
        startPage();
        List<SysOrg> list = sysOrgService.queryList(sysOrg);
        return getDataTable(list);
    }

    @ApiOperation(value = "查询所有组织机构列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sys:org:list')")
    @Log(title = "查询所有组织机构", businessType = BusinessType.SELECT)
    public AjaxResult<?> orgList(SysOrg sysOrg){
        List<SysOrg> list = sysOrgService.queryList(sysOrg);
        // 由于children属性存在，前端组织机构树会加载children，故此处不处理，前端处理树结构，如果没有子节点，会删掉children属性
//        list = sysOrgService.buildOrgTree(list, 0L);
        return AjaxResult.success(list);
    }

    /**
     * 获取项目部内部角色列表
     */
    @GetMapping("/roles/{orgId}")
    @PreAuthorize("@ss.hasPermi('sys:org:list')")
    public AjaxResult<?> getOrgRoles(@PathVariable Long orgId) {
        List<SysOrgRole> list = sysOrgRoleService.listByOrgId(orgId);
        return AjaxResult.success(list);
    }

    /**
     * 新增项目部内部角色
     */
    @PostMapping("/role")
    @PreAuthorize("@ss.hasPermi('sys:org:edit')")
    public AjaxResult<?> addOrgRole(@RequestBody SysOrgRole role) {
        role.setCreateTime(java.time.LocalDateTime.now());
        return toAjax(sysOrgRoleService.addRole(role));
    }

    /**
     * 更新项目部内部角色
     */
    @PutMapping("/role")
    @PreAuthorize("@ss.hasPermi('sys:org:edit')")
    public AjaxResult<?> updateOrgRole(@RequestBody SysOrgRole role) {
        return toAjax(sysOrgRoleService.updateRole(role));
    }

    /**
     * 删除项目部内部角色
     */
    @DeleteMapping("/role/{id}")
    @PreAuthorize("@ss.hasPermi('sys:org:del')")
    public AjaxResult<?> deleteOrgRole(@PathVariable Long id) {
        return toAjax(sysOrgRoleService.deleteRole(id));
    }
}
