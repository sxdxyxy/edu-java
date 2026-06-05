package com.joyfishs.dawa.exam.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;

/**
 * 考试的返回封装类
 */

@Data
public class ExamVo {

    /**  id （projectId） */
    private Long id;

    /** 项目所属组织 */
    private Long orgId;

    /** * 项目名 */
    private String projectName;

    /** 人数 */
    private Integer personNumber;

    /** 考试开始时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date examSdate;

    /** 考试结束时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date examEdate;

    /** 考试状态  1-未开始  2-进行中 3-已结束*/
    private Integer examState;

    /** 考试次数 */
    private Integer examNumber;

    /** 补考次数 */
    private Integer resitNumber;

    /** 项目状态 1-未发布 2-未开始 3-进行中  4-已完成  5-已中止 */
    private Integer projectStatus;

    /** 是否参加考试  1-是  0-否   移动端用于反参*/
    @TableField(exist = false)
    private Integer isAttend;

    @TableField(exist = false)
    /** 1-考试详情 2-预约考试(20220517丢弃) 3-进入考试*/
    private Integer examCondition;

    /* 是否禁用 （考试移动端 20220517）*/
    @TableField(exist = false)
    private Integer disable;
}
