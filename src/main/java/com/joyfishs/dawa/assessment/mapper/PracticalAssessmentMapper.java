package com.joyfishs.dawa.assessment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.assessment.entity.PracticalAssessment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实操考核 Mapper 接口
 * 
 * @author safe-edu
 * @since 2026-03-29
 */
@Mapper
public interface PracticalAssessmentMapper extends BaseMapper<PracticalAssessment> {

    /**
     * 统计指定组织的考核总数
     * 
     * @param orgId 组织ID
     * @return 考核总数
     */
    Long countTotalByOrg(@Param("orgId") Long orgId);

    /**
     * 统计指定组织通过考核的数量
     * 
     * @param orgId 组织ID
     * @return 通过考核数量
     */
    Long countPassedByOrg(@Param("orgId") Long orgId);

    /**
     * 统计指定组织优秀考核的数量
     * 
     * @param orgId 组织ID
     * @return 优秀考核数量
     */
    Long countExcellentByOrg(@Param("orgId") Long orgId);
}
