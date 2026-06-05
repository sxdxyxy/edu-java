package com.joyfishs.dawa.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ProjectTestPaperTactics",description = "组卷策略明细")
@TableName(value ="xm_project_test_paper_tactics")
public class ProjectTestPaperTactics extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("题目类型: 1=单选  2=多选  3=判断")
    private Integer topicType;

    @ApiModelProperty("抽题数量")
    private Integer topicNum;

    @ApiModelProperty("单题分值")
    private Integer topicScore;

    @TableField(exist = false)
    Integer i;
    @TableField(exist = false)
    Integer totalNum;
    @TableField(exist = false)
    Integer totalScore;
}
