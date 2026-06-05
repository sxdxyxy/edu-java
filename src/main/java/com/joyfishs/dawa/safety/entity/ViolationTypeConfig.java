package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 违章类型配置实体类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_violation_type_config", autoResultMap = true)
@ApiModel(value = "ViolationTypeConfig", description = "违章类型配置")
public class ViolationTypeConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    /**
     * 违章代码
     */
    @ApiModelProperty("违章代码")
    private String violationCode;

    /**
     * 违章名称
     */
    @ApiModelProperty("违章名称")
    private String violationName;

    /**
     * 级别（minor/major/critical）
     */
    @ApiModelProperty("级别")
    private String violationLevel;

    /**
     * 扣分分值
     */
    @ApiModelProperty("扣分分值")
    private Integer deductScore;

    /**
     * 是否触发强制培训（0否 1是）
     */
    @ApiModelProperty("是否触发强制培训")
    private Boolean triggerTraining;

    /**
     * 触发培训学时
     */
    @ApiModelProperty("触发培训学时")
    private Integer trainingHours;

    /**
     * 状态（enabled/disabled）
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createdAt;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updatedAt;
}
