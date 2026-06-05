package com.joyfishs.dawa.course.domain.vo;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CourseTypeWordVo {

    /**
     * 课程类别
     */
    private String courseType;

    /**
     * 课程
     **/
    private List<CourseWordVo> courseList;

}
