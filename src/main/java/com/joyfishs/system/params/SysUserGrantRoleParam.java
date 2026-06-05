package com.joyfishs.system.params;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 用户分配角色参数
 */
@Data
public class SysUserGrantRoleParam {

    /**
     * 主键
     */
    @NotNull(message = "id不能为空，请检查id参数")
    private Long id;

    /**
     * 授权角色
     */
    @NotNull(message = "授权角色不能为空，请检查grantRoleIdList参数")
    private List<Long> grantRoleIdList;

}
