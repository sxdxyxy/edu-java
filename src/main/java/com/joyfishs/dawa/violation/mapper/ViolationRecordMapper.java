package com.joyfishs.dawa.violation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 违章记录 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-03-29
 */
@Mapper
public interface ViolationRecordMapper extends BaseMapper<ViolationRecord> {

    /**
     * 查询违章记分排行榜TOP10
     */
    List<Map<String, Object>> selectTop10ViolatorsByScore(@Param("orgId") Long orgId);

    /**
     * 更新违章记录状态和备注
     */
    @Update("UPDATE violation_records SET status = #{status}, remark = #{remark}, update_time = NOW() WHERE id = #{id}")
    int updateStatusAndRemark(@Param("id") Long id, @Param("status") String status, @Param("remark") String remark);
}
