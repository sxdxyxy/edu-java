package com.joyfishs.system.config.security.face;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class FaceAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;

    private Long personId;

    /**
     * 此构造函数用来初始化未授信凭据.
     *
     * @param personId   the personId
     */
    public FaceAuthenticationToken(Long personId) {
        super(null);
        this.personId = personId;
        setAuthenticated(false);
    }
    public FaceAuthenticationToken(Object principal, Long personId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.personId = personId;
        super.setAuthenticated(true);
    }

    public Long getPersonId() {
        return this.personId;
    }

    @Override
    public Object getCredentials() {
        return this.personId;
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
        this.personId = null;
    }
}
