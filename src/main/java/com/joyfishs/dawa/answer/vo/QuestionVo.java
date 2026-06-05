package com.joyfishs.dawa.answer.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.joyfishs.dawa.course.entity.CourseQuestion;
import com.joyfishs.utils.StringUtils;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.experimental.Accessors;

// 题目明细
@Data
@Accessors(chain = true)
public class QuestionVo {

    /** 序号 */
    private int orderNumber;

    /** 题目id */
    private Long questionId;

    /** 题目名称 */
    private String questionName;

    /** 题目类型 1-单选题 2-多选题 3-判断题  4-简答题 5-填空题 */
    private String questionType;

    /** 答案A */
    private String answerA;

    /** 答案B */
    private String answerB;

    /** 答案C */
    private String answerC;

    /** 答案D */
    private String answerD;

    /** 答案E */
    private String answerE;

    /** 答案F */
    private String answerF;

    /** 答案解析 */
    private String answerParse;

    /** 正确答案，答案A/B/C/D (多选题使用逗号隔开) */
    private String rightAnswers;

    /** 全站正确率 */
    private BigDecimal accuracyRate;

    /** 易错项 */
    private String wrongItem;

    /** 答题时间 单位:秒 */
    private int answerTime;

    /** 答题情况 0:未答题 1:正确 2:错误 */
    private int answerType;

    /** 是否收藏 0:否 1:是 */
    private int isCollection;

    /** 我的答案 */
    private String myAnswers;

    public List<String> getRightAnswerList(){
        List<String> charList = new ArrayList<>();
        if(this.rightAnswers == null) return charList;
        if("2".equals(this.questionType)) { //多选题
            char[] chars = this.rightAnswers.toCharArray();
            for (char aChar : chars) {
                charList.add(String.valueOf(aChar));
            }
        } else if ("5".equals(this.questionType)){ //填空题
            String[] split = this.rightAnswers.split(CourseQuestion.Completion_rightAnswers_fengefu);
            for (String s : split) {
                charList.add(s);
            }
        } else { // 其他题型，直接把正确答案放进集合
            charList.add(this.rightAnswers);
        }


        return charList;
    }

    /** 我的答案 返回给前端集合 */
    private List<String> myAnswerList;
    public List<String> getMyAnswerList(){
        if (StringUtils.isEmpty(this.getMyAnswers())) return new ArrayList<>();
        return (JSONUtil.toList(JSONUtil.parseArray(this.myAnswers), String.class));
    }

    /** 课中检测 接收前端我的答案数据 */
    public List<String> getAnswerList(){
        return this.myAnswerList;
    }

    /** 练习分类->课程类别（0006） */
    private String courseType;

    /** 练习分类名称*/
    private String courseTypeName;

}
