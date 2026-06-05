package com.joyfishs.dawa.archives.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesUserAutoTrain {

    /** 项目ID **/
    private Long id;

    /** 项目名称 **/
    private String projectName;

    /** 项目开始日期 **/
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date startDate;

    /** 项目结束日期 **/
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date endDate;

    /** 受训角色 **/
    private String roleName;

    /** 应修学时 **/
    private String mustClassHour;

    /** 已修学时 **/
    private String alreadyClassHour;

    /** 完成状态 0-未完成 1-已完成 **/
    private Integer finishState = 0;

    /** 总练习题量 **/
    private Integer addUpExercises = 0;

    /** 已练习题量 **/
    private Integer alreadyExercises = 0;

    /** 答对题量 **/
    private Integer yesTopic = 0;

    /** 正确率 **/
    private String yesRate;

    /** 考试成绩 **/
    private BigDecimal testResult;

    /** 补考成绩 **/
    private BigDecimal mendTestResult;

    /** 考试耗时 **/
    private int testTime;

    /** 考试状态 0-不合格 1-合格 **/
    private Integer testState;

}
