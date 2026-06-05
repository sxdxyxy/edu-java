package com.joyfishs.dawa.archives.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.archives.domain.ArchivesGraduationCertificate;
import com.joyfishs.dawa.archives.service.ArchivesCertificateService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/archives/graduation")
public class GraduationCertificateController extends BaseController {

    @Autowired
    private ArchivesCertificateService archivesCertificateService;

    @GetMapping("/list")
    public TableDataInfo<?> list(
            @RequestParam(value = "personName", required = false, defaultValue = "") String personName,
            @RequestParam(value = "projectName", required = false, defaultValue = "") String projectName) {
        log.info("GraduationCertificateController - list called with personName={}, projectName={}", personName, projectName);
        startPage();
        List<ArchivesGraduationCertificate> list = archivesCertificateService.graduationListPage(personName, projectName);
        return getDataTable(list);
    }
}
