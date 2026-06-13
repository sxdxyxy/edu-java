package com.joyfishs.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.joyfishs.system.entity.LoginUser;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.vo.PersonVo;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.util.ObjUtil;

import java.util.List;

/**
 * 安全服务工具类
 **/
public class SecurityUtil {

    private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);

    /**
     * 获取Authentication
     **/
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 当前是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        String principal = getAuthentication().getPrincipal().toString();
        if ("anonymousUser".equals(principal)) return false;
        return true;
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new CustomException("获取用户信息异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取用户ID
     **/
    public static Long getUserId() {
        try {
            return getLoginUser().getUser().getId();
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取用户账户
     **/
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取人员ID
     **/
    public static Long getPersonId() {
        try {
            PersonVo vo = getLoginUser().getPerson();
            return ObjUtil.isNull(vo) ? null : vo.getId();
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 是否单位管理员
     **/
    public static boolean isAdmin() {
        try {
            PersonVo vo = getLoginUser().getPerson();
            return vo.getIsAdmin() == 1;
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取人员所在单位部门ID
     **/
    public static Long getOrgId() {
        try {
            PersonVo vo = getLoginUser().getPerson();
            return ObjUtil.isNull(vo) ? null : vo.getOrgId();
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取人员所在单位部门ID（如果没有，返回null）
     * 这个方法不会抛出异常，而是返回null
     **/
    public static Long getOrgIdOrNull() {
        try {
            PersonVo vo = getLoginUser().getPerson();
            return ObjUtil.isNull(vo) ? null : vo.getOrgId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取人员有权限管理的所有机构 ID 列表（用于数据过滤）
     * 管理员返回其所在机构及下属机构，普通用户返回 null
     * @return 机构 ID 列表，null 表示无限制（超级管理员）
     */
    public static java.util.List<Long> getManagedOrgIds() {
        try {
            PersonVo vo = getLoginUser().getPerson();
            if (ObjUtil.isNull(vo)) {
                return null;
            }

            // 如果是超级管理员（admin 用户），返回 null 表示无限制
            if ("admin".equals(getLoginUser().getUsername())) {
                return null;
            }

            // D23-1: 公司管理员 (sys_role.code='company_manager') 限定本机构
            //   与 isAdmin() 互斥 — admin 走 ALL, company_manager 走本机构
            if (isCompanyManager()) {
                java.util.List<Long> orgIds = new java.util.ArrayList<>();
                if (vo.getOrgId() != null) {
                    orgIds.add(vo.getOrgId());
                }
                return orgIds;
            }

            // 如果是单位管理员，返回其管理的机构 ID 列表
            if (vo.getIsAdmin() == 1) {
                // 这里可以扩展为查询该管理员管理的所有机构
                // 目前返回其主机构 ID
                java.util.List<Long> orgIds = new java.util.ArrayList<>();
                if (vo.getOrgId() != null) {
                    orgIds.add(vo.getOrgId());
                }
                return orgIds;
            }

            // 普通用户，返回其所属机构 ID
            java.util.List<Long> orgIds = new java.util.ArrayList<>();
            if (vo.getOrgId() != null) {
                orgIds.add(vo.getOrgId());
            }
            return orgIds;
        } catch (Exception e) {
            throw new CustomException("获取管理机构列表异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 生成 BCrypt 加密密码
     */
    public static String encryptPassword(String password) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        return "{bcrypt}" + passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = SpringUtil.getBean(org.springframework.security.crypto.password.PasswordEncoder.class);
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * D23-1: 判断当前用户是否为「公司管理员」(sys_role.code='company_manager')
     * 与 isAdmin() 互斥 — admin 走 ALL, company_manager 走本机构
     * 失败时返回 false (fallback 到无限制), 不抛异常 — 调用方在 getManagedOrgIds 数据过滤路径上
     * 宁可宽松过滤也别 500; 角色识别失败就当普通用户处理.
     */
    public static boolean isCompanyManager() {
        try {
            LoginUser loginUser = getLoginUser();
            if (loginUser == null || loginUser.getUser() == null) {
                return false;
            }
            // 优先看 currentRoleCode (单角色登录态), 回退到 roleList.code 多角色态
            String currentCode = loginUser.getCurrentRoleCode();
            if (currentCode != null && SysRole.COMPANY_MANAGER_CODE.equals(currentCode)) {
                return true;
            }
            List<SysRole> roles = loginUser.getRoleList();
            if (roles != null) {
                for (SysRole r : roles) {
                    if (r != null && SysRole.COMPANY_MANAGER_CODE.equals(r.getCode())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("isCompanyManager failed, falling back to false", e);
            return false;
        }
    }
}
