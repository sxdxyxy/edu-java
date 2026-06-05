package com.joyfishs.dawa.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 必选题设置-课件题目关联
 * 课件必选题
 */

@Data
@ApiModel(value = "课件必选题")
@Accessors(chain = true)
@TableName("xm_courseware_required_question")
public class CoursewareRequiredQuestion extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /*课件ID*/
    private Long coursewareId;

    /*题目ID*/
    private Long questionId;

}
