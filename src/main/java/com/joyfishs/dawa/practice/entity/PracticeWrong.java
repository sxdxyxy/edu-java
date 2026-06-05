package com.joyfishs.dawa.practice.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@TableName("xm_practice_wrong")
@ApiModel(value = "PracticeRecord", description = "练习-我的错题集")
public class PracticeWrong {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("题目id")
    private Long questionId;

    @ApiModelProperty("我的答案")
    private String myAnswers;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN, timezone = "GMT+8")
    private Date createTime;

}
