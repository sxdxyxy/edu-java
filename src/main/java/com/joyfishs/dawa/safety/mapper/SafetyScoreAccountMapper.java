package com.joyfishs.dawa.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 安全积分账户 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Mapper
public interface SafetyScoreAccountMapper extends BaseMapper<SafetyScoreAccount> {

    /**
     * 根据人员ID查询账户
     */
    @Select("SELECT * FROM t_safety_score_account WHERE person_id = #{personId} LIMIT 1")
    SafetyScoreAccount selectByPersonId(@Param("personId") Long personId);

    /**
     * 根据人员ID更新当前积分
     */
    @Update("UPDATE t_safety_score_account SET current_score = #{score}, update_time = NOW() WHERE person_id = #{personId}")
    int updateScoreByPersonId(@Param("personId") Long personId, @Param("score") Integer score);

    /**
     * 查询需要年度清零的账户
     */
    @Select("SELECT * FROM t_safety_score_account WHERE annual_reset_date < #{date} AND status = 'active'")
    List<SafetyScoreAccount> selectNeedsReset(@Param("date") Date date);

    /**
     * 根据项目ID查询所有账户
     */
    @Select("SELECT * FROM t_safety_score_account WHERE project_id = #{projectId} AND status = 'active'")
    List<SafetyScoreAccount> selectByProjectId(@Param("projectId") Long projectId);
}
