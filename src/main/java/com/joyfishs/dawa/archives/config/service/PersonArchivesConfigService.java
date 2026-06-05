package com.joyfishs.dawa.archives.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesConfig;

import java.util.List;

/**
 * 人员档案配置服务接口
 */
public interface PersonArchivesConfigService extends IService<PersonArchivesConfig> {

    /**
     * 根据工种和机构 ID 查询配置列表
     */
    List<PersonArchivesConfig> getByWorkTypeAndOrgId(Integer workType, Long orgId);

    /**
     * 根据机构 ID 查询所有配置列表（不限工种）
     */
    List<PersonArchivesConfig> getByOrgId(Long orgId);

    /**
     * 根据 ID 查询配置详情
     */
    PersonArchivesConfig getByIdWithCheck(Long id);

    /**
     * 根据工种、机构、项目 ID 查询配置列表
     */
    List<PersonArchivesConfig> listConfigs(Integer workType, Long orgId, Long projectId);

    /**
     * 初始化标准一人一档配置
     */
    void initializeDefaultScheme(Long orgId);
}
