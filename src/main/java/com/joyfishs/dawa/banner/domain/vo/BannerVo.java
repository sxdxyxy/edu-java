package com.joyfishs.dawa.banner.domain.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轮播图
 */
@Data
@ApiModel(value = "Banner", description = "轮播图")
public class BannerVo {
    private Long id;

    @ApiModelProperty("图片url路径")
    private String bannerUrl;

    @ApiModelProperty("附加数据(例如APP内跳转的路径或者网页跳转的url)")
    private String data;

    @ApiModelProperty("展示时长(以秒为单位)")
    private Integer durationTime;

    @ApiModelProperty("是否发布状态，false下架，true上架")
    private Boolean publish;

    @ApiModelProperty("开始展示时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束展示时间")
    private LocalDateTime endTime;

    @ApiModelProperty("创建人id")
    private Long createBy;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern= DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人id")
    private Long updateBy;

    @JsonFormat(pattern=DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
