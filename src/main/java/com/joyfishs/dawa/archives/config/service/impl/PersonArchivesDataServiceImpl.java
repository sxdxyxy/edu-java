package com.joyfishs.dawa.archives.config.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.Pictures;
import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.service.AnswerReportService;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesConfig;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesData;
import com.joyfishs.dawa.archives.config.mapper.PersonArchivesDataMapper;
import com.joyfishs.dawa.archives.config.service.PersonArchivesConfigService;
import com.joyfishs.dawa.archives.config.service.PersonArchivesDataService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.entity.PersonCertificate;
import com.joyfishs.dawa.person.service.PersonCertificateService;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.safetycode.entity.SafetyCode;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.entity.ProjectRelate;
import com.joyfishs.dawa.project.service.ProjectRelateService;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.dawa.violation.service.ViolationRecordService;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.config.CosConfig;
import com.joyfishs.utils.SecurityUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PersonArchivesDataServiceImpl extends ServiceImpl<PersonArchivesDataMapper, PersonArchivesData>
        implements PersonArchivesDataService {

    @Autowired
    private PersonArchivesConfigService configService;

    @Autowired
    private PersonService personService;

    @Autowired
    private AnswerReportService answerReportService;

    @Autowired
    private PersonCertificateService certificateService;

    @Autowired
    private ViolationRecordService violationRecordService;

    @Autowired
    private SafetyCodeService safetyCodeService;

    @Autowired
    @Lazy
    private ProjectService projectService;

    @Autowired
    @Lazy
    private ProjectRelateService projectRelateService;

    @Autowired
    @Lazy
    private SysOrgService sysOrgService;

    @Autowired
    private CosConfig cosConfig;

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Value("${joyfishs.upload-path:}")
    private String uploadPath;

    @Override
    public String generatePreviewUrl(Long id, String format) {
        log.info("Generating preview for archive data id: {}", id);
        PersonArchivesData data = getById(id);
        if (data == null) throw new RuntimeException("数据不存在");

        PersonArchivesConfig config = configService.getByIdWithCheck(data.getConfigId());
        if (config == null) throw new RuntimeException("配置方案不存在");

        Person person = personService.getById(data.getPersonId());
        if (person == null) throw new RuntimeException("人员信息不存在");

        // 确定附件存储根路径，如果未配置则使用系统临时目录
        String effectiveUploadPath = uploadPath;
        if (StrUtil.isEmpty(effectiveUploadPath)) {
            effectiveUploadPath = System.getProperty("java.io.tmpdir") + "/joyfishs-uploads";
            log.info("joyfishs.upload-path is not set, using temporary path: {}", effectiveUploadPath);
        }
        FileUtil.mkdir(effectiveUploadPath);

        // 获取静态域名配置
        String domain = cosConfig.getDomain();
        if (domain.endsWith("/")) domain = domain.substring(0, domain.length() - 1);

        // 收集所有需要合并的模板列表
        List<String> templateUrls = new ArrayList<>();
        String itemsJson = config.getArchiveItems();
        if (StrUtil.isNotEmpty(itemsJson)) {
            JSONArray items = JSON.parseArray(itemsJson);
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                String itemTpl = item.getString("templateUrl");
                if (StrUtil.isNotEmpty(itemTpl)) {
                    templateUrls.add(itemTpl);
                }
            }
        }

        // 如果分项没有任何模板，则尝试使用主模板
        if (templateUrls.isEmpty() && StrUtil.isNotEmpty(config.getDocumentTemplateUrl())) {
            templateUrls.add(config.getDocumentTemplateUrl());
        }

        if (templateUrls.isEmpty()) {
            throw new RuntimeException("当前方案未配置任何 Word 模板，无法预览");
        }

        log.info("Total templates to merge: {}", templateUrls.size());

        XWPFDocument combinedDoc = null;
        List<String> tempFiles = new ArrayList<>();

        try {
            for (String url : templateUrls) {
                // 下载/定位模板
                // 更加鲁棒的相对路径提取
                String relativePath = url;
                if (url.startsWith("http")) {
                    if (url.contains(domain)) {
                        relativePath = url.replace(domain, "");
                    } else if (url.contains("://")) {
                        // 尝试移除协议和域名部分
                        int domainEndIndex = url.indexOf("/", url.indexOf("://") + 3);
                        if (domainEndIndex != -1) {
                            relativePath = url.substring(domainEndIndex);
                        }
                    }
                }
                if (relativePath.startsWith("/")) relativePath = relativePath.substring(1);
                String templatePath = effectiveUploadPath + "/" + relativePath;

                log.info("Template source URL: {}, Disk path: {}", url, templatePath);

                if (!FileUtil.exist(templatePath)) {
                    log.warn("模板文件不存在，尝试从云端下载: {}", templatePath);
                    // 如果本地不存在，尝试基于 URL 下载到本地临时路径
                    try {
                        FileUtil.mkParentDirs(templatePath);
                        cn.hutool.http.HttpUtil.downloadFile(url, templatePath);
                    } catch (Exception downloadErr) {
                        log.error("下载模板失败: {}", url, downloadErr);
                        continue;
                    }
                }

                if (!FileUtil.exist(templatePath)) {
                    log.warn("模板文件不存在: {}", templatePath);
                    continue;
                }

                // 组装填充数据
                Map<String, Object> dataMap = buildDataMap(person, data);

                // 渲染单个文档
                String renderedFile = templatePath.replace(".docx", "_rendered_" + IdUtil.simpleUUID() + ".docx");
                XWPFTemplate template = XWPFTemplate.compile(templatePath).render(dataMap);
                try (FileOutputStream out = new FileOutputStream(renderedFile)) {
                    template.write(out);
                }
                template.close();
                tempFiles.add(renderedFile);

                // 合并到总文档
                try (FileInputStream is = new FileInputStream(renderedFile)) {
                    XWPFDocument doc = new XWPFDocument(is);
                    if (combinedDoc == null) {
                        combinedDoc = doc;
                    } else {
                        // 插入分页符
                        XWPFParagraph breakP = combinedDoc.createParagraph();
                        breakP.createRun().addBreak(org.apache.poi.xwpf.usermodel.BreakType.PAGE);

                        // 追加元素
                        for (IBodyElement element : doc.getBodyElements()) {
                            if (element instanceof XWPFParagraph) {
                                copyParagraph(combinedDoc, (XWPFParagraph) element);
                            } else if (element instanceof XWPFTable) {
                                copyTable(combinedDoc, (XWPFTable) element);
                            }
                        }
                    }
                }
            }

            if (combinedDoc == null) throw new RuntimeException("合并文档失败");

            // 保存最终文档到临时路径
            String datePath = DateUtil.format(new Date(), "yyyyMMdd");
            String fileName = IdUtil.simpleUUID() + ".docx";
            String finalRelativePath = "archives/preview/" + datePath + "/" + fileName;
            String finalPath = effectiveUploadPath + "/" + finalRelativePath;
            
            File finalFile = new File(finalPath);
            FileUtil.mkParentDirs(finalFile);
            try (FileOutputStream out = new FileOutputStream(finalFile)) {
                combinedDoc.write(out);
            }

            // 上传至腾讯云 COS
            log.info("Uploading preview doc to COS: {}", finalRelativePath);
            sysOssService.upload(finalFile, finalRelativePath);
            
            
            // 生成带腾讯云文档预览参数的链接
            // 支持 html (默认) 或 pdf
            if (StrUtil.isEmpty(format)) format = "html";
            String finalUrl = domain + "/" + finalRelativePath + "?ci-process=doc-preview&dstType=" + format;
            log.info("Preview generation success. URL: {}", finalUrl);
            
            return finalUrl;

        } catch (Exception e) {
            log.error("生成预览失败", e);
            throw new RuntimeException("生成预览失败: " + e.getMessage());
        } finally {
            // 清理临时渲染文件
            for (String f : tempFiles) { FileUtil.del(f); }
        }
    }

    /**
     * 核心逻辑：组装系统数据与动态数据 (支持图片处理)
     */
    private Map<String, Object> buildDataMap(Person person, PersonArchivesData archivesData) {
        Map<String, Object> mapper = new HashMap<>();
        
        // 1. 系统基础信息 (支持英文与中文标签)
        mapper.put("name", person.getName());
        mapper.put("姓名", person.getName());
        mapper.put("phone", person.getPhone());
        mapper.put("手机号", person.getPhone());
        mapper.put("idCard", person.getIdCardNo());
        mapper.put("idCardNo", person.getIdCardNo());
        mapper.put("身份证号", person.getIdCardNo());
        mapper.put("birthday", person.getBirthday());
        mapper.put("homeAddress", person.getHomeAddress());
        mapper.put("家庭住址", person.getHomeAddress());
        mapper.put("workType", person.getWorkTypeName());
        mapper.put("工种", person.getWorkTypeName());
        mapper.put("orgName", person.getOrgName());
        mapper.put("单位名称", person.getOrgName());
        mapper.put("gender", person.getSexStr());
        mapper.put("性别", person.getSexStr());
        
        // 1.1 集成安全监管基础数据 (违章与安全码)
        if (person.getUserId() != null) {
            // 获取累计违章记分 (仅统计已处理的)
            Integer totalViolationScore = violationRecordService.getTotalScore(person.getUserId());
            mapper.put("violationScore", totalViolationScore);
            mapper.put("累计违章记分", totalViolationScore);
            
            // 获取安全码颜色
            SafetyCode safetyCode = safetyCodeService.getStatus(person.getUserId());
            if (safetyCode != null) {
                String colorName = "未知";
                if ("green".equals(safetyCode.getColor())) colorName = "绿色 (准入)";
                else if ("yellow".equals(safetyCode.getColor())) colorName = "黄色 (整改)";
                else if ("red".equals(safetyCode.getColor())) colorName = "红色 (禁入)";
                
                mapper.put("safetyColor", colorName);
                mapper.put("安全码状态", colorName);
            }
        }

        // 2. 三级安全教育数据提取 (根据项目名称关键字匹配)
        List<AnswerReport> reports = answerReportService.list(new LambdaQueryWrapper<AnswerReport>()
            .eq(AnswerReport::getPersonId, person.getId())
            .eq(AnswerReport::getType, 2) // 考试类
            .eq(AnswerReport::getIsDelete, 0)
            .orderByDesc(AnswerReport::getSubmitTime));
        
        for (AnswerReport report : reports) {
            String projName = report.getCourseName(); // 这里的 courseName 在 AnswerReport 逻辑中可能存放的是项目名
            if (StrUtil.isEmpty(projName)) continue;
            
            String score = report.getScore() != null ? report.getScore().toString() : "0";
            String date = report.getSubmitTime() != null ? DateUtil.format(report.getSubmitTime(), "yyyy-MM-dd") : "-";
            
            if (projName.contains("公司级")) {
                mapper.put("公司级分数", score);
                mapper.put("公司级日期", date);
                mapper.put("一级分数", score);
                mapper.put("一级日期", date);
            } else if (projName.contains("项目级")) {
                mapper.put("项目级分数", score);
                mapper.put("项目级日期", date);
                mapper.put("二级分数", score);
                mapper.put("二级日期", date);
            } else if (projName.contains("班组级")) {
                mapper.put("班组级分数", score);
                mapper.put("班组级日期", date);
                mapper.put("三级分数", score);
                mapper.put("三级日期", date);
            }
        }
        
        // 3. 证书信息
        List<PersonCertificate> certs = certificateService.listByPersonId(person.getId());
        if (CollUtil.isNotEmpty(certs)) {
             PersonCertificate lastCert = certs.get(0);
             mapper.put("证书编号", lastCert.getId()); // 暂时使用 ID，如果有编号字段请替换
             mapper.put("certNo", lastCert.getId());
             mapper.put("发证日期", DateUtil.format(lastCert.getRegisterDate(), "yyyy-MM-dd"));
        }

        // 4. 图片类字段处理 (支持自动从 URL 转为 Word 图片)
        try {
            // 身份证正面
            String faceUrl = extractUrl(person.getIdPhotoFace());
            if (StrUtil.isNotEmpty(faceUrl)) {
                var pic = Pictures.ofUrl(faceUrl).size(126, 80);
                mapper.put("idPhotoFace", pic.create());
                mapper.put("身份证正面", pic.create());
                mapper.put("身份证正投影", pic.create());
            }

            // 身份证反面
            String backUrl = extractUrl(person.getIdPhotoBack());
            if (StrUtil.isNotEmpty(backUrl)) {
                var pic = Pictures.ofUrl(backUrl).size(126, 80);
                mapper.put("idPhotoBack", pic.create());
                mapper.put("身份证反面", pic.create());
                mapper.put("身份证背投影", pic.create());
            }

            // 人脸照片
            if (StrUtil.isNotEmpty(person.getFacePhotoUrl())) {
                var pic = Pictures.ofUrl(person.getFacePhotoUrl()).size(100, 100);
                mapper.put("facePhotoUrl", pic.create());
                mapper.put("人脸照片", pic.create());
                mapper.put("学员照片", pic.create());
            }
        } catch (Exception e) {
            log.warn("处理系统图片字段失败", e);
        }

        // 5. 动态属性与签名处理
        if (StrUtil.isNotEmpty(archivesData.getArchiveData())) {
            try {
                JSONObject json = JSON.parseObject(archivesData.getArchiveData());
                for (String key : json.keySet()) {
                    Object val = json.get(key);
                    if (key.toLowerCase().contains("signature") && val instanceof String && ((String) val).startsWith("http")) {
                        mapper.put(key, Pictures.ofUrl((String) val).size(150, 60).create());
                    } else if (val != null) {
                        mapper.put(key, val);
                    }
                }
            } catch (Exception e) {
                log.error("解析动态数据失败", e);
            }
        }
        
        return mapper;
    }

    private void copyParagraph(XWPFDocument target, XWPFParagraph source) {
        XWPFParagraph newP = target.createParagraph();
        newP.getCTP().set(source.getCTP().copy());
    }

    private void copyTable(XWPFDocument target, XWPFTable source) {
        XWPFTable newT = target.createTable();
        newT.getCTTbl().set(source.getCTTbl().copy());
    }

    @Override
    public List<PersonArchivesData> getByPersonIdAndWorkType(Long personId, Integer workType) {
        LambdaQueryWrapper<PersonArchivesData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonArchivesData::getPersonId, personId);
        if (workType != null) {
            wrapper.eq(PersonArchivesData::getWorkType, workType);
        }
        wrapper.eq(PersonArchivesData::getIsDelete, 0);
        return list(wrapper);
    }

    @Override
    public List<PersonArchivesData> getByConfigId(Long configId) {
        PersonArchivesData query = new PersonArchivesData();
        query.setConfigId(configId);
        return listData(query);
    }

    @Override
    public List<PersonArchivesData> listData(PersonArchivesData query) {
        log.info(">>>> LISTING DATA: configId={}, orgId={}", query.getConfigId(), query.getOrgId());
        
        // 1. 获取该机构及其子机构 ID 列表
        List<Long> allOrgIds = new ArrayList<>();
        if (query.getOrgId() != null) {
            allOrgIds.add(query.getOrgId());
            try {
                List<Long> children = sysOrgService.getChildIdListById(query.getOrgId());
                if (CollUtil.isNotEmpty(children)) allOrgIds.addAll(children);
            } catch (Exception ignored) {}
        }

        // 2. 使用 Lambda 构建强兼容查询 (绕过可能不完善的 XML)
        LambdaQueryWrapper<PersonArchivesData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonArchivesData::getIsDelete, 0);
        if (query.getConfigId() != null) wrapper.eq(PersonArchivesData::getConfigId, query.getConfigId());
        if (query.getPersonId() != null) wrapper.eq(PersonArchivesData::getPersonId, query.getPersonId());
        if (CollUtil.isNotEmpty(allOrgIds)) wrapper.in(PersonArchivesData::getOrgId, allOrgIds);
        if (StrUtil.isNotEmpty(query.getArchiveNumber())) wrapper.like(PersonArchivesData::getArchiveNumber, query.getArchiveNumber());
        
        wrapper.orderByDesc(PersonArchivesData::getUpdateTime);
        
        List<PersonArchivesData> list = list(wrapper);
        if (CollUtil.isEmpty(list)) return list;
        
        // 3. 增强：名称自愈与信息补全
        for (PersonArchivesData data : list) {
            try {
                // 链路 A：通过 Person ID 物理查找
                Person p = personService.getById(data.getPersonId());
                if (p == null) {
                    // 链路 B：通过 User ID 兼容查找
                    p = personService.getByUserId(data.getPersonId());
                }
                
                if (p != null) {
                    data.setPersonName(p.getName());
                    if (p.getUserId() != null) {
                        try {
                            SafetyCode sc = safetyCodeService.getStatus(p.getUserId());
                            if (sc != null) {
                                data.setSafetyColor(sc.getColor());
                                data.setSafetyStatus(sc.getStatus());
                            }
                        } catch (Exception ignored) {}
                    }
                } else {
                    data.setPersonName("未知成员(ID:" + data.getPersonId() + ")");
                }
            } catch (Exception e) {
                log.error("Correction failed for pid: {}", data.getPersonId());
            }
        }
        return list;
    }

    private void populateExtraInfo(List<PersonArchivesData> list) {
        if (CollUtil.isEmpty(list)) return;
        for (PersonArchivesData data : list) {
            Person person = personService.getById(data.getPersonId());
            if (person != null) {
                data.setPersonName(person.getName());
                if (person.getUserId() != null) {
                    SafetyCode sc = safetyCodeService.getStatus(person.getUserId());
                    if (sc != null) {
                        data.setSafetyColor(sc.getColor());
                        data.setSafetyStatus(sc.getStatus());
                    }
                }
            }
        }
    }

    @Override
    public int autoSyncDataFromSystem(Long configId, Long orgId, Integer workType) {
        log.info(">>>> [ULTIMATE CLEAN SYNC] CALLED WITH: configId={}, orgId={}", configId, orgId);
        
        // 0. 如果是宜昌两网项目 (org_id=4)，执行物理全清（物理抹除所有旧数据，确保绝对干净）
        if (orgId == 4) {
            try {
                int deleted = jdbcTemplate.update("DELETE FROM person_archives_data WHERE config_id = ? AND org_id = ?", configId, orgId);
                log.info(">>>> PROJECT 4 PRE-SYNC WIPE SUCCESS: {} records removed", deleted);
            } catch (Exception e) { log.error("Project 4 wipe error", e); }
        }

        java.util.Set<Long> pids = new java.util.HashSet<>();
        try {
            // A. 只抓取跟该 orgId 强关联的人员 (针对项目部 4 的精简过滤)
            java.util.List<java.util.Map<String, Object>> rels = jdbcTemplate.queryForList(
                "SELECT person_id FROM xm_person_org WHERE org_id = ?", orgId
            );
            for (java.util.Map<String, Object> it : rels) {
                Object pObj = it.get("person_id");
                if (pObj != null) {
                    long pid = Long.parseLong(pObj.toString());
                    // 如果是项目 4，除了余安琪 (130) 以外其他人暂时屏蔽，除非后续有需要
                    if (orgId == 4 && pid != 130) continue;
                    pids.add(pid);
                }
            }
            
            // B. 显式锁定余安琪 (ID: 130)
            if (orgId == 4) {
               pids.add(130L); 
            }

            log.info(">>>> TARGET PIDS FOR CLEAN SYNC: {}", pids);
        } catch (Exception e) { log.error("Pre-sync scan error", e); }

        if (pids.isEmpty()) return 0;
        
        // 2. 物理精准灌入
        int count = 0;
        String today = cn.hutool.core.date.DateUtil.today().replace("-", "");
        for (Long pid : pids) {
            String archiveNo = "DAG-" + today + "-" + cn.hutool.core.util.IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
            
            Person p = personService.getById(pid);
            if (p == null) continue;
            
            try {
                jdbcTemplate.update(
                    "INSERT INTO person_archives_data (config_id, person_id, user_id, org_id, work_type, archive_number, status, created_at, updated_at, is_delete) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 0)",
                    configId, pid, p.getUserId(), orgId, (p.getWorkType() != null ? p.getWorkType() : 0), archiveNo, "published"
                );
                count++;
            } catch (Exception e) { log.error("SYNC INSERT FAILED for pid:{}", pid, e); }
        }
        return count;
    }

    @Override
    public boolean saveOrUpdate(PersonArchivesData entity) {
        boolean result = super.saveOrUpdate(entity);
        if (result && entity.getPersonId() != null) {
            try {
                // 联动机制：档案变更触发安全码重新评估
                Person person = personService.getById(entity.getPersonId());
                if (person != null && person.getUserId() != null) {
                    log.info("Archive updated for person {}, triggering safety code re-evaluation for user {}", person.getName(), person.getUserId());
                    safetyCodeService.reevaluateColor(person.getUserId(), entity.getProjectId(), null, null);
                }
            } catch (Exception e) {
                log.error("Failed to trigger safety code re-evaluation after archive update", e);
            }
        }
        return result;
    }

    @Override
    public PersonArchivesData createArchive(Long configId, Long personId, Long userId) {
        PersonArchivesData data = new PersonArchivesData();
        data.setConfigId(configId);
        data.setPersonId(personId);
        data.setUserId(userId);
        
        // 填充基本信息以便查询冗余
        Person person = personService.getById(personId);
        if (person != null) {
            data.setOrgId(person.getOrgId() != null ? person.getOrgId() : 0L);
            data.setWorkType(person.getWorkType() != null ? person.getWorkType() : 0);
        } else {
            // 人员不存在时给个默认值，防止数据库报错
            data.setOrgId(0L);
            data.setWorkType(0);
        }
        
        data.setStatus("draft");
        data.setArchiveNumber("DAG-" + cn.hutool.core.date.DateUtil.format(new java.util.Date(), "yyyyMMdd") + "-" + cn.hutool.core.util.IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase());
        data.setCreatedAt(LocalDateTime.now());
        data.setUpdatedAt(LocalDateTime.now());
        data.setIsDelete(0);
        save(data);
        return data;
    }

    private String extractUrl(String jsonOrUrl) {
        if (StrUtil.isEmpty(jsonOrUrl)) return null;
        if (jsonOrUrl.trim().startsWith("[")) {
            try {
                JSONArray array = JSON.parseArray(jsonOrUrl);
                if (array.size() > 0) {
                    return array.getJSONObject(0).getString("url");
                }
            } catch (Exception e) {
                log.warn("解析图片JSON失败: {}", jsonOrUrl);
            }
        }
        return jsonOrUrl;
    }
    @Override
    public byte[] generateQrCode(Long id) {
        String url = generatePreviewUrl(id, "html");
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 300, 300, hints);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("生成二维码失败", e);
            throw new RuntimeException("生成二维码失败: " + e.getMessage());
        }
    }

}
