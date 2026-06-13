package com.joyfishs.dawa.project.controller;

import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.system.entity.LoginUser;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.entity.vo.PersonVo;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.exception.CustomException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProjectController D23-2: company_manager 创建项目时 orgId 强制覆盖
 *
 * 覆盖:
 *  - company_manager 传入 orgId=999 → 被覆盖为 currentOrgId=10
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
    @DisplayName("get — company_manager 访问别公司项目 (orgId=999) 抛 403")
    void get_companyManager_crossCompany_throws403() {
        loginAsCompanyManager(10L);

        Project p = new Project();
        p.setId(1L);
        p.setOrgId(999L);
        when(projectService.get(1L)).thenReturn(p);

        assertThatThrownBy(() -> projectController.get(1L))
            .isInstanceOf(CustomException.class)
            .extracting("code").isEqualTo(403);
    }

    @Test
    @DisplayName("get — company_manager 访问本公司项目正常")
    void get_companyManager_ownCompany_succeeds() {
        loginAsCompanyManager(10L);

        Project p = new Project();
        p.setId(1L);
        p.setOrgId(10L);
        when(projectService.get(1L)).thenReturn(p);

        AjaxResult<?> result = projectController.get(1L);

        assertThat(result.get(AjaxResult.CODE_TAG)).isEqualTo(200);
    }

    @Test
    @DisplayName("del — company_manager 删别公司项目 → 403")
    void del_companyManager_crossCompany_throws403() {
        loginAsCompanyManager(10L);

        Project p = new Project();
        p.setId(1L);
        p.setOrgId(999L);
        when(projectService.get(1L)).thenReturn(p);

        assertThatThrownBy(() -> projectController.del("1", "test reason"))
            .isInstanceOf(CustomException.class)
            .extracting("code").isEqualTo(403);
    }

    @Test
    @DisplayName("release — company_manager 发布别公司项目 → 403")
    void release_companyManager_crossCompany_throws403() {
        loginAsCompanyManager(10L);

        Project p = new Project();
        p.setId(1L);
        p.setOrgId(999L);
        when(projectService.get(1L)).thenReturn(p);

        assertThatThrownBy(() -> projectController.release(1L))
            .isInstanceOf(CustomException.class)
            .extracting("code").isEqualTo(403);
    }

    @Test
    @DisplayName("stop — company_manager 中止别公司项目 → 403")
    void stop_companyManager_crossCompany_throws403() {
        loginAsCompanyManager(10L);

        Project p = new Project();
        p.setId(1L);
        p.setOrgId(999L);
        when(projectService.get(1L)).thenReturn(p);

        assertThatThrownBy(() -> projectController.stop(1L))
            .isInstanceOf(CustomException.class)
            .extracting("code").isEqualTo(403);
    }
}