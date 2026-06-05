package com.joyfishs.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.entity.SysUserRole;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    @Delete("delete from sys_user_role where user_id = #{userId}")
    public Integer delByUserId(@Param("userId") Long userId);

    @Delete("delete from sys_user_role where role_id = #{roleId}")
    public Integer delByRoleId(@Param("roleId") Long roleId);

    @Delete("delete from sys_user_role where user_id = #{userId} and role_id = #{roleId}")
    public Integer delByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select("select user.* from sys_user user left join sys_user_role ur on user.id = ur.user_id where ur.role_id = #{roleId} and user.is_delete = 0")
    public List<SysUser> findUserListByRoleId(@Param("roleId") Long roleId);

    @Select("select role.* from sys_role role left join sys_user_role ur on role.id = ur.role_id where ur.user_id = #{userid} and role.is_delete = 0")
    public List<SysRole> findRoleListByUserId(@Param("userid") Long userid);

}
