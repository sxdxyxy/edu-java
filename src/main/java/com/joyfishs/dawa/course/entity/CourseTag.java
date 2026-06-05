package com.joyfishs.dawa.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "课程标签")
@TableName("xm_course_tag")
public class CourseTag extends BaseEntity {

    @TableId(value = "id" ,type = IdType.AUTO)
    private Long id;

    /*课程ID*/
    private Long courseId;

    /*字典value值*/
    private Integer dictValue;
}
