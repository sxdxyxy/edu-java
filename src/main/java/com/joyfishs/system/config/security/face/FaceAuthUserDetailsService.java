package com.joyfishs.system.config.security.face;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface FaceAuthUserDetailsService {
    /**
     * load user by personId
     *
     * @param personId personId
     * @return userDetails
     * @throws UsernameNotFoundException not found user
     */
    UserDetails loadUserByPersonId(Long personId) throws UsernameNotFoundException;
}
