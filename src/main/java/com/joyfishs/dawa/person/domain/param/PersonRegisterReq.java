package com.joyfishs.dawa.person.domain.param;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ykfnb
 */

@Data
@ApiModel(value = "PersonRegisterReq", description = "学员注册请求")
public class PersonRegisterReq{
    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号码")
    @NotEmpty(message = "请输入手机号码")
    private String phone;

    @ApiModelProperty("密码可选")
    private String password;

    @ApiModelProperty("短信验证码")
    @NotEmpty(message = "请输入注册短信验证码")
    private String captcha;

    @ApiModelProperty("注册客户端类型：app、web、mini_program")
    @NotEmpty(message = "请设置注册客户端类型")
    private String deviceType;

    @ApiModelProperty("openid")
    private String openid;

    @ApiModelProperty("unionid")
    private String unionid;

    @ApiModelProperty("注册机构")
    private Long orgId;
}
