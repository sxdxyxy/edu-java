package com.joyfishs.dawa.course.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 考试题目表
 * </p>
 */
@Data
@Accessors(chain = true)
public class QuestionWordVo {
    /**
     * 序号
     */
    private int id;
    /**
     * 题目名称
     */
    private String questionName;

    /**
     * 题目类型 1-单选题 2-多选题 3-判断题  4-简答题 5-填空题
     */
    private String questionType;

    /**
     * A选项
     */
    private String answerA;

    /**
     * B选项
     */
    private String answerB;

    /**
     * C选项
     */
    private String answerC;

    /**
     * D选项
     */
    private String answerD;

    /**
     * E选项
     */
    private String answerE;

    /**
     * F选项
     */
    private String answerF;

    /**
     * 答案解析
     */
    private String answerParse;

    /**
     * 正确答案，答案A/B/C/D (多选题使用逗号隔开)
     */
    private String rightAnswers;

}
