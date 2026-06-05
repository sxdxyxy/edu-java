package com.joyfishs.system.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 新增用户请求参数
 * 安全修复: 将密码从 @RequestParam 改为 @RequestBody,避免密码出现在URL参数中
 */
@ApiModel(value = "新增用户请求")
@Data
public class AddUserParam {

    @ApiModelProperty(value = "用户名称", required = true)
    @NotBlank(message = "用户名称不能为空")
    @Size(min = 2, max = 30, message = "用户名称长度必须在2到30个字符之间")
    private String name;

    @ApiModelProperty(value = "登录账号", required = true)
    @NotBlank(message = "登录账号不能为空")
    @Size(min = 4, max = 20, message = "登录账号长度必须在4到20个字符之间")
    private String userName;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    private String password;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "用户性别 0-未知 1-男 2-女")
    private Integer sex;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "身份证号码")
    private String idCardNo;
}
