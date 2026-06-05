package com.joyfishs.system.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class LoginUserTest {

    @Test
    void loginUser_CreationAndGetters() {
        LoginUser user = new LoginUser();
        user.setToken("test-token");
        user.setLoginTime(System.currentTimeMillis());
        user.setExpireTime(System.currentTimeMillis() + 3600000);
        user.setIpaddr("127.0.0.1");
        user.setLoginLocation("localhost");
        user.setBrowser("Chrome");
        user.setOs("Windows 10");

        assertEquals("test-token", user.getToken());
        assertEquals("127.0.0.1", user.getIpaddr());
        assertEquals("localhost", user.getLoginLocation());
        assertEquals("Chrome", user.getBrowser());
        assertEquals("Windows 10", user.getOs());
    }

    @Test
    void loginUser_Permissions() {
        LoginUser user = new LoginUser();
        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:user:add");
        user.setPermissions(permissions);

        assertEquals(2, user.getPermissions().size());
        assertTrue(user.getPermissions().contains("system:user:list"));
    }

    @Test
    void loginUser_DefaultCredentialsNonExpired() {
        LoginUser user = new LoginUser();
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void loginUser_DefaultAccountNonExpired() {
        LoginUser user = new LoginUser();
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void loginUser_DefaultAccountNonLocked() {
        LoginUser user = new LoginUser();
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void loginUser_DefaultEnabled() {
        LoginUser user = new LoginUser();
        assertTrue(user.isEnabled());
    }

    @Test
    void loginUser_SetAndGetUser() {
        LoginUser user = new LoginUser();
        SysUser sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setUserName("testuser");
        user.setUser(sysUser);

        assertNotNull(user.getUser());
        assertEquals("testuser", user.getUser().getUserName());
    }

    @Test
    void loginUser_Authorities_ReturnsNull() {
        LoginUser user = new LoginUser();
        assertNull(user.getAuthorities());
    }
}
