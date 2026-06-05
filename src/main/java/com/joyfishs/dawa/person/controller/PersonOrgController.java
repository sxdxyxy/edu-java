package com.joyfishs.dawa.person.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.person.domain.param.PersonOrgAddParam;
import com.joyfishs.dawa.person.service.PersonOrgService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/person/org")
public class PersonOrgController extends BaseController {

    @Autowired
    private PersonOrgService personOrgService;

    // 根据项目查询用户
    @GetMapping("/listByOrgId")
    @PreAuthorize("@ss.hasPermi('person:org:list')")
    @Log(title = "根据项目查询用户", businessType = BusinessType.SELECT)
    public AjaxResult<?> listByOrgId(@RequestParam Long orgId) {
        log.info("XmPersonController - listByOrgId ids:{}", orgId);
        return AjaxResult.success(personOrgService.listByOrgId(orgId));
    }

    // 新增人员项目关联
    @PostMapping("/addPersonOrg")
    @PreAuthorize("@ss.hasPermi('person:org:add')")
    @Log(title = "新增人员项目关联", businessType = BusinessType.INSERT)
    public AjaxResult<?> addPersonOrg(@RequestBody PersonOrgAddParam param) {
        log.info("XmPersonController - addPersonOrg PersonOrgAddParam:{}", param);
        return AjaxResult.success(personOrgService.addPersonOrg(param));
    }

    // 移除人员项目关联
    @PostMapping("/removePersonOrg")
    @PreAuthorize("@ss.hasPermi('person:org:remove')")
    @Log(title = "移除人员项目关联", businessType = BusinessType.INSERT)
    public AjaxResult<?> removePersonOrg(@RequestParam Long orgId, @RequestParam Long personId) {
        log.info("XmPersonController - removePersonOrg orgId:{}", orgId);
        log.info("XmPersonController - removePersonOrg personId:{}", personId);
        return AjaxResult.success(personOrgService.removePersonOrg(orgId, personId));
    }
}
