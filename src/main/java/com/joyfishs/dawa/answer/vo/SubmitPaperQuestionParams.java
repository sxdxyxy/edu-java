package com.joyfishs.dawa.answer.vo;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 交卷- 题目 -参数
 */
@Data
@Accessors(chain = true)
public class SubmitPaperQuestionParams {

    /** 题目id */
    private Long questionId;

    /** 当前题目 答题时间 单位:秒 */
    private int answerTime;

    /** 我的答案 */
    private List<String> myAnswerList;
}
