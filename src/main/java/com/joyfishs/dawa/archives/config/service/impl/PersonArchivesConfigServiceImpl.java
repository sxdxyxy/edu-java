package com.joyfishs.dawa.archives.config.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesConfig;
import com.joyfishs.dawa.archives.config.mapper.PersonArchivesConfigMapper;
import com.joyfishs.dawa.archives.config.service.PersonArchivesConfigService;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 人员档案配置服务实现
 */
@Slf4j
@Service
public class PersonArchivesConfigServiceImpl extends ServiceImpl<PersonArchivesConfigMapper, PersonArchivesConfig>
        implements PersonArchivesConfigService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<PersonArchivesConfig> listConfigs(Integer workType, Long orgId, Long projectId) {
        LambdaQueryWrapper<PersonArchivesConfig> wrapper = new LambdaQueryWrapper<>();
        if (orgId != null) {
            wrapper.eq(PersonArchivesConfig::getOrgId, orgId);
        }
        if (workType != null && workType > 0) {
            wrapper.eq(PersonArchivesConfig::getWorkType, workType);
        }
        if (projectId != null) {
            wrapper.eq(PersonArchivesConfig::getProjectId, projectId);
        }
        wrapper.eq(PersonArchivesConfig::getIsDelete, 0)
               .orderByAsc(PersonArchivesConfig::getSortOrder);
        List<PersonArchivesConfig> list = list(wrapper);
        fillExtraInfo(list);
        return list;
    }

    @Override
    public List<PersonArchivesConfig> getByWorkTypeAndOrgId(Integer workType, Long orgId) {
        return listConfigs(workType, orgId, null);
    }

    @Override
    public List<PersonArchivesConfig> getByOrgId(Long orgId) {
        LambdaQueryWrapper<PersonArchivesConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonArchivesConfig::getOrgId, orgId)
               .eq(PersonArchivesConfig::getIsDelete, 0)
               .eq(PersonArchivesConfig::getIsActive, true)
               .orderByAsc(PersonArchivesConfig::getSortOrder);
        List<PersonArchivesConfig> list = list(wrapper);
        // 填充扩展信息：更新人员名称和档案项数量
        fillExtraInfo(list);
        return list;
    }

    @Override
    public PersonArchivesConfig getByIdWithCheck(Long id) {
        PersonArchivesConfig config = getById(id);
        if (config == null) {
            throw new RuntimeException("档案配置不存在");
        }
        // 填充单个信息
        fillExtraInfo(List.of(config));
        return config;
    }

    @Override
    public void initializeDefaultScheme(Long orgId) {
        // 创建标准方案
        PersonArchivesConfig config = new PersonArchivesConfig();
        config.setOrgId(orgId);
        config.setWorkType(0); // 0 表示通用工种/全员方案
        config.setConfigName("标准施工人员一人一档方案");
        config.setConfigCode("STANDARD_PERSON_PROFILE");
        config.setIsActive(true);
        config.setIsDefault(true);
        config.setSortOrder(1);

        // 8个标准档案项，包含详细字段配置
        String itemsJson = "[" +
                "{\"archiveName\":\"1. 人员信息登记表\",\"sortOrder\":10,\"fields\":[" +
                    "{\"label\":\"姓名\",\"key\":\"name\",\"type\":\"input\",\"required\":true}," +
                    "{\"label\":\"性别\",\"key\":\"gender\",\"type\":\"select\",\"options\":[\"男\",\"女\"]}," +
                    "{\"label\":\"工种\",\"key\":\"jobType\",\"type\":\"input\"}," +
                    "{\"label\":\"身份证号\",\"key\":\"idCard\",\"type\":\"input\",\"required\":true}," +
                    "{\"label\":\"照片\",\"key\":\"photo\",\"type\":\"image\"}" +
                "]}," +
                "{\"archiveName\":\"2. 职业危害告知书\",\"sortOrder\":20,\"fields\":[" +
                    "{\"label\":\"岗位\",\"key\":\"post\",\"type\":\"input\"}," +
                    "{\"label\":\"危害因素\",\"key\":\"hazards\",\"type\":\"checkbox\",\"options\":[\"粉尘\",\"噪声\",\"高温\",\"有毒化学品\"]}," +
                    "{\"label\":\"确认签名\",\"key\":\"signature\",\"type\":\"signature\"}" +
                "]}," +
                "{\"archiveName\":\"3. 健康承诺与体检报告\",\"sortOrder\":30,\"fields\":[" +
                    "{\"label\":\"疾病史\",\"key\":\"diseases\",\"type\":\"checkbox\",\"options\":[\"心脏病\",\"高血压\",\"贫血\",\"癫痫\"]}," +
                    "{\"label\":\"体检报告\",\"key\":\"report\",\"type\":\"file\"}," +
                    "{\"label\":\"承诺签名\",\"key\":\"signature\",\"type\":\"signature\"}" +
                "]}," +
                "{\"archiveName\":\"4. 建设领域从业人员临时用工协议\",\"sortOrder\":40,\"fields\":[" +
                    "{\"label\":\"甲方名称\",\"key\":\"partyA\",\"type\":\"input\"}," +
                    "{\"label\":\"协议期限\",\"key\":\"duration\",\"type\":\"range-picker\"}," +
                    "{\"label\":\"工资标准\",\"key\":\"salary\",\"type\":\"input\"}," +
                    "{\"label\":\"签字确认\",\"key\":\"signature\",\"type\":\"signature\"}" +
                "]}," +
                "{\"archiveName\":\"5. 三级安全教育登记表\",\"sortOrder\":50,\"fields\":[" +
                    "{\"label\":\"总学时\",\"key\":\"hours\",\"type\":\"number\"}," +
                    "{\"label\":\"考核结果\",\"key\":\"result\",\"type\":\"select\",\"options\":[\"合格\",\"不合格\"]}," +
                    "{\"label\":\"班组级成绩\",\"key\":\"scoreGroup\",\"type\":\"number\"}" +
                "]}," +
                "{\"archiveName\":\"6. 安全技术交底\",\"sortOrder\":60,\"fields\":[" +
                    "{\"label\":\"工程名称\",\"key\":\"projectName\",\"type\":\"input\"}," +
                    "{\"label\":\"交底内容\",\"key\":\"content\",\"type\":\"textarea\"}," +
                    "{\"label\":\"交底人签名\",\"key\":\"signature\",\"type\":\"signature\"}" +
                "]}," +
                "{\"archiveName\":\"7. 安全防护用品领用登记表\",\"sortOrder\":70,\"fields\":[" +
                    "{\"label\":\"领用清单\",\"key\":\"items\",\"type\":\"table-input\",\"columns\":[" +
                        "{\"title\":\"用品名称\",\"key\":\"name\"},{\"title\":\"数量\",\"key\":\"count\"},{\"title\":\"领用日期\",\"key\":\"date\"}" +
                    "]}" +
                "]}," +
                "{\"archiveName\":\"8. 安全质量责任及消防安全承诺书\",\"sortOrder\":80,\"fields\":[" +
                    "{\"label\":\"承诺日期\",\"key\":\"date\",\"type\":\"date\"}," +
                    "{\"label\":\"承诺人签名\",\"key\":\"signature\",\"type\":\"signature\"}" +
                "]}" +
                "]";
        config.setArchiveItems(itemsJson);

        save(config);
    }

    /**
     * 填充扩展信息：更新人员用户名、档案项数量
     */
    private void fillExtraInfo(List<PersonArchivesConfig> list) {
        for (PersonArchivesConfig config : list) {
            // 获取更新人员名称
            if (config.getUpdateBy() != null) {
                SysUser user = sysUserService.getById(config.getUpdateBy());
                if (user != null) {
                    config.setUpdateUserName(user.getName());
                }
            }
            // 统计该配置下档案项数量
            if (config.getArchiveItems() != null && !config.getArchiveItems().isEmpty()) {
                try {
                    List<?> items = objectMapper.readValue(config.getArchiveItems(), List.class);
                    config.setItemCount(items.size());
                } catch (JsonProcessingException e) {
                    log.warn("解析archiveItems JSON失败: {}", e.getMessage());
                    config.setItemCount(0);
                }
            } else {
                config.setItemCount(0);
            }
        }
    }
}