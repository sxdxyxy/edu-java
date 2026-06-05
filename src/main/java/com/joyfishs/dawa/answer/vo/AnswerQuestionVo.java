package com.joyfishs.dawa.answer.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 答题题目详情
 * @author aoli.qu
 * @date 2022-01-12 22:49
 */
@Data
@Accessors(chain = true)
public class AnswerQuestionVo {

    /**
     * 题目类型 1-单选题 2-多选题 3-判断题  4-简答题 5-填空题
     */
    private String questionType;

    /** 题目数量 */
    Integer questionCount = 0;

    /* 已答题数量*/
    Integer answeredCount = 0;

    /** 单题分值 */
    Integer questionScore = 0;

    /** 当前类型题目总分 */
    Integer totalScore = 0;

    /** 题目列表 */
    private List<QuestionVo> questionList = new ArrayList<>();

}
