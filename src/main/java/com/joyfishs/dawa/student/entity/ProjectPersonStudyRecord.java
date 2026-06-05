package com.joyfishs.dawa.student.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 * @author yangkaifeng
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "学习记录")
@TableName(value = "xm_project_person_study_record")
public class ProjectPersonStudyRecord extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("人员id")
    private Long personId;

    @ApiModelProperty("参与状态 0-未参与  1-进行中  2-已完成")
    private Integer status;

    @ApiModelProperty("课程id")
    private Long courseId;

    @ApiModelProperty("课件id")
    private Long coursewareId;

    @ApiModelProperty("视频id")
    private Long videoId;

    @ApiModelProperty("通知公告id")
    private Long noticeId;

    @ApiModelProperty("学时")
    private BigDecimal studyHours = BigDecimal.ZERO;

    @ApiModelProperty("获得的学分")
    private BigDecimal credit = BigDecimal.ZERO;

    @ApiModelProperty("抽取的题目id")
    private Long topicId;

    @ApiModelProperty("答题次数 默认2次")
    private Integer answerCount;
}
