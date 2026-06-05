package com.joyfishs.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysMenu;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysRoleMenu;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    @Delete("delete from sys_role_menu where role_id = #{roleId}")
    public Integer delByRoleId(@Param("roleId") Long roleId);

//    @Delete("delete from sys_role_menu where menu_id = #{menuId}")
//    public Integer delByMenuId(@Param("menuId") Long menuId);

    @Delete("delete from sys_role_menu where role_id = #{roleId} and menu_id = #{menuId}")
    public Integer delByRoleIdAndMenuId(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    @Select("select menu.* from sys_menu menu left join sys_role_menu rm on menu.id = rm.menu_id where rm.role_id = #{roleId} and menu.is_delete = 0 order by menu.int_code")
    public List<SysMenu> findMenuListByRoleId(@Param("roleId") Long roleId);

    @Select("select role.* from sys_role role left join sys_role_menu rm on role.id = rm.role_id where rm.menu_id = #{menuId} and role.is_delete = 0")
    public List<SysRole> findRoleListByMenuId(@Param("menuId") Long menuId);
}
