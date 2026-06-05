package com.joyfishs.system.entity;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.joyfishs.system.constant.UserConstants;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 **/
@ApiModel(value = "修改密码请求")
@Data
public class PasswordBody {

    @ApiModelProperty(value = "用户密码", required = true)
    @NotBlank(message = "用户密码不能为空")
    @Length(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH, message = "用户密码长度必须在{min}到{max}个字符之间")
    private String password;
}
