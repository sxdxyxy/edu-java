package com.joyfishs.dawa.plan.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 项目管理通用字典选择器
 *
 * @TableName xm_project_dict
 */

@Data
@Accessors(chain = true)
@ApiModel(value = "TrainPlan", description = "培训计划")
@TableName(value = "xm_train_plan")
public class TrainPlan extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("发布组织")
    private Long orgId;

    @ApiModelProperty("发布组织名称")
    @TableField(exist = false)
    private String orgName;

    @ApiModelProperty("类型 2-年度培训计划  3-单位培训计划")
    private Integer type;

    @ApiModelProperty("培训计划名称")
    private String name;

    @ApiModelProperty("年度")
    private Integer year;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("项目id列表")
    @TableField(exist = false)
    private List<Long> projectIds;

    @ApiModelProperty("项目实体列表")
    @TableField(exist = false)
    private List<Project> projects;

    @ApiModelProperty("创建人员名字")
    @TableField(exist = false)
    private String createName;
}
