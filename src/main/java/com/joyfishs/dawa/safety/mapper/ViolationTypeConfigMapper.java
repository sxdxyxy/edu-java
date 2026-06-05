package com.joyfishs.dawa.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.safety.entity.ViolationTypeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 违章类型配置 Mapper 接口
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Mapper
public interface ViolationTypeConfigMapper extends BaseMapper<ViolationTypeConfig> {

    /**
     * 查询所有启用的违章类型配置
     */
    @Select("SELECT * FROM t_violation_type_config WHERE status = 'enabled' ORDER BY sort_order ASC, id ASC")
    List<ViolationTypeConfig> selectAllEnabled();

    /**
     * 根据违章代码查询
     */
    @Select("SELECT * FROM t_violation_type_config WHERE violation_code = #{violationCode} AND status = 'enabled'")
    ViolationTypeConfig selectByViolationCode(String violationCode);

    /**
     * 根据违章级别查询
     */
    @Select("SELECT * FROM t_violation_type_config WHERE violation_level = #{level} AND status = 'enabled' ORDER BY sort_order ASC")
    List<ViolationTypeConfig> selectByLevel(String level);
}
