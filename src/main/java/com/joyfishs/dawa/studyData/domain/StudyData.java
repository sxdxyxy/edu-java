package com.joyfishs.dawa.studyData.domain;

import java.util.List;

import lombok.Data;

@Data
public class StudyData {

    /*累计学习课时*/
    private Integer totalClassHour;

    /*累计学习课程*/
    private Integer totalCourse;

    /*结业课程*/
    private Integer completeCourse;

    /*考试次数*/
    private Integer examSize;

    /*年度学时完成情况*/
    private Double yearClassHourSituation;

    /*年度完成学时*/
    private Integer yearTotalClassHour;

    /*年度学时要求*/
    private Integer yearClassHourRequi;

    /*年度学时-必修课程*/
    private Integer yearClassHour1;

    /*年度学时-选修课程*/
    private Integer yearClassHour2;

    /*年度学时-必读公告*/
    private Integer yearClassHour3;

    /*所选课程成绩情况*/
    private List<StudyDataCourse> courseList;

}
