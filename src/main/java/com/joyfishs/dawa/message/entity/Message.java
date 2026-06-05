package com.joyfishs.dawa.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_message")
@ApiModel(value = "Message", description = "消息通知")
public class Message extends BaseEntity {

    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("通知类型 1: 项目提醒 2:学习提醒 3:年度培训计划 4:单位培训计划 5:终端培训 ")
    private Integer type;

    @ApiModelProperty("对应通知类型的id")
    private Long dataId;

    @ApiModelProperty("接收人id")
    private Long personId;

    @ApiModelProperty("是否阅读 1 已阅读 0 未阅读 ")
    private Integer isRead;

}
