package com.joyfishs.system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joyfishs.system.entity.LoginUser;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;

    @Mock
    private TokenService tokenService;

    private LoginUser loginUser;
    private Set<String> permissions;

    @BeforeEach
    void setUp() {
        permissions = new HashSet<>();
        loginUser = new LoginUser();
        loginUser.setPermissions(permissions);
    }

    @Test
    void hasPermi_WithNullPermission_ReturnsFalse() {
        assertFalse(permissionService.hasPermi(null));
    }

    @Test
    void hasPermi_WithEmptyPermission_ReturnsFalse() {
        assertFalse(permissionService.hasPermi(""));
    }

    @Test
    void hasPermi_WithBlankPermission_ReturnsFalse() {
        assertFalse(permissionService.hasPermi("   "));
    }

    @Test
    void hasAnyPermi_WithNullPermissions_ReturnsFalse() {
        assertFalse(permissionService.hasAnyPermi(null));
    }

    @Test
    void hasAnyPermi_WithEmptyPermissions_ReturnsFalse() {
        assertFalse(permissionService.hasAnyPermi(""));
    }

    @Test
    void hasAnyPermi_WithBlankPermissions_ReturnsFalse() {
        assertFalse(permissionService.hasAnyPermi("   "));
    }

    @Test
    void hasAnyPermi_WithMatchingPermission_ReturnsTrue() {
        permissions.add("system:user:list");
        permissions.add("system:user:add");

        loginUser.setPermissions(permissions);

        when(tokenService.getLoginUser(any())).thenReturn(loginUser);

        assertTrue(permissionService.hasAnyPermi("system:user:list,system:user:delete"));
    }

    @Test
    void hasAnyPermi_WithNoMatchingPermission_ReturnsFalse() {
        permissions.add("system:user:list");

        loginUser.setPermissions(permissions);

        when(tokenService.getLoginUser(any())).thenReturn(loginUser);

        assertFalse(permissionService.hasAnyPermi("system:role:list,system:role:add"));
    }

    @Test
    void hasAnyPermi_WithAllPermission_ReturnsTrue() {
        permissions.add(PermissionService.ALL_PERMISSION);

        loginUser.setPermissions(permissions);

        when(tokenService.getLoginUser(any())).thenReturn(loginUser);

        assertTrue(permissionService.hasAnyPermi("any:permission:here"));
    }

    @Test
    void hasAnyPermi_WithNullLoginUser_ReturnsFalse() {
        when(tokenService.getLoginUser(any())).thenReturn(null);

        assertFalse(permissionService.hasAnyPermi("system:user:list"));
    }

    @Test
    void hasAnyPermi_WithEmptyPermissionsSet_ReturnsFalse() {
        loginUser.setPermissions(new HashSet<>());

        when(tokenService.getLoginUser(any())).thenReturn(loginUser);

        assertFalse(permissionService.hasAnyPermi("system:user:list"));
    }
}
