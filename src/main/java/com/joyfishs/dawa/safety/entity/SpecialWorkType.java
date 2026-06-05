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
 * 特种作业类型实体类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_special_work_type", autoResultMap = true)
@ApiModel(value = "SpecialWorkType", description = "特种作业类型")
public class SpecialWorkType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    /**
     * 作业类型编码（如：ELEC_HIGH）
     */
    @ApiModelProperty("作业类型编码")
    private String workTypeCode;

    /**
     * 作业类型名称（如：高压电工）
     */
    @ApiModelProperty("作业类型名称")
    private String workTypeName;

    /**
     * 危险等级（high/medium/low）
     */
    @ApiModelProperty("危险等级")
    private String dangerLevel;

    /**
     * 默认初始积分
     */
    @ApiModelProperty("默认初始积分")
    private Integer defaultScore;

    /**
     * 证书有效期（年）
     */
    @ApiModelProperty("证书有效期（年）")
    private Integer certificateValidYears;

    /**
     * 状态（enabled/disabled）
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remarks;

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
