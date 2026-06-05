package com.joyfishs.system.entity;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "短信验证码登录或者重置密码请求")
@Data
public class CaptchaLoginBody {

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号")
    @Length(min = 11, max = 11, message = "手机号长度必须是{min}位")
    private String phone;

    @ApiModelProperty(value = "短信验证码", required = true)
    @NotBlank(message = "短信验证码")
    @Length(min = 6, max = 6, message = "验证码长度是{min}位")
    private String captcha;

}
