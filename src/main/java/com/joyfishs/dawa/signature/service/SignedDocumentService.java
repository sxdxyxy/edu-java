package com.joyfishs.dawa.signature.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.google.common.collect.Lists;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.enums.WorkType;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.signature.domain.bo.SignedDocumentBo;
import com.joyfishs.dawa.signature.domain.vo.*;
import com.joyfishs.dawa.signature.entity.JobsSignatureConfiguration;
import com.joyfishs.dawa.signature.entity.OnePersonOneArchives;
import com.joyfishs.dawa.signature.entity.SignedDocument;
import com.joyfishs.dawa.signature.mapper.SignedDocumentMapper;
import com.joyfishs.oss.domain.UploadResult;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.config.CosConfig;
import com.qcloud.cos.model.ciModel.job.FileProcessJobResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author yangkaifeng
 */
@Service
@RequiredArgsConstructor
public class SignedDocumentService extends ServiceImpl<SignedDocumentMapper, SignedDocument> {
    private final OnePersonOneArchivesService onePersonOneArchivesService;
    private final JobsSignatureConfigurationService jobsSignatureConfigurationService;
    private final SysOssService ossService;
    private final SysOrgService orgService;
    private final PersonService personService;
    @Resource
    private CosConfig cosConfig;

    public OnePersonOneArchivesVo queryList(Person person) {
        OnePersonOneArchives archive = onePersonOneArchivesService.find(person.getOrgId(), person.getId(), person.getWorkType());
        if (ObjectUtil.isNull(archive)) {
            archive = new OnePersonOneArchives();
            archive.setNumber(onePersonOneArchivesService.getNumber());
            archive.setOrgId(person.getOrgId());
            archive.setPersonId(person.getId());
            archive.setWorkType(person.getWorkType());
            archive.setPersonName(person.getName());
            archive.setCreateTime(LocalDateTime.now());
            onePersonOneArchivesService.save(archive);
            List<JobsSignatureConfigurationVo> signatureConfigurationList = jobsSignatureConfigurationService.queryList(archive.getOrgId(), archive.getWorkType(), (Long) null);
            List<SignedDocumentVo> signedDocumentList = Lists.newArrayList();
            OnePersonOneArchives finalArchive = archive;
            signatureConfigurationList.forEach(config -> {
                SignedDocumentVo vo = new SignedDocumentVo();
                vo.setDocumentName(config.getDocumentName());
                vo.setConfigurationId(config.getId());
                vo.setArchivesId(finalArchive.getId());
                vo.setPersonId(person.getId());
                vo.setTemplateFileUrl(config.getFileUrl());
                signedDocumentList.add(vo);
            });
            OnePersonOneArchivesVo result = new OnePersonOneArchivesVo();
            BeanUtil.copyProperties(archive, result);
            result.setSignedDocument(signedDocumentList);
            return result;
        }
        List<SignedDocument> list = selectByArchiveId(archive.getId());
        List<Long> configIds = list.stream().map(SignedDocument::getConfigurationId).collect(Collectors.toList());
        List<JobsSignatureConfiguration> signatureConfigurationList = jobsSignatureConfigurationService.queryList(archive.getOrgId(), archive.getWorkType(), configIds);
        List<SignedDocumentVo> signedDocumentList = Lists.newArrayList();
        list.forEach(item -> {
            SignedDocumentVo vo = new SignedDocumentVo();
            BeanUtil.copyProperties(item, vo);
            signedDocumentList.add(vo);
        });
        OnePersonOneArchives finalArchive = archive;
        signatureConfigurationList.forEach(item -> {
            SignedDocumentVo vo = new SignedDocumentVo();
            vo.setConfigurationId(item.getId());
            vo.setArchivesId(finalArchive.getId());
            vo.setDocumentName(item.getDocumentName());
            vo.setTemplateFileUrl(item.getFileUrl());
            signedDocumentList.add(vo);
        });
        OnePersonOneArchivesVo result = new OnePersonOneArchivesVo();
        BeanUtil.copyProperties(archive, result);
        result.setSignedDocument(signedDocumentList);
        return result;
    }

    public SignedDocument build(SignedDocumentBo bo, Long personId, boolean isTemporary) {
        JobsSignatureConfiguration configuration = jobsSignatureConfigurationService.getById(bo.getConfigurationId());
        SignedDocument signedDocument = new SignedDocument();
        if (ObjectUtil.isNotNull(bo.getId())) {
            signedDocument = getById(bo.getId());
        } else {
            signedDocument.setArchivesId(bo.getArchivesId());
            signedDocument.setDocumentName(configuration.getDocumentName());
            signedDocument.setConfigurationId(bo.getConfigurationId());
            signedDocument.setPersonId(personId);
            signedDocument.setCreateTime(LocalDateTime.now());
        }
        signedDocument.setSignatureImageName(bo.getSignatureImageName());
        signedDocument.setSignatureImageUrl(bo.getSignatureImageUrl());
        // 生成签署后的文件
        File templateFile = ossService.download(configuration.getFileName());
        XWPFTemplate template = XWPFTemplate.compile(templateFile);
        OnePersonOneArchives archive = onePersonOneArchivesService.getById(bo.getArchivesId());
        template.render(buildWordModelVo(personService.getById(personId), archive, signedDocument));
        File outputDocx = FileUtil.createTempFile(OnePersonOneArchives.DOCX, true);
        try {
            template.writeAndClose(new FileOutputStream(outputDocx));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String signedFileKey = (isTemporary ? CosConfig.TEMPORARY : CosConfig.SignedDocument) + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + IdUtil.objectId() + StrUtil.DOT + OnePersonOneArchives.DOCX;
        UploadResult uploadResult = ossService.upload(outputDocx, signedFileKey);
        signedDocument.setFileUrl(uploadResult.getUrl());
        signedDocument.setFileName(uploadResult.getFileKey());
        if (!isTemporary) {
            saveOrUpdate(signedDocument);
        }
        return signedDocument;
    }

    /**
     * 重新批量构建文档
     *
     * @param archive
     */
    public void rebuild(OnePersonOneArchives archive) {
        List<SignedDocument> list = selectByArchiveId(archive.getId());
        list.forEach(doc -> {
            rebuild(doc);
        });
    }

    private SignedDocument rebuild(SignedDocument doc) {
        JobsSignatureConfiguration configuration = jobsSignatureConfigurationService.getById(doc.getConfigurationId());
        // 生成签署后的文件
        File templateFile = ossService.download(configuration.getFileName());
        XWPFTemplate template = XWPFTemplate.compile(templateFile);
        OnePersonOneArchives archive = onePersonOneArchivesService.getById(doc.getArchivesId());
        template.render(buildWordModelVo(personService.getById(doc.getPersonId()), archive, doc));
        File outputDocx = FileUtil.createTempFile(OnePersonOneArchives.DOCX, true);
        try {
            template.writeAndClose(new FileOutputStream(outputDocx));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String signedFileKey = CosConfig.SignedDocument + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + IdUtil.objectId() + StrUtil.DOT + OnePersonOneArchives.DOCX;
        UploadResult uploadResult = ossService.upload(outputDocx, signedFileKey);
        doc.setFileUrl(uploadResult.getUrl());
        doc.setFileName(uploadResult.getFileKey());
        updateById(doc);
        return doc;
    }

    private WordModelVo buildWordModelVo(Person person, OnePersonOneArchives archives, SignedDocument signedDocument) {
        WordModelVo modelVo = new WordModelVo();
        if (ObjectUtil.isNotNull(signedDocument)) {
            modelVo.setSignImage(Pictures.ofUrl(signedDocument.getSignatureImageUrl()).size(300, 100).create());
        }
        modelVo.setNumber(archives.getNumber());
        SysOrg org = orgService.findUnitByOrgId(archives.getOrgId());
        modelVo.setOrgName(ObjectUtil.isNotNull(org) ? org.getName() : "");
        modelVo.setWorkTypeName(WorkType.value(archives.getWorkType()).getDesc());
        modelVo.setPersonName(archives.getPersonName());
        modelVo.setSex(person.getSex() == 1 ? "男" : "女");
        modelVo.setIdCardNo(person.getIdCardNo());
        modelVo.setPhone(person.getPhone());
        modelVo.setHomeAddress(person.getHomeAddress());
        modelVo.setEmergencyContact(person.getEmergencyContact());
        modelVo.setEmergencyContactPhone(person.getEmergencyContactPhone());
        modelVo.setTeamName(archives.getTeamName());
        modelVo.setProjectName(archives.getProjectName());
        if (ObjectUtil.isNotNull(archives.getEntryTime())) {
            modelVo.setEntryTime(LocalDateTimeUtil.format(archives.getEntryTime(), DatePattern.CHINESE_DATE_PATTERN));
        }
        if (ObjectUtil.isNotNull(signedDocument)) {
            modelVo.setSignDate(LocalDateTimeUtil.format(signedDocument.getCreateTime(), DatePattern.CHINESE_DATE_PATTERN));
        } else {
            modelVo.setSignDate(LocalDateTimeUtil.format(LocalDate.now(), DatePattern.CHINESE_DATE_PATTERN));
        }
        modelVo.setTeamName(archives.getTeamName());
        return modelVo;
    }

    public List<SignedDocument> selectByArchiveId(Long archiveId) {
        LambdaQueryWrapper<SignedDocument> lqw = Wrappers.lambdaQuery();
        lqw.eq(SignedDocument::getArchivesId, archiveId);
        return list(lqw);
    }

    public boolean syncCompressedJob(OnePersonOneArchives archive) {
        FileProcessJobResponse response = ossService.describeFileProcessJob(archive.getJobId());
        if (response.getJobDetail().getState().equals("Success")) {
            archive.setJobComplete(Boolean.TRUE);
            onePersonOneArchivesService.updateById(archive);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 压缩档案文件
     *
     * @param archive
     */
    public void archiveCompress(OnePersonOneArchives archive) {
        List<SignedDocument> signedDocs = selectByArchiveId(archive.getId());
        SignedDocument catalogueDocument = new SignedDocument();
        catalogueDocument.setDocumentName("信息目录");
        catalogueDocument.setFileName(archive.getCatalogueName());
        signedDocs.add(catalogueDocument);
        String signedFileKey = CosConfig.ARCHIVES_ZIP + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + IdUtil.objectId() + StrUtil.DOT + OnePersonOneArchives.ZIP;
        FileProcessJobResponse res = ossService.fileCompress(signedDocs, signedFileKey);
        archive.setJobId(res.getJobDetail().getJobId());
        archive.setJobComplete(Boolean.FALSE);
        archive.setFileName(signedFileKey);
        archive.setFileUrl(cosConfig.getDomain() + signedFileKey);
        onePersonOneArchivesService.updateById(archive);
    }

    /**
     * 生成目录
     */
    public void createCatalogue(OnePersonOneArchives archive) {
        List<SignedDocument> signedDocs = selectByArchiveId(archive.getId());
        List<CatalogueVo> tableData = Lists.newArrayList();
        for (int i = 0; i < signedDocs.size(); i++) {
            tableData.add(new CatalogueVo(i + 1, signedDocs.get(i).getDocumentName()));
        }
        LoopRowTableRenderPolicy rowTableRenderPolicy = new LoopRowTableRenderPolicy();
        Configure configure = Configure.builder().bind("catalogue", rowTableRenderPolicy).build();

        File templateFile = ossService.download(OnePersonOneArchives.CATALOGUE_TEMPLATE);
        XWPFTemplate template = XWPFTemplate.compile(templateFile, configure);
        template.render(new HashMap<String, Object>() {{
            put("projectName", archive.getProjectName());
            put("orgName", archive.getTeamName());
            put("personName", archive.getPersonName());
            put("number", archive.getNumber());
            put("workTypeName", WorkType.value(archive.getWorkType()).getDesc());
            put("catalogue", tableData);
        }});
        File outputDocx = FileUtil.createTempFile(OnePersonOneArchives.DOCX, true);
        try {
            template.writeAndClose(new FileOutputStream(outputDocx));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String signedFileKey = CosConfig.SignedDocument + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + IdUtil.objectId() + StrUtil.DOT + OnePersonOneArchives.DOCX;
        UploadResult uploadResult = ossService.upload(outputDocx, signedFileKey);
        archive.setCatalogueUrl(uploadResult.getUrl());
        archive.setCatalogueName(uploadResult.getFileKey());
        onePersonOneArchivesService.updateById(archive);
    }

    public static void main(String[] args) throws IOException {
        LoopRowTableRenderPolicy rowTableRenderPolicy = new LoopRowTableRenderPolicy();
        Configure configure = Configure.builder().bind("catalogue", rowTableRenderPolicy).build();
        File file = new File("D:\\project\\template.docx");
        List<CatalogueVo> tableData = Lists.newArrayList();
        tableData.add(new CatalogueVo(1, "农民工进场通知单"));
        tableData.add(new CatalogueVo(2, "人员信息登记表"));
        tableData.add(new CatalogueVo(3, "身份证复印件"));
        tableData.add(new CatalogueVo(4, "体检报告"));
        tableData.add(new CatalogueVo(5, "新工人入场三级安全教育登记表"));
        tableData.add(new CatalogueVo(6, "公司（第一级）安全教育记录"));
        tableData.add(new CatalogueVo(7, "项目部（第二级）安全教育记录"));
        tableData.add(new CatalogueVo(8, "班组（第三级）安全教育记录"));
        tableData.add(new CatalogueVo(9, "安全教育培训试卷"));
        tableData.add(new CatalogueVo(10, "安全操作规程"));
        tableData.add(new CatalogueVo(11, "应急知识教育登记表"));
        tableData.add(new CatalogueVo(12, "专题安全教育登记表"));
        tableData.add(new CatalogueVo(13, "日常安全教育登记表"));
        XWPFTemplate template = XWPFTemplate.compile(file, configure).render(new HashMap<String, Object>() {{
            put("projectName", "超级工程项目");
            put("orgName", "施工单位名称");
            put("personName", "张三丰");
            put("catalogue", tableData);
        }});
        template.writeAndClose(new FileOutputStream("D:\\project\\output.docx"));
    }
}
