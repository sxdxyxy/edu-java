package com.joyfishs.dawa.person.domain.param;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改身份证信息参数
 */
@Data
@ApiModel(value = "刷脸登录请求")
public class BindWeixinParam {

    @NotBlank
    @ApiModelProperty(value = "openid", required = true)
    private String openid;

    @NotBlank
    @ApiModelProperty(value = "unionid", required = true)
    private String unionid;
}
