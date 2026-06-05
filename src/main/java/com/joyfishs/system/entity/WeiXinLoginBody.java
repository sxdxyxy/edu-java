package com.joyfishs.system.entity;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "微信登录请求")
@Data
public class WeiXinLoginBody {

    @ApiModelProperty(value = "微信OAuth code", required = true)
    @NotBlank(message = "微信OAuth code")
    private String code;

    @ApiModelProperty(required = false)
    private String state;
}
