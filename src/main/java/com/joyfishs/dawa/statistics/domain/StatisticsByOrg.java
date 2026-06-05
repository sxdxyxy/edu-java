package com.joyfishs.dawa.statistics.domain;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部门统计
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "部门统计")
public class StatisticsByOrg {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name ;

    @ApiModelProperty("完成课程数量")
    private int finishOfCourses ;

    @ApiModelProperty("累计学时")
    private BigDecimal totalClassHours ;

}
