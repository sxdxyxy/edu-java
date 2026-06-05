package com.joyfishs.dawa.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safety.entity.SpecialWorkType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 特种作业类型 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Mapper
public interface SpecialWorkTypeMapper extends BaseMapper<SpecialWorkType> {

    /**
     * 查询所有启用的特种作业类型
     */
    @Select("SELECT * FROM t_special_work_type WHERE status = 'enabled' ORDER BY id")
    List<SpecialWorkType> selectAllEnabled();

    /**
     * 根据作业类型编码查询
     */
    @Select("SELECT * FROM t_special_work_type WHERE work_type_code = #{workTypeCode} AND status = 'enabled'")
    SpecialWorkType selectByWorkTypeCode(String workTypeCode);
}
