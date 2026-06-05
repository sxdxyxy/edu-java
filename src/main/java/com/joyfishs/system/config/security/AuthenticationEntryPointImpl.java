package com.joyfishs.system.config.security;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.utils.AjaxResult;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;

/** 认证失败处理类 返回未授权 **/
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID =  SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        String msg = String.format("请求访问：%s，认证失败，用户未登录或已过期。", request.getRequestURI());
        response.setContentType("text/json;charset=utf-8");
        ServletUtil.write(response, JSONObject.toJSONString(AjaxResult.error(HttpStatus.UNAUTHORIZED.value(), msg)), ContentType.JSON.toString(Charset.defaultCharset()));
    }
}
