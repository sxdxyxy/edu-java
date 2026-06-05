package com.joyfishs.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.joyfishs.system.config.security.Md5PasswordEncoder;
import com.joyfishs.system.config.security.captcha.CaptchaUserDetailsService;
import com.joyfishs.system.entity.LoginUser;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.utils.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户验证处理
 *
 */
@Slf4j
@Service
public class CaptchaUserDetailsServiceImpl implements CaptchaUserDetailsService {
    @Autowired
    private SysUserService sysUserService;
    @Override
    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        SysUser user = sysUserService.findByPhone(phone);
        if (user == null) {
            log.info("登录手机号：{} 不存在.", phone);
            // throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
            throw new InternalAuthenticationServiceException("登录手机号用户：" + phone + " 不存在");
        } else if (1 == user.getIsDelete()) {
            log.info("登录手机号用户：{} 已被删除.", phone);
            throw new BaseException("对不起，您的账号：" + phone + " 已被删除");
        } else if (0 == user.getStatus()) {
            log.info("登录手机号用户：{} 已被停用.", phone);
            throw new BaseException("对不起，您的手机号账号：" + phone + " 已停用");
        }
        Md5PasswordEncoder.getInstance().setSalt(user.getSalt());
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        // 这里可以查询用户权限放入 LoginUser 内
        return new LoginUser().setUser(user);
    }
}
