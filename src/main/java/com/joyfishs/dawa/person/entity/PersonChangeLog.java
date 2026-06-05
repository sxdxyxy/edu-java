package com.joyfishs.dawa.person.entity;

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
 *
 *
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@TableName(value = "xm_person_change_log")
@ApiModel(value = "PersonChangeLog", description = "学员信息变动")
public class PersonChangeLog extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("部门信息")
    private Long orgId;

    @ApiModelProperty("变动类型")
    private String changeType;

    @ApiModelProperty("部门名称")
    @TableField(exist = false)
    private String orgName;

}
