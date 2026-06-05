package com.joyfishs.system.entity;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.joyfishs.system.constant.UserConstants;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录对象
 **/
@ApiModel(value = "用户名密码登录请求")
@Data
public class UsernamePasswordLoginBody {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Length(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.USERNAME_MAX_LENGTH, message = "用户名长度必须在{min}到{max}个字符之间")
    private String username;

    @ApiModelProperty(value = "用户密码", required = true)
    @NotBlank(message = "用户密码不能为空")
    @Length(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH, message = "用户密码长度必须在{min}到{max}个字符之间")
    private String password;

    private String roleCode;
}
