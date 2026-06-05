package com.joyfishs.dawa.course.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.dawa.course.enums.QuestionType;
import com.joyfishs.system.entity.BaseEntity;
import com.joyfishs.utils.StringUtils;

import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 考试题目表
 * </p>
 *
 */
@Data
@ApiModel(value = "课程题目")
@Accessors(chain = true)
@TableName("xm_course_question")
public class CourseQuestion extends BaseEntity {
    public static final String Completion_rightAnswers_fengefu       = "<\\{\\[CDATA]\\}\\>";
    public static final String Completion_rightAnswers_fengefu_spilt = "\\<\\{\\[CDATA\\]\\}\\>";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程Id
     */
    private Long courseId;

    @TableField(exist = false)
    private String courseName;

    /**
     * 题目名称
     */
    @TableField("question_name")
    private String questionName;

    /**
     * 题目类型 1-单选题 2-多选题 3-判断题  4-简答题 5-填空题
     */
    @TableField("question_type")
    private QuestionType questionType;

    @TableField(exist = false)
    private String questionTypeName;

    /**
     * A选项
     */
    @TableField("answerA")
    private String answerA;

    /**
     * B选项
     */
    @TableField("answerB")
    private String answerB;

    /**
     * C选项
     */
    @TableField("answerC")
    private String answerC;

    /**
     * D选项
     */
    @TableField("answerD")
    private String answerD;

    /**
     * E选项
     */
    @TableField("answerE")
    private String answerE;

    /**
     * F选项
     */
    @TableField("answerF")
    private String answerF;

    /**
     * 答案解析
     */
    @TableField("answer_parse")
    private String answerParse;

    /**
     * 正确答案，答案A/B/C/D (多选题使用逗号隔开)
     */
    @TableField("right_answers")
    private String rightAnswers;

    public String getRightAnswers(){
        if (QuestionType.FillInTheBlank.equals(this.questionType)) {
            return StringUtils.join(this.getAnswerList(), Completion_rightAnswers_fengefu);
        }
        return this.rightAnswers;
    }

    /**
     * 正确答案转list
     * 类型是2 多选题  5 填空题  时使用
     * @return
     */
    public List<String> getRightAnswersList(){
        List<String> charList = new ArrayList<>();
        if(this.rightAnswers == null) {
            return charList;
        }
        //多选题
        if(QuestionType.MultipleChoice.equals(this.questionType)) {
            char[] chars = this.rightAnswers.toCharArray();
            for (char aChar : chars) {
                charList.add(String.valueOf(aChar));
            }
            //填空题
        }else if (QuestionType.FillInTheBlank.equals(this.questionType)){
            String[] split = this.rightAnswers.split(Completion_rightAnswers_fengefu);
            for (String s : split) {
                charList.add(s);
            }
        }else {
            charList.add(rightAnswers);
        }

        return charList;
    }
    public String getRightAnswersDesc() {
        String answers = getRightAnswers();
        if (answers == null) {
            return "";
        }
        if (QuestionType.TrueOrFalse.equals(questionType)) {
            if ("A".equals(answers)) {
                return "正确";
            } else if ("B".equals(answers)) {
                return "错误";
            }
        }
        return answers;
    }
    /**
    * 用于接收前台传入的填空题答案
    */
    @TableField(exist = false)
    private List<String> answerList;
    public List<String> getAnswerList(){
        if (QuestionType.FillInTheBlank.equals(this.questionType) && CollUtil.isEmpty(this.answerList)) {
            return Arrays.asList(this.rightAnswers.split(Completion_rightAnswers_fengefu_spilt));
        }
        return this.answerList;
    }


    public List<String> getAnswerListSource(){
        return this.answerList;
    }

}
