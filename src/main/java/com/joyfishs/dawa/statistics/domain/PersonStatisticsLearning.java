package com.joyfishs.dawa.statistics.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyfishs.dawa.student.domain.MyCourseList;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author QuAoLi
 * @description: 个人学习统计报告
 */
@Data
@Accessors(chain = true)
public class PersonStatisticsLearning {

    /** 姓名 */
    private String name;

    /** 累计学时 */
    private BigDecimal sumClassHour;

    /** 累计学习课程 */
    private int sumCourse;

    /** 结业课程 */
    private int completionCourse;

    /** 考试次数 */
    private int examCount;

    /** 本年度计划学时 */
    private BigDecimal planClassHour;

    /** 本年度已完成学时 */
    private BigDecimal yearClassHour;

    /** 本年度所学课程数 */
    private int yearCourse;

    /**
     *  年度学时完成情况 name:名称 classHour:学时
     */
    private List<Map<String, Object>> classHourList = new ArrayList<>();

    /** 本年度所学课程 */
    private List<MyCourseList> courseList = new ArrayList<>();

}
