package com.joyfishs.dawa.exam.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 *  考试 - 单题分数、题目类型、题目的正确答案（考试题目详情）
 */

@Data
public class ExamQuestionVO {

    private static final String Completion_rightAnswers_fengefu = "<{[CDATA]}>";

    /* 题目ID */
    private Long questionId;

    /* 题目类型 */
    private Integer questionType;

    /* 单题分值 */
    private BigDecimal topicScore;

    /* 正确答案 */
    private String rightAnswers;

    /**
     * 正确答案转list
     * 类型是2 多选题  5 填空题  时使用
     * @return
     */
    public List<String> getRightAnswersList(){
        List<String> charList = new ArrayList<>();
        if(this.rightAnswers == null) return charList;
        if(this.questionType != null && this.questionType == 2) { //多选题
            char[] chars = this.rightAnswers.toCharArray();
            for (char aChar : chars) {
                charList.add(String.valueOf(aChar));
            }
        }
        if (this.questionType != null && this.questionType == 5){ //填空题
            String[] split = this.rightAnswers.split(Completion_rightAnswers_fengefu);
            for (String s : split) {
                charList.add(s);
            }
        }
        return charList;
    }
}
