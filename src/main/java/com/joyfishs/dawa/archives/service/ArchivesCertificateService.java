package com.joyfishs.dawa.archives.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.archives.domain.ArchivesCertificate;
import com.joyfishs.dawa.archives.domain.ArchivesGraduationCertificate;
import com.joyfishs.dawa.archives.mapper.ArchivesCertificateMapper;
import com.joyfishs.dawa.org.service.SysOrgService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ArchivesCertificateService extends ServiceImpl<ArchivesCertificateMapper, ArchivesCertificate> {

    @Autowired
    private SysOrgService sysOrgService;

    public List<ArchivesCertificate> listPage(Long orgId, Integer orgType, Integer type){
        List<ArchivesCertificate> certificateList = baseMapper.listPage(orgId, orgType, type);
        for (ArchivesCertificate archivesCertificate : certificateList) {
            archivesCertificate.setDwOrgName(sysOrgService.findUnitByOrgId(archivesCertificate.getPersionOrgId()).getName());
        }
        return certificateList;
    }

    public List<ArchivesGraduationCertificate> graduationListPage(String personName, String projectName) {
        return baseMapper.graduationListPage(personName, projectName);
    }

}
