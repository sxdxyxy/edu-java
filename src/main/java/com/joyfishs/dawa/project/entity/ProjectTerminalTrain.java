package com.joyfishs.dawa.project.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.system.entity.BaseEntity;
import com.joyfishs.utils.StringUtils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.extra.qrcode.QrCodeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_project_terminal_train")
@ApiModel(value = "ProjectTerminalTrain", description = "终端培训")
public class ProjectTerminalTrain extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("所属组织id")
    private Long orgId;

    @ApiModelProperty("培训名称")
    private String trainName;

    @ApiModelProperty("培训地点")
    private String trainAddress;

    @ApiModelProperty("培训开始时间")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private Date trainSdate;

    @ApiModelProperty("培训结束时间")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private Date trainEdate;

    @ApiModelProperty("主持人")
    private String host;

    @ApiModelProperty("会议人员数量")
    private Integer personNum;

    @ApiModelProperty("工程单位")
    private String engineeringOrg;

    @ApiModelProperty("施工单位")
    private String constructionOrg;

    @ApiModelProperty("学习内容")
    private String learningContent;

    @ApiModelProperty("联系人")
    private String contact;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("培训状态：1-未发布  2-未开始 3-进行中  4-已结束  5-已中止")
    private Integer status;

    @ApiModelProperty("是否补录")
    private Boolean later;

    @ApiModelProperty("签到码")
    private String signCode;

    @ApiModelProperty("/*报名状态 1-已报名 2-未报名*/")
    @TableField(exist = false)
    private Integer enrollStatus;

    @ApiModelProperty("签到码二维码Base64")
    @TableField(exist = false)
    private String signCodeQrCode;

    public String getSignCodeQrCode() {
        return StringUtils.isEmpty(this.signCode) ? "" :
                String.format("data:image/png;base64,%s", Base64.encode(QrCodeUtil.generatePng(this.signCode, 400, 400)));
    }

}
