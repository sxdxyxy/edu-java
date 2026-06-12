package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 智理安全配置项实体
 * <p>
 * 配置键按 "分组.子键.属性" 命名, 例如:
 *   color.worker.yellow.threshold
 *   score.retrain.restore
 *   retrain.overdue.days-per-week
 *   role.admin.required-match
 * </p>
 * <p>
 * 注: getValueAsInt/getValueAsString 等类型转换在 Service 层提供,
 *     实体只负责持久化.
 * </p>
 *
 * @author safe-edu
 * @since 2026-06-10
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_safety_config", autoResultMap = true)
@ApiModel(value = "SafetyConfig", description = "智理安全配置项")
public class SafetyConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("配置键")
    private String configKey;

    @ApiModelProperty("配置分组 (color/score/retrain/role)")
    private String configGroup;

    @ApiModelProperty("配置值 (字符串,按 valueType 转换)")
    private String configValue;

    @ApiModelProperty("值类型 (int/decimal/boolean/string/json)")
    private String valueType;

    @ApiModelProperty("说明")
    private String description;

    @ApiModelProperty("是否允许前端修改 (0=系统内置)")
    private Boolean isEditable;

    @ApiModelProperty("创建时间")
    private Date createdAt;

    @ApiModelProperty("更新时间")
    private Date updatedAt;
}
