package com.joyfishs.system.entity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joyfishs.system.entity.vo.PersonVo;
import com.joyfishs.system.pojo.LoginMenuTreeNode;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录用户身份权限
 *
 */
@Data
@Accessors(chain = true)
public class LoginUser implements UserDetails {
    private static final long serialVersionUID =  SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /** 用户唯一标识 **/
    private String token;

    /** 登录时间 **/
    private Long loginTime;

    /** 过期时间 **/
    private Long expireTime;

    /** 登录IP地址 **/
    private String ipaddr;

    /** 登录地点 **/
    private String loginLocation;

    /** 浏览器类型 **/
    private String browser;

    /** 操作系统 **/
    private String os;

    /** 用户信息 **/
    private SysUser user;

    /** 所有角色信息 **/
    private List<SysRole> roleList;

    /** 当前角色编码 **/
    private String currentRoleCode;

    /** 当前角色信息 **/
    private SysRole currentRole;

    /** 权限列表 **/
    private Set<String> permissions;

    /** 登录菜单信息，AntDesign版本菜单 **/
    private List<LoginMenuTreeNode> menus;

    /* 学员信息 */
    private PersonVo person;

    /** 是否超级管理员 **/
    public boolean ifAdmin(){
        if("admin".equals(this.user.getUserName())) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    @Override
    public String getPassword(){ return user.getPassword(); }

    @Override
    public String getUsername(){ return user.getUserName(); }

    public String getName(){ return user.getName(); }

    /** 账户是否未过期,过期无法验证 **/
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /** 指定用户是否解锁,锁定的用户无法进行身份验证 **/
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    /** 指示是否已过期的用户的凭据(密码),过期的凭据防止认证 **/
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /** 是否可用 ,禁用的用户不能身份验证 **/
    @JsonIgnore
    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return null;
    }
}
