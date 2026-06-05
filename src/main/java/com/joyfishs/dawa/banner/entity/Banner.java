package com.joyfishs.dawa.banner.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.*;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轮播图
 */
@Data
@TableName(value = "xm_banner")
@ApiModel(value = "Banner", description = "轮播图")
public class Banner extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("图片url路径")
    @NotEmpty(message = "url路径不能为空")
    private String bannerUrl;

    @ApiModelProperty("附加数据(例如APP内跳转的路径或者网页跳转的url)")
    private String data;

    @ApiModelProperty("是否发布状态，false下架，true上架")
    @NotNull
    private Boolean publish;

    @ApiModelProperty("开始展示时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束展示时间")
    private LocalDateTime endTime;
}
