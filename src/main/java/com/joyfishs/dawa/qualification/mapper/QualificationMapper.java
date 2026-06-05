package com.joyfishs.dawa.qualification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.qualification.entity.Qualification;

/**
 * 资质证件 Mapper 接口
 * <p>
 * 用于操作 qualifications 表
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Mapper
public interface QualificationMapper extends BaseMapper<Qualification> {

    /**
     * 根据用户 ID 查询资质列表
     *
     * @param userId 用户 ID
     * @return 资质列表
     */
    List<Qualification> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 和状态查询资质列表
     *
     * @param userId 用户 ID
     * @param status 状态
     * @return 资质列表
     */
    List<Qualification> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 查询即将过期的资质（30 天内）
     *
     * @return 资质列表
     */
    List<Qualification> selectExpiringSoon();
    
    /**
     * 查询指定组织即将到期的资质证件
     *
     * @param orgId 组织ID
     * @param days 天数阈值
     * @return 即将到期的资质证件列表
     */
    List<Qualification> selectExpiringByOrgId(@Param("orgId") Long orgId, @Param("days") int days);
}

