package com.joyfishs.dawa.statistics.domain;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页统计
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "首页统计数据")
public class HomeStatistics {

    @ApiModelProperty("注册学员人数")
    private int numberOfRegistrants ;

    @ApiModelProperty("平台活跃人数")
    private int numberOfActive ;

    @ApiModelProperty("平台课程数")
    private long numberOfCourses ;

    @ApiModelProperty("人均学时")
    private BigDecimal averageClassHours ;

    // ===== Dawa 安全模块统计字段 =====

    @ApiModelProperty("总人数")
    private Integer totalUsers;

    @ApiModelProperty("在场人数")
    private Integer onSiteUsers;
}
