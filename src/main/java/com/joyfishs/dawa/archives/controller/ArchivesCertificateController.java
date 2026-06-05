package com.joyfishs.dawa.archives.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.archives.domain.*;
import com.joyfishs.dawa.archives.service.ArchivesCertificateService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

/** 证书档案 **/
@Slf4j
@RestController
@RequestMapping("/archives/certificate")
public class ArchivesCertificateController extends BaseController {

    @Autowired
    private ArchivesCertificateService archivesCertificateService;

    @GetMapping("/listPage")
    @PreAuthorize("@ss.hasPermi('archives:certificate:list')")
    @Log(title = "获取证书档案列表", businessType = BusinessType.SELECT)
    public TableDataInfo<?> listPage(
            @RequestParam(value = "orgId", required = false, defaultValue = "") Long orgId,
            @RequestParam(value = "orgType", required = false, defaultValue = "") Integer orgType,
            @RequestParam(value = "type", required = false, defaultValue = "") Integer type){
        startPage();
        List<ArchivesCertificate> list = archivesCertificateService.listPage(orgId, orgType, type);
        return getDataTable(list);
    }


}
