package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 课程准入规则实体类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_course_admission_rule", autoResultMap = true)
@ApiModel(value = "CourseAdmissionRule", description = "课程准入规则")
public class CourseAdmissionRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    /**
     * 课程ID
     */
    @ApiModelProperty("课程ID")
    private Long courseId;

    /**
     * 规则类型（safety_score/special_cert/training_completed）
     */
    @ApiModelProperty("规则类型")
    private String ruleType;

    /**
     * 规则条件
     */
    @ApiModelProperty("规则条件")
    private String ruleCondition;

    /**
     * 规则值
     */
    @ApiModelProperty("规则值")
    private String ruleValue;

    /**
     * 是否强制检查（0=提示，1=禁止）
     */
    @ApiModelProperty("是否强制检查")
    private Boolean isMandatory;

    /**
     * 不通过时的提示信息
     */
    @ApiModelProperty("不通过时的提示信息")
    private String errorMessage;

    /**
     * 优先级
     */
    @ApiModelProperty("优先级")
    private Integer sortOrder;

    /**
     * 状态（enabled/disabled）
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createdAt;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updatedAt;
}
