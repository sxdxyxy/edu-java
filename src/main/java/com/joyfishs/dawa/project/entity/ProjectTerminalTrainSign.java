package com.joyfishs.dawa.project.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfn
 * @TableName xm_project_terminal_train_sign
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_project_terminal_train_sign")
@ApiModel(value = "ProjectTerminalTrainSign", description = "终端培训签到记录")
public class ProjectTerminalTrainSign extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("终端培训id")
    private Long trainId;

    @ApiModelProperty("报名时间")
    private Date enrollTime;

    @ApiModelProperty("报名状态 1-已报名 2-取消报名")
    private Integer enrollStatus;

    @ApiModelProperty("签到时间")
    private Date signTime;

    @ApiModelProperty("签到状态 1-正常  2-迟到  3-未签到")
    private Integer signStatus;

}
