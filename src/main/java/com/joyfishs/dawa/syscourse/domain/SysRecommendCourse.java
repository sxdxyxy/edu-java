package com.joyfishs.dawa.syscourse.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;

/**
 * @Author xiaodai
 * @create 2022/1/5 10:37
 */

@Data
public class SysRecommendCourse {

    /** 课程ID */
    private Long id;

    /**项目名称*/
    private String projectName;

    /** 培训开始时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date trainSdate;

    /** 培训结束时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date trainEdate;

    /** 培训类型 1-必修课  2-选修课 */
    private String trainType;

    /** 主讲老师 */
    private String teacher;

    /** 总课时 */
    private Integer totalCourseHours;

    /** 课程介绍 */
    private String remark;

    /** 系统课程封面图 */
    private String coverImage;

    /** 2-年度培训计划 */
    private Integer projectType;

    /** 添加状态 0-未添加 1-已添加 */
    private Integer signStatus;

}
