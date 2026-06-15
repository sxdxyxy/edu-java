package com.joyfishs.system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joyfishs.system.domain.LoginRes;
import com.joyfishs.system.entity.UsernamePasswordLoginBody;
import com.joyfishs.system.enums.RoleType;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysLoginService.autoLogin 自动识别登录")
class SysLoginServiceAutoLoginTest {

    @InjectMocks
    private SysLoginService sysLoginService;

    private SysLoginService spyService;

    @BeforeEach
    void setUp() {
        spyService = spy(sysLoginService);
    }

    private UsernamePasswordLoginBody body() {
        UsernamePasswordLoginBody body = new UsernamePasswordLoginBody();
        body.setUsername("alice");
        body.setPassword("pwd");
        return body;
    }

    private LoginRes success() {
        LoginRes res = new LoginRes();
        res.setToken("tk");
        return res;
    }

    private LoginRes failNoRole() {
        LoginRes res = new LoginRes();
        res.setError("登录用户没有该角色");
        return res;
    }

    private LoginRes failAuth() {
        LoginRes res = new LoginRes();
        res.setError("用户名密码不匹配", 401);
        return res;
    }

    @Test
    @DisplayName("管理员身份登录成功:不再尝试学员身份")
    void autoLogin_managerRoleSucceeds_returnsManagerToken() {
        doReturn(success()).when(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());

        LoginRes result = spyService.autoLogin(body(), true);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getToken()).isEqualTo("tk");
        assertThat(result.getRoleCode()).isEqualTo(RoleType.MANAGER.getValue());
        verify(spyService, times(1)).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());
        verify(spyService, never()).tryLoginWithRole(any(), eq(RoleType.STUDENT), anyBoolean());
    }

    @Test
    @DisplayName("纯学员账号:管理员身份无该角色,回退到学员身份")
    void autoLogin_onlyStudentRole_fallsBackToStudent() {
        doReturn(failNoRole()).when(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());
        doReturn(success()).when(spyService).tryLoginWithRole(any(), eq(RoleType.STUDENT), anyBoolean());

        LoginRes result = spyService.autoLogin(body(), true);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getToken()).isEqualTo("tk");
        assertThat(result.getRoleCode()).isEqualTo(RoleType.STUDENT.getValue());
        verify(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());
        verify(spyService).tryLoginWithRole(any(), eq(RoleType.STUDENT), anyBoolean());
    }

    @Test
    @DisplayName("双重身份账号:管理员优先")
    void autoLogin_userHasBothRoles_prefersManager() {
        doReturn(success()).when(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());

        LoginRes result = spyService.autoLogin(body(), true);

        assertThat(result.getRoleCode()).isEqualTo(RoleType.MANAGER.getValue());
        verify(spyService, never()).tryLoginWithRole(any(), eq(RoleType.STUDENT), anyBoolean());
    }

    @Test
    @DisplayName("密码错(认证失败):直接返回失败,不再尝试学员身份")
    void autoLogin_wrongPassword_doesNotFallback() {
        doReturn(failAuth()).when(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());

        LoginRes result = spyService.autoLogin(body(), true);

        assertThat(result.getCode()).isEqualTo(401);
        assertThat(result.getRoleCode()).isNull();
        verify(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());
        verify(spyService, never()).tryLoginWithRole(any(), eq(RoleType.STUDENT), anyBoolean());
    }

    @Test
    @DisplayName("账号无任何角色:管理员失败-学员也失败,返回学员的错误")
    void autoLogin_noRoleAtAll_returnsStudentError() {
        doReturn(failNoRole()).when(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());
        doReturn(failNoRole()).when(spyService).tryLoginWithRole(any(), eq(RoleType.STUDENT), anyBoolean());

        LoginRes result = spyService.autoLogin(body(), true);

        assertThat(result.getCode()).isEqualTo(403);
        assertThat(result.getRoleCode()).isNull();
        verify(spyService).tryLoginWithRole(any(), eq(RoleType.MANAGER), anyBoolean());
        verify(spyService).tryLoginWithRole(any(), eq(RoleType.STUDENT), anyBoolean());
    }
}