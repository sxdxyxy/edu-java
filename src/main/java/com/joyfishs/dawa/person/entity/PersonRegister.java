package com.joyfishs.dawa.person.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;
import com.joyfishs.system.enums.DeviceType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@TableName(value = "xm_person_register")
@ApiModel(value = "PersonRegister", description = "学员注册信息")
public class PersonRegister extends BaseEntity {

    public static final String DEFAULT_ORG_CODE = "grdw";

    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("用户名")
    private String userName;

    private DeviceType regDevice;

    private String openid;

    private String unionid;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("部门信息")
    private Long orgId;

    @ApiModelProperty("人员id ")
    private Long personId;

}
