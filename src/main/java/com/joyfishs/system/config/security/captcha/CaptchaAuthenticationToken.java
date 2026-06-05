package com.joyfishs.system.config.security.captcha;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CaptchaAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;
    private String phone;

    private String captcha;

    /**
     * 此构造函数用来初始化未授信凭据.
     *
     * @param phone   the phone
     * @param captcha the captcha
     */
    public CaptchaAuthenticationToken(String phone, String captcha) {
        super(null);
        this.phone = phone;
        this.captcha = captcha;
        setAuthenticated(false);
    }

    public CaptchaAuthenticationToken(Object principal, String captcha, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.captcha = captcha;
        super.setAuthenticated(true);
    }

    public String getPhone() {
        return phone;
    }

    public String getCaptcha() {
        return captcha;
    }

    @Override
    public Object getCredentials() {
        return this.captcha;
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
        captcha = null;
    }
}
