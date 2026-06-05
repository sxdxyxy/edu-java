package com.joyfishs.system.params;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 系统角色分配菜单参数
 */
@Data
public class SysRoleGrantMenuParam {

    /**
     * 主键
     */
    @NotNull(message = "id不能为空，请检查id参数")
    private Long id;

    /**
     * 授权菜单
     */
    @NotNull(message = "授权菜单不能为空，请检查grantMenuIdList参数")
    private List<Long> grantMenuIdList;

}
