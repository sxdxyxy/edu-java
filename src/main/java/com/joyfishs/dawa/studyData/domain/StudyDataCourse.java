package com.joyfishs.dawa.studyData.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StudyDataCourse {

    /*课程名称*/
    private Long courseId;

    /*课程名称*/
    private String courseName;

    /*课程类别 1-选修 0-必修*/
    private Integer courseType;

    /*开始时间*/
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private Date startTime;

    /*课程状态 0-在修 1-修完课程通过*/
    private Integer status;

    /*学时 已完成学时/课程学时*/
    private String studyTime;

    /*考试成绩*/
    private Integer testResult;

}
