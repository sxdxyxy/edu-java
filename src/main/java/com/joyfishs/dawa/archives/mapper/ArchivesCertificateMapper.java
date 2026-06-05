package com.joyfishs.dawa.archives.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.archives.domain.ArchivesCertificate;
import com.joyfishs.dawa.archives.domain.ArchivesGraduationCertificate;

@Mapper
public interface ArchivesCertificateMapper extends BaseMapper<ArchivesCertificate> {

    public List<ArchivesCertificate> listPage(@Param("orgId") Long orgId, @Param("orgType") Integer orgType,
                                              @Param("type") Integer type);

    List<ArchivesGraduationCertificate> graduationListPage(
            @Param("personName") String personName,
            @Param("projectName") String projectName);

}
