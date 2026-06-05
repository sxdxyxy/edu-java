package com.joyfishs.dawa.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safety.entity.SafetyScoreTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 安全积分变动流水 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Mapper
public interface SafetyScoreTransactionMapper extends BaseMapper<SafetyScoreTransaction> {

    /**
     * 查询人员的积分变动记录
     */
    @Select("SELECT * FROM t_safety_score_transaction WHERE person_id = #{personId} ORDER BY created_at DESC")
    List<SafetyScoreTransaction> selectByPersonId(@Param("personId") Long personId);

    /**
     * 查询人员的最近N条积分变动记录
     */
    @Select("SELECT * FROM t_safety_score_transaction WHERE person_id = #{personId} ORDER BY created_at DESC LIMIT #{limit}")
    List<SafetyScoreTransaction> selectRecentByPersonId(@Param("personId") Long personId, @Param("limit") Integer limit);
}
