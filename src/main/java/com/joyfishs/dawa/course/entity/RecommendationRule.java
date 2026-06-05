package com.joyfishs.dawa.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 推荐规则配置实体
 */
@Data
@TableName("sys_recommendation_rule")
public class RecommendationRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 规则编码 (profession, job, gender, age, degree, violation, hidden_danger) */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 是否启用 (0-禁用, 1-启用) */
    private Integer enabled;

    /** 描述 */
    private String description;
}
