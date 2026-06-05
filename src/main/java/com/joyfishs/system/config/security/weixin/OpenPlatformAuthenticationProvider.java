package com.joyfishs.system.config.security.weixin;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenPlatformAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final OpenPlatformUserDetailsService openPlatformUserDetailsService;
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public OpenPlatformAuthenticationProvider(OpenPlatformUserDetailsService openPlatformUserDetailsService) {
        this.openPlatformUserDetailsService = openPlatformUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(OpenPlatformAuthenticationToken.class, authentication, () -> messages.getMessage("OpenPlatformAuthenticationProvider.onlySupports", "Only OpenPlatformAuthenticationToken is supported"));

        OpenPlatformAuthenticationToken unAuthenticationToken = (OpenPlatformAuthenticationToken) authentication;
        String unionid = unAuthenticationToken.getUnionid();
        log.info("登录unionid：{}", unionid);
        UserDetails userDetails = openPlatformUserDetailsService.loadUserByUnionid(unionid);
        if (ObjectUtil.isNotNull(userDetails)) {
            return createSuccessAuthentication(authentication, userDetails);
        } else {
            throw new UsernameNotFoundException("还没有绑定账号");
        }
    }

    /**
     * 认证成功将非授信凭据转为授信凭据.
     * 封装用户信息 角色信息。
     *
     * @param authentication the authentication
     * @param user           the user
     * @return the authentication
     */
    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection<? extends GrantedAuthority> authorities = authoritiesMapper.mapAuthorities(user.getAuthorities());
        OpenPlatformAuthenticationToken authenticationToken = new OpenPlatformAuthenticationToken(user, null, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OpenPlatformAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(openPlatformUserDetailsService, "openPlatformUserDetailsService must not be null");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
