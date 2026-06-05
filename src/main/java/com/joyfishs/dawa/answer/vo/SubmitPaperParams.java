package com.joyfishs.dawa.answer.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 交卷 -参数
 */
@Data
@Accessors(chain = true)
public class SubmitPaperParams {

    /** 项目id */
    private Long projectId;

    /** 课程id */
    private Long courseId;

    /** 总 答题时间 单位:秒 */
    private int answerTime;

    /** 交卷类型 1:新答题 2:继续答题  提交练习时需传入此字段 */
    private int type = 1;

    /* 考试类别  1-模拟考试  2-正式考试 3-作业 */
    private Integer examType;

    private List<SubmitPaperQuestionParams> questionList = new ArrayList<>();
}
