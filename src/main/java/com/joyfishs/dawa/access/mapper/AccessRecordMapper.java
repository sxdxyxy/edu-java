package com.joyfishs.dawa.access.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.access.entity.AccessRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 准入记录 Mapper 接口
 * 
 * @author safe-edu
 * @since 2026-03-29
 */
@Mapper
public interface AccessRecordMapper extends BaseMapper<AccessRecord> {
    /**
     * 统计指定组织今日入场人数
     */
    Long countTodayEntriesByOrg(@Param("orgId") Long orgId);
    
    /**
     * 统计指定组织今日出场人数
     */
    Long countTodayExitsByOrg(@Param("orgId") Long orgId);
    
    /**
     * 统计指定组织当前在场人数
     */
    Long countCurrentOnsiteByOrg(@Param("orgId") Long orgId);
}

