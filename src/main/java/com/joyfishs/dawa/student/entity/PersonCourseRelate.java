package com.joyfishs.dawa.student.entity;

import java.math.BigDecimal;

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
 * @description: 人员课程关联
 */
@Data
@ApiModel(value = "我学习的课程和学时")
@TableName("xm_person_course_relate")
@Accessors(chain = true)
public class PersonCourseRelate extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("通知公告id")
    private Long noticeId;

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("是否签到： 1-未签到 2-已签到 3-已学完")
    private Integer isSign;

    @ApiModelProperty("学时")
    private BigDecimal learnHours;

    @ApiModelProperty("总学分")
    private BigDecimal learnScore;

    @ApiModelProperty("是否参加考试 0-否  1-是")
    private Integer ifJoinExam;

    @ApiModelProperty("准考证号")
    private String candidateNo;

}
