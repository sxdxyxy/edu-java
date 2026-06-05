package com.joyfishs.dawa.archives.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 人员档案配置 Mapper
 */
@Mapper
public interface PersonArchivesConfigMapper extends BaseMapper<PersonArchivesConfig> {

    /**
     * 根据工种和机构 ID 查询配置列表
     */
    List<PersonArchivesConfig> selectByWorkTypeAndOrgId(Integer workType, Long orgId);
}
