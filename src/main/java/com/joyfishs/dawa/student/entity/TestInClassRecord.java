package com.joyfishs.dawa.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yangkaifeng
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "答题记录")
@TableName("xm_test_in_class_record")
public class TestInClassRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("课程id")
    private Long courseId;

    @ApiModelProperty("课件id")
    private Long coursewareId;

    @ApiModelProperty("题目id")
    private Long questionId;

    @ApiModelProperty("答案")
    private String answer;

    @ApiModelProperty("是否正确 0-不正确 1正确")
    private Integer correct;
}
