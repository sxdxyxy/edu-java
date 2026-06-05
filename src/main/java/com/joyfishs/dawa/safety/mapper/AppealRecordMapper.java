package com.joyfishs.dawa.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safety.entity.AppealRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 申诉记录 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Mapper
public interface AppealRecordMapper extends BaseMapper<AppealRecord> {

    /**
     * 查询违章关联的申诉记录
     */
    @Select("SELECT * FROM t_safety_appeal_record WHERE violation_record_id = #{violationRecordId}")
    AppealRecord selectByViolationRecordId(@Param("violationRecordId") Long violationRecordId);

    /**
     * 查询申诉人的申诉记录
     */
    @Select("SELECT * FROM t_safety_appeal_record WHERE person_id = #{personId} ORDER BY created_at DESC")
    List<AppealRecord> selectByPersonId(@Param("personId") Long personId);

    /**
     * 查询待审批的申诉记录（安全主管工作台）
     */
    @Select("SELECT * FROM t_safety_appeal_record WHERE review_result = 'pending' ORDER BY created_at DESC")
    List<AppealRecord> selectPendingAppeals();

    /**
     * 更新审批结果
     */
    @Update("UPDATE t_safety_appeal_record SET review_result = #{reviewResult}, review_comment = #{reviewComment}, " +
            "reviewer_id = #{reviewerId}, review_time = NOW(), score_restored = #{scoreRestored}, " +
            "score_restored_at = CASE WHEN #{reviewResult} = 'approved' THEN NOW() ELSE score_restored_at END, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateReviewResult(@Param("id") Long id,
                           @Param("reviewResult") String reviewResult,
                           @Param("reviewComment") String reviewComment,
                           @Param("reviewerId") Long reviewerId,
                           @Param("scoreRestored") Integer scoreRestored);
}
