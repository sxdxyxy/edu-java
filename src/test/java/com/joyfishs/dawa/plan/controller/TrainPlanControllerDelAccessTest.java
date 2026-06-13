package com.joyfishs.dawa.plan.controller;

import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.plan.service.TrainPlanService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TrainPlanController D23-4: company_manager 删 TrainPlan 时越权 403
 *
 * 覆盖:
 *  - company_manager 删别公司 plan (project.orgId=999 vs user orgId=10) → 403
 *  - company_manager 删本公司 plan (project.orgId=10 == user orgId=10) → 200
 *  - company_manager 删 plan 但 plan 不存在 → 抛错, 不调 service.del
 *  - company_manager 删 plan 但 plan.projectIds 为空 → 抛错, 不调 service.del
 *  - admin 删除时不走越权检查 (Service 直接被调用)
 *
 * 注意: TrainPlan 实体没有 projectId 字段, 用 List<Long> projectIds; service.get(id)
 *       会从 mapper 加载 projectIds 列表. 越权检查要遍历所有 project.
 *
 * @author safe-edu
 * @since 2026-06-14
 */
@DisplayName("TrainPlanController D23-4 del 越权")
class TrainPlanControllerDelAccessTest {

    private TrainPlanController trainPlanController;
    private TrainPlanService trainPlanService;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        trainPlanService = mock(TrainPlanService.class);
        projectService = mock(ProjectService.class);
        // Controller 用 @Autowired 字段注入, 直接 new 出来, 反射塞 mock
        trainPlanController = new TrainPlanController();
        injectField(trainPlanController, "trainPlanService", trainPlanService);
        injectField(trainPlanController, "projectService", projectService);
    }

    private static void injectField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("injectField failed: " + fieldName, e);
        }
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

        Authentication auth = new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void loginAsAdmin() {
        SysUser admin = new SysUser();
        admin.setId(1L);
        admin.setUserName("admin");

        LoginUser loginUser = new LoginUser()
                .setUser(admin)
                .setRoleList(Collections.emptyList())
                .setCurrentRoleCode("admin");

        Authentication auth = new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("del — company_manager 删别公司 plan (project.orgId=999) 抛 403")
    void del_companyManager_crossCompany_throws403() {
        loginAsCompanyManager(10L);

        TrainPlan plan = new TrainPlan();
        plan.setId(1L);
        plan.setProjectIds(List.of(50L));
        when(trainPlanService.get(1L)).thenReturn(plan);

        Project otherCompany = new Project();
        otherCompany.setId(50L);
        otherCompany.setOrgId(999L);
        when(projectService.get(50L)).thenReturn(otherCompany);

        assertThatThrownBy(() -> trainPlanController.del(1L))
            .isInstanceOf(CustomException.class)
            .extracting("code").isEqualTo(403);

        verify(trainPlanService, never()).del(anyLong());
    }

    @Test
    @DisplayName("del — company_manager 删本公司 plan (project.orgId=10) 正常返回")
    void del_companyManager_ownCompany_succeeds() {
        loginAsCompanyManager(10L);

        TrainPlan plan = new TrainPlan();
        plan.setId(1L);
        plan.setProjectIds(List.of(50L));
        when(trainPlanService.get(1L)).thenReturn(plan);

        Project own = new Project();
        own.setId(50L);
        own.setOrgId(10L);
        when(projectService.get(50L)).thenReturn(own);

        doNothing().when(trainPlanService).del(1L);

        AjaxResult<?> result = trainPlanController.del(1L);

        assertThat(result.get(AjaxResult.CODE_TAG)).isEqualTo(200);
        verify(trainPlanService).del(1L);
    }

    @Test
    @DisplayName("del — company_manager 删 plan 但 plan 不存在 抛错")
    void del_companyManager_planNotFound_throws() {
        loginAsCompanyManager(10L);
        when(trainPlanService.get(1L)).thenReturn(null);

        assertThatThrownBy(() -> trainPlanController.del(1L))
            .isInstanceOf(CustomException.class);

        verify(projectService, never()).get(anyLong());
        verify(trainPlanService, never()).del(anyLong());
    }

    @Test
    @DisplayName("del — company_manager 删 plan 但 plan.projectIds 为空 抛错")
    void del_companyManager_planNoProjects_throws() {
        loginAsCompanyManager(10L);

        TrainPlan plan = new TrainPlan();
        plan.setId(1L);
        plan.setProjectIds(Collections.emptyList());
        when(trainPlanService.get(1L)).thenReturn(plan);

        assertThatThrownBy(() -> trainPlanController.del(1L))
            .isInstanceOf(CustomException.class);

        verify(projectService, never()).get(anyLong());
        verify(trainPlanService, never()).del(anyLong());
    }

    @Test
    @DisplayName("del — company_manager 删 plan 其中一个 project.orgId 不匹配 → 403")
    void del_companyManager_anyProjectCrossOrg_throws403() {
        loginAsCompanyManager(10L);

        TrainPlan plan = new TrainPlan();
        plan.setId(1L);
        plan.setProjectIds(List.of(50L, 51L));
        when(trainPlanService.get(1L)).thenReturn(plan);

        Project own = new Project();
        own.setId(50L);
        own.setOrgId(10L);
        Project other = new Project();
        other.setId(51L);
        other.setOrgId(999L);
        when(projectService.get(50L)).thenReturn(own);
        when(projectService.get(51L)).thenReturn(other);

        assertThatThrownBy(() -> trainPlanController.del(1L))
            .isInstanceOf(CustomException.class)
            .extracting("code").isEqualTo(403);

        verify(trainPlanService, never()).del(anyLong());
    }

    @Test
    @DisplayName("del — admin 删 plan 不走越权检查 (Service 直接被调用)")
    void del_admin_bypassesAccessCheck() {
        loginAsAdmin();

        doNothing().when(trainPlanService).del(1L);

        AjaxResult<?> result = trainPlanController.del(1L);

        assertThat(result.get(AjaxResult.CODE_TAG)).isEqualTo(200);
        verify(trainPlanService).del(1L);
        verify(trainPlanService, never()).get(any());
        verify(projectService, never()).get(any());
    }
}