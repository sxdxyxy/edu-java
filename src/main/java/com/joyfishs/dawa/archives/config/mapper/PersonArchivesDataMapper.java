package com.joyfishs.dawa.archives.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 人员档案数据 Mapper
 */
@Mapper
public interface PersonArchivesDataMapper extends BaseMapper<PersonArchivesData> {

    /**
     * 根据人员 ID 和工种查询档案数据列表
     */
    List<PersonArchivesData> selectByPersonIdAndWorkType(Long personId, Integer workType);

    /**
     * 根据配置 ID 查询档案数据列表
     */
    List<PersonArchivesData> selectByConfigId(Long configId);

    /**
     * 联表查询档案列表（含人员姓名、安全码状态等）
     */
    List<PersonArchivesData> selectListData(PersonArchivesData search);
}
