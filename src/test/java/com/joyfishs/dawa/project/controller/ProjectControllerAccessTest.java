package com.joyfishs.dawa.project.controller;

import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.system.entity.LoginUser;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.entity.vo.PersonVo;
import com.joyfishs.utils.AjaxResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProjectController D23-2: company_manager 创建项目时 orgId 强制覆盖
 *
 * 覆盖:
 *  - company_manager 传入 orgId=999 → 被覆盖为 currentOrgId=10
 *  - admin 用户 orgId 不被覆盖
 *  - 普通用户 orgId 不被覆盖
 *
 * @author safe-edu
 * @since 2026-06-14
 */
@DisplayName("ProjectController D23-2 addOrUpdate orgId 强制")
class ProjectControllerAccessTest {

    private ProjectController projectController;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        projectService = mock(ProjectService.class);
        projectController = new ProjectController(projectService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void loginAsCompanyManager(Long orgId) {
        SysRole companyManager = new SysRole();
        companyManager.setId(SysRole.COMPANY_MANAGER_ROLE);
        companyManager.setCode("company_manager");
        companyManager.setName("公司管理员");

        SysUser user = new SysUser();
        user.setId(100L);
        user.setUserName("cm_test_user");

        PersonVo person = new PersonVo();
        person.setId(200L);
        person.setOrgId(orgId);
        person.setIsAdmin(0);

        LoginUser loginUser = new LoginUser()
                .setUser(user)
                .setRoleList(List.of(companyManager))
                .setCurrentRoleCode("company_manager")
                .setPerson(person);

        Authentication auth = new UsernamePasswordAuthenticationToken(loginUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void loginAsAdmin() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUserName("admin");

        PersonVo person = new PersonVo();
        person.setId(1L);
        person.setOrgId(null);
        person.setIsAdmin(0);

        LoginUser loginUser = new LoginUser()
                .setUser(user)
                .setPerson(person);

        Authentication auth = new UsernamePasswordAuthenticationToken(loginUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void loginAsOrdinaryUser(Long orgId) {
        SysUser user = new SysUser();
        user.setId(300L);
        user.setUserName("ordinary_user");

        PersonVo person = new PersonVo();
        person.setId(400L);
        person.setOrgId(orgId);
        person.setIsAdmin(0);

        LoginUser loginUser = new LoginUser()
                .setUser(user)
                .setPerson(person);

        Authentication auth = new UsernamePasswordAuthenticationToken(loginUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("addOrUpdate — company_manager 传入 orgId=999 被覆盖为 currentOrgId=10")
    void addOrUpdate_companyManager_orgIdOverridden() {
        loginAsCompanyManager(10L);

        Project input = new Project();
        input.setOrgId(999L);
        input.setProjectName("测试项目");

        when(projectService.saveOrUpdateProject(any(Project.class))).thenReturn(true);

        AjaxResult<?> result = projectController.add(input);

        assertThat(result.get(AjaxResult.CODE_TAG)).isEqualTo(200);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectService).saveOrUpdateProject(captor.capture());
        assertThat(captor.getValue().getOrgId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("addOrUpdate — admin 用户 orgId 不被覆盖")
    void addOrUpdate_admin_orgIdPreserved() {
        loginAsAdmin();

        Project input = new Project();
        input.setOrgId(999L);
        input.setProjectName("admin项目");

        when(projectService.saveOrUpdateProject(any(Project.class))).thenReturn(true);

        AjaxResult<?> result = projectController.add(input);

        assertThat(result.get(AjaxResult.CODE_TAG)).isEqualTo(200);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectService).saveOrUpdateProject(captor.capture());
        assertThat(captor.getValue().getOrgId()).isEqualTo(999L);
    }

    @Test
    @DisplayName("addOrUpdate — 普通用户 orgId 不被覆盖")
    void addOrUpdate_ordinaryUser_orgIdPreserved() {
        loginAsOrdinaryUser(20L);

        Project input = new Project();
        input.setOrgId(999L);
        input.setProjectName("普通项目");

        when(projectService.saveOrUpdateProject(any(Project.class))).thenReturn(true);

        AjaxResult<?> result = projectController.add(input);

        assertThat(result.get(AjaxResult.CODE_TAG)).isEqualTo(200);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectService).saveOrUpdateProject(captor.capture());
        assertThat(captor.getValue().getOrgId()).isEqualTo(999L);
    }
}