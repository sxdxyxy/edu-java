package com.joyfishs.system.config.async;

import java.util.Date;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import com.joyfishs.system.entity.SysOperLog;
import com.joyfishs.system.service.SysOperLogService;
import com.joyfishs.utils.Constants;
import com.joyfishs.utils.HttpServletUtil;
import com.joyfishs.utils.SpringUtil;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;

/** 异步工厂（产生任务用） **/
@Slf4j
public class AsyncFactory {

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLoginInfo(final String username, final String status, final String message,
                                            final Object... args) {
        final UserAgent userAgent = UserAgentUtil.parse(HttpServletUtil.getRequest().getHeader("User-Agent"));
        final String ip = ServletUtil.getClientIP(HttpServletUtil.getRequest(), "");
        return new TimerTask() {
            @Override
            public void run() {
                String address = "";
                String os = userAgent.getOs().getName();  // 获取客户端操作系统
                String browser = userAgent.getBrowser().getName(); // 获取客户端浏览器

                // 封装对象
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setTitle("用户登陆");
                sysOperLog.setMethod("/login");
                HttpServletRequest request = HttpServletUtil.getRequest();
                sysOperLog.setRequestMethod(request == null ? "GET" : request.getMethod());
                sysOperLog.setOperName(username);
                sysOperLog.setOperIp(ip);
                sysOperLog.setOperLocation(address);
                sysOperLog.setErrorMsg(browser+";"+os+";"+message);
                sysOperLog.setOperTime(new Date());

                // 日志状态
                if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status)) sysOperLog.setStatus(Integer.parseInt(Constants.SUCCESS));
                else if (Constants.LOGIN_FAIL.equals(status)) sysOperLog.setStatus(Integer.parseInt(Constants.FAIL));

                // 插入数据
                SpringUtil.getBean(SysOperLogService.class).save(sysOperLog);
            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param sysOperLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog sysOperLog) {
        return new TimerTask() {
            @Override
            public void run() {
                // 远程查询操作地点
                SpringUtil.getBean(SysOperLogService.class).save(sysOperLog);
            }
        };
    }
}
