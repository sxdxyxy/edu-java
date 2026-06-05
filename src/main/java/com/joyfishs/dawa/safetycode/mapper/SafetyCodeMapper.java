package com.joyfishs.dawa.safetycode.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safetycode.entity.SafetyCode;

/**
 * 安全码 Mapper 接口
 * <p>
 * 用于操作 safety_codes 表
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Mapper
public interface SafetyCodeMapper extends BaseMapper<SafetyCode> {
    /**
     * 统计指定组织今天新增的安全码数量
     */
    Long countTodayNewCodesByOrg(Long orgId);
    
    /**
     * 统计指定组织有效安全码数量
     */
    Long countValidCodesByOrg(Long orgId);
    
    /**
     * 统计指定组织过期安全码数量
     */
    Long countExpiredCodesByOrg(Long orgId);
}
