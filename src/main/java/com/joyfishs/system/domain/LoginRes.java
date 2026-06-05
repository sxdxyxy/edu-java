package com.joyfishs.system.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ykfnb
 */

@Data
@ApiModel(value = "LoginRes", description = "登录响应结果")
public class LoginRes implements Serializable {
    @ApiModelProperty("是否经过认证")
    private Boolean identity;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("登录状态码，200=正常，404=没有此认证方式账号，如果可以请进行手机号绑定流程，401=认证不通过，403=其它错误提示")
    private Integer code;

    @ApiModelProperty("错误提示消息")
    private String errorMsg;

    @ApiModelProperty("openid")
    private String openid;
    @ApiModelProperty("unionid")
    private String unionid;

    public LoginRes() {
        this.code = 200;
    }

    public LoginRes setError(String msg) {
        this.errorMsg = msg;
        this.code = 403;
        return this;
    }

    public LoginRes setCode(int code) {
        this.code = code;
        return this;
    }

    public LoginRes setError(String msg, Integer code) {
        this.errorMsg = msg;
        this.code = code;
        return this;
    }
}
