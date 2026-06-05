package com.joyfishs.system.config.security;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.system.config.async.AsyncFactory;
import com.joyfishs.system.config.async.AsyncManager;
import com.joyfishs.system.entity.LoginUser;
import com.joyfishs.system.service.TokenService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.Constants;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;

/** 自定义退出处理类 返回成功 **/
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler
{
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);

        if (null != loginUser) {
            String userName = loginUser.getUsername();

            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());

            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLoginInfo(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletUtil.write(response, JSONObject.toJSONString(AjaxResult.success("退出成功")), ContentType.JSON.toString(Charset.defaultCharset()));
    }
}
