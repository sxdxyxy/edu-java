package com.joyfishs.dawa.notice.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "通知公告新闻视图")
public class NoticeVo extends BaseEntity {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("封面图Url")
    private String cover;

    @ApiModelProperty("发布范围id")
    private Long rangeId;

    @ApiModelProperty("是否置顶 1是 0否 ")
    private Integer isTop;

    @ApiModelProperty("是否即时发布 1是 0否 ")
    private Integer isNowPublish;

    @ApiModelProperty("发布时间，如果是即时发布就不需要设置 ")
    private LocalDateTime publishTime;

    @ApiModelProperty("内容，富文本编辑")
    private String content;

    @ApiModelProperty("附件url")
    private String file;

    @ApiModelProperty("阅读量")
    private Integer viewCount;

    @ApiModelProperty("发布状态 0:未发布 1:已发布 2:撤销")
    private Integer status;

    @ApiModelProperty("发布人所在单位")
    private Long createOrgId;

    @ApiModelProperty("发布单位名称")
    private String createOrgName;

    @ApiModelProperty("发布范围名称")
    private String rangeName;

    @ApiModelProperty("发布人")
    private String publishName;

    @ApiModelProperty("是否计算学时 1是 0否")
    private Integer isCalculate;

    @ApiModelProperty("学时")
    private BigDecimal classHour;
}
