package com.joyfishs.dawa.answer.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 答题卡明细
 */
@Data
@Accessors(chain = true)
@TableName("xm_answer_report_item")
public class AnswerReportItem {

    /**  id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 答题报告id */
    private Long reportId;

    /** 题目id */
    private Long questionId;

    /** 我的答案 */
    private String myAnswers;

    /** 答题时间 单位:秒 */
    private int answerTime;

    /** 答题情况 0:未答题 1:正确 2:错误 */
    private int answerType;

    /**  单题分值 */
    private BigDecimal questionScore;

}
