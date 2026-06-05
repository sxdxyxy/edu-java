package com.joyfishs.dawa.answer.vo;

import java.util.*;

import lombok.Data;
import lombok.experimental.Accessors;

// 答题页面详情
@Data
@Accessors(chain = true)
public class AnswerVo {

    /**
     * 课程名称
     */
    private String courseName;

    /** 总分 */
    private Integer totalScore;

    /* 开始时间 */
    private Integer examTime;

    /**
     * 根据题目类型分类的题目
     */
    List<AnswerQuestionVo> question = new ArrayList<>();

}
