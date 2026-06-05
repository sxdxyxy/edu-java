package com.joyfishs.dawa.statistics.domain;

import java.math.BigDecimal;

import lombok.Data;
import lombok.experimental.Accessors;

/**

 * @description: 部门统计
 * @date 2021-12-30 16:17
 */
@Data
@Accessors(chain = true)
public class DataStatisticsOrg {

    /** 单位名称 */
    private String orgName;

    /** 学员数量 */
    private Integer personCount = 0;

    /** 培训人数 */
    private Integer trainCount = 0;

    /** 培训率 */
    private BigDecimal trainRate = BigDecimal.ZERO;

    /** 年度学时 */
    private BigDecimal yearClassHour = BigDecimal.ZERO;

    /** 累计学时 */
    private BigDecimal sumClassHour = BigDecimal.ZERO;

    /** 人均学时 */
    private BigDecimal avgClassHour = BigDecimal.ZERO;
}
