package com.joyfishs.system.entity;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.experimental.Accessors;

/** 角色菜单 **/
@Data
@Accessors(chain = true)
public class SysRoleMenu {

    /** 主键ID **/
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色ID **/
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /** 菜单ID **/
    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

}
