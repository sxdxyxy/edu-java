package com.joyfishs.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.entity.PersonRegister;
import com.joyfishs.dawa.person.service.PersonRegisterService;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.system.config.security.Md5PasswordEncoder;
import com.joyfishs.system.config.security.weixin.OpenPlatformUserDetailsService;
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
public class OpenPlatformUserDetailsServiceImpl implements OpenPlatformUserDetailsService {
    @Autowired
    private PersonRegisterService personRegisterService;
    @Autowired
    private PersonService personService;
    @Autowired
    private SysUserService sysUserService;
    @Override
    public UserDetails loadUserByUnionid(String unionid)  throws UsernameNotFoundException {
        PersonRegister register = personRegisterService.findByUnionid(unionid);
        if (register == null) {
            log.info("登录unionid：{} 还没有绑定账号.", unionid);
            throw new UsernameNotFoundException("登录unionid：" + unionid + " 还没有绑定账号");
        }
        Person person = personService.getById(register.getPersonId());
        SysUser user = sysUserService.getById(person.getUserId());
        if (1 == user.getIsDelete()) {
            log.info("用户账号：{} 已被删除.", person.getUserName());
            throw new BaseException("您的账号：" + person.getUserName() + " 已被删除");
        } else if (0 == user.getStatus()) {
            log.info("用户账号：{} 已被停用.", person.getUserName());
            throw new BaseException("用户账号：" + person.getUserName() + " 已停用");
        }
        Md5PasswordEncoder.getInstance().setSalt(user.getSalt());
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        // 这里可以查询用户权限放入 LoginUser 内
        return new LoginUser().setUser(user);
    }
}
