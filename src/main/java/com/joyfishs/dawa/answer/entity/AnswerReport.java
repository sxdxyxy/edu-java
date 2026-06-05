package com.joyfishs.dawa.answer.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.system.entity.BaseEntity;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 答题报告
 */
@Data
@Accessors(chain = true)
@TableName("xm_answer_report")
public class AnswerReport extends BaseEntity {
    /**  id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 人员id */
    private Long personId;

    /** 项目id */
    private Long projectId;

    /** 课程id */
    private Long courseId;

    /** 报告类型 1:课程 2:考试 3:练习 4:作业*/
    private int type;

    /** 题目总数 */
    private int totalQuestions;

    /** 已答题数 */
    private int answeredQuestions;

    /** 答对题数 */
    private int rightQuestions;

    /** 总分 */
    private BigDecimal totalScore;

    /** 得分 */
    private BigDecimal score;

    /** 答题时间 单位:秒 */
    private int answerTime;

    /** 交卷时间 */
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private Date submitTime;

    /* 是否参加考试  1-是  0-否*/
    private Integer ifAttend;

    /** 课程名称 */
    @TableField(exist = false)
    private String courseName;

    /** 击败比例 */
    @TableField(exist = false)
    private BigDecimal beat;

    /** 全站平均得分 */
    @TableField(exist = false)
    private BigDecimal avgScore;

    /** 答题卡明细 */
    @TableField(exist = false)
    List<AnswerReportItem> reportItems = new ArrayList<>();
}
