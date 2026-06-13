package com.joyfishs.utils;

import com.joyfishs.system.entity.LoginUser;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.entity.vo.PersonVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityUtil.getManagedOrgIds company_manager 分支")
class SecurityUtilGetManagedOrgIdsTest {

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
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

        Authentication auth = new UsernamePasswordAuthenticationToken(
                loginUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("company_manager + orgId=10 → [10]")
    void companyManager_returnsOwnOrgId() {
        loginAsCompanyManager(10L);

        List<Long> result = SecurityUtil.getManagedOrgIds();

        assertThat(result).containsExactly(10L);
    }

    @Test
    @DisplayName("company_manager + orgId=null → []")
    void companyManager_nullOrgId_returnsEmpty() {
        loginAsCompanyManager(null);

        List<Long> result = SecurityUtil.getManagedOrgIds();

        assertThat(result).isEmpty();
    }
}