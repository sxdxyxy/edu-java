package com.joyfishs.dawa.statistics.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 统计概述
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "统计概述")
public class OverviewStatistics {

    @ApiModelProperty("人员数量")
    private int numberOfPersonnel ;

    @ApiModelProperty("累计完成课程数量")
    private int numberOfCourses ;

    @ApiModelProperty("累计完成课时")
    private BigDecimal finishClassHours ;

    @ApiModelProperty("人均学时")
    private BigDecimal averageClassHours ;

    @ApiModelProperty("部门统计 - 完成课程数量排名 (前 10)")
    private List<StatisticsByOrg> totalOfCourses;

    @ApiModelProperty("部门统计 - 累计学时排名 (前 10)")
    private List<StatisticsByOrg> totalOfClassHours;

    @ApiModelProperty("个人统计 - 个人完成课程数量排名 (前 10)")
    private List<DataStatisticsPersonal> totalOfCoursesForPersonal;

    @ApiModelProperty("个人统计 - 个人年度学时排名 (前 10)")
    private List<DataStatisticsPersonal> yearClassHourForPersonal;

    @ApiModelProperty("活跃统计（近 15 日)")
    List<ActiveRecord> activeRecord;

    // ===== Dawa 安全模块统计字段 =====

    @ApiModelProperty("安全码有效率")
    private Double safetyCodeRate;

    @ApiModelProperty("资质合规率")
    private Double qualificationRate;

    @ApiModelProperty("违章总数")
    private Long violationCount;

    @ApiModelProperty("待处理违章")
    private Long pendingViolations;

    @ApiModelProperty("考核总次数")
    private Long assessmentCount;

    @ApiModelProperty("考核通过率")
    private Double assessmentPassRate;

    @ApiModelProperty("近 7 天违章趋势")
    private List<Map<String, Object>> violationTrend;

    @ApiModelProperty("考核结果分布")
    private List<Map<String, Object>> assessmentDistribution;

    @ApiModelProperty("违章类型分布")
    private List<Map<String, Object>> violationTypeDistribution;

    @ApiModelProperty("资质类型分布")
    private List<Map<String, Object>> qualificationDistribution;
}
