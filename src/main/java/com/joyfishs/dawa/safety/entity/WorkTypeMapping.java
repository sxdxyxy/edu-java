package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 工种与岗位类型映射实体类
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_work_type_mapping", autoResultMap = true)
@ApiModel(value = "WorkTypeMapping", description = "工种岗位映射")
public class WorkTypeMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("安全积分岗位代码")
    private String accountWorkType;

    @ApiModelProperty("培训系统工种编码（1-19）")
    private Integer workType;

    @ApiModelProperty("工种名称")
    private String workTypeName;

    @ApiModelProperty("初始积分")
    private Integer initialScore;

    @ApiModelProperty("绿码阈值")
    private Integer greenThreshold;

    @ApiModelProperty("黄码阈值")
    private Integer yellowThreshold;

    @ApiModelProperty("是否启用")
    private Boolean enabled;
}
