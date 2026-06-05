package com.joyfishs.dawa.practice.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.system.entity.BaseEntity;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author aoli.qu
 * @date 2022-01-07 10:06
 */
@Data
@Accessors(chain = true)
@TableName("xm_practice_record")
@ApiModel(value = "PracticeRecord", description = "练习-答题记录")
public class PracticeRecord extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("课程id")
    private Long courseId;

    @ApiModelProperty("答题报告id")
    private Long reportId;

    @ApiModelProperty("练习时间")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private Date practiceTime;

    @ApiModelProperty("课程名称")
    @TableField(exist = false)
    private String courseName;

    @ApiModelProperty("题目总数")
    @TableField(exist = false)
    private int totalQuestions;

    @ApiModelProperty("答对题数")
    @TableField(exist = false)
    private int rightQuestions;

    @ApiModelProperty("交卷时间")
    @TableField(exist = false)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private Date submitTime;

    @TableField(exist = false)
    @ApiModelProperty("练习类型 0:未完成 1:已完成")
    private int type;
}
