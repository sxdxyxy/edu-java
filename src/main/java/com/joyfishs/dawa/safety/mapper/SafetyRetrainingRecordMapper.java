package com.joyfishs.dawa.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safety.entity.SafetyRetrainingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 安全再培训记录 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Mapper
public interface SafetyRetrainingRecordMapper extends BaseMapper<SafetyRetrainingRecord> {

    /**
     * 查询人员待完成的再培训记录
     */
    @Select("SELECT * FROM t_safety_retraining_record WHERE person_id = #{personId} AND status = 'pending' ORDER BY create_time DESC")
    List<SafetyRetrainingRecord> selectPendingByPersonId(@Param("personId") Long personId);

    /**
     * 查询人员所有待完成的再培训记录
     */
    @Select("SELECT * FROM t_safety_retraining_record WHERE person_id = #{personId} AND status IN ('pending', 'ongoing') ORDER BY create_time DESC")
    List<SafetyRetrainingRecord> selectPendingOrOngoingByPersonId(@Param("personId") Long personId);

    /**
     * 更新状态
     */
    @Update("UPDATE t_safety_retraining_record SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 标记为已完成
     */
    @Update("UPDATE t_safety_retraining_record SET status = 'completed', end_date = #{endDate}, score_restored = #{scoreRestored}, update_time = NOW() WHERE id = #{id}")
    int markCompleted(@Param("id") Long id, @Param("endDate") Date endDate, @Param("scoreRestored") Integer scoreRestored);
}
