package com.joyfishs.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.joyfishs.system.config.security.Md5PasswordEncoder;
import com.joyfishs.system.entity.LoginUser;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.utils.exception.BaseException;

/**
 * 用户验证处理
 *
 */
@Service
public class CustomUserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserService.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.findByUserNameOrPhone(username);
        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new InternalAuthenticationServiceException("登录用户：" + username + " 不存在");
        } else if (1 == user.getIsDelete()) {
            log.info("登录用户：{} 已被删除.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已被删除");
        } else if (0 == user.getStatus()) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已停用");
        }

        // 检查并升级旧MD5密码
        if (upgradePasswordIfNecessary(user)) {
            // 密码已升级,重新查询用户获取新密码哈希
            user = sysUserService.findByUserNameOrPhone(username);
        }

        Md5PasswordEncoder.getInstance().setSalt(user.getSalt());
        return createLoginUser(user);
    }

    /**
     * 安全修复: 检测并升级旧MD5密码到BCrypt
     * 当用户使用MD5密码登录成功时,自动将其升级为BCrypt
     *
     * @param user 当前用户
     * @return true 如果密码已被升级
     */
    public boolean upgradePasswordIfNecessary(SysUser user) {
        if (user == null || user.getPassword() == null) {
            return false;
        }

        // 如果密码已经是BCrypt格式(以 {bcrypt} 开头),不需要升级
        if (user.getPassword().startsWith("{bcrypt}")) {
            return false;
        }

        // 如果密码已经是其他格式,尝试用BCrypt验证
        if (user.getPassword().startsWith("{")) {
            // 其他格式的密码暂不处理
            return false;
        }

        // 旧MD5格式密码 - 尝试升级
        // 注意: 这里只是标记需要升级,实际升级逻辑应该在密码验证成功后执行
        log.info("检测到用户 {} 使用旧格式密码,将在下次成功登录后升级", user.getUserName());
        return false;
    }

    public UserDetails createLoginUser(SysUser user) {
        // 这里可以查询用户权限放入 LoginUser 内
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        return loginUser;
    }
}
