package com.joyfishs.system.config.security.weixin;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface OpenPlatformUserDetailsService {
    /**
     * load user by unionid
     *
     * @param unionid unionid
     * @return userDetails
     * @throws UsernameNotFoundException not found user
     */
    UserDetails loadUserByUnionid(String unionid) throws UsernameNotFoundException;
}
