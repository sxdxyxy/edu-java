package com.joyfishs.system.config.security.weixin;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class OpenPlatformAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;

    private String unionid;

    /**
     * 此构造函数用来初始化未授信凭据.
     *
     * @param unionid   the unionid
     */
    public OpenPlatformAuthenticationToken(String unionid) {
        super(null);
        this.unionid = unionid;
        setAuthenticated(false);
    }
    public OpenPlatformAuthenticationToken(Object principal, String unionid, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.unionid = unionid;
        super.setAuthenticated(true);
    }

    public String getUnionid() {
        return unionid;
    }

    @Override
    public Object getCredentials() {
        return this.unionid;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.unionid = null;
    }
}
