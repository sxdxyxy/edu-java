package com.joyfishs.dawa.person.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yangkaifeng
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_person_credit")
@ApiModel(value = "PersonCredit", description = "学员积分")
public class PersonCredit {
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("学员id")
    private Long personId;

    @ApiModelProperty("积分")
    private Integer credit;
}
