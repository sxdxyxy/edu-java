package com.joyfishs.dawa.course.domain.vo;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CourseWordVo {
    /**
     * 序号
     */
    private int id;
    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类别
     */
    private String courseType;

    /**
     * 课程题目
     **/
    private List<QuestionWordVo> questionList = Lists.newArrayList();

}
