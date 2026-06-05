package com.joyfishs.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("select * from sys_user where user_name = #{userName} and is_delete = 0")
    public SysUser findByUserName(@Param("userName") String userName);

    @Select("select * from sys_user where (user_name = #{userName} or phone = #{userName}) and is_delete = 0 order by create_time desc")
    public SysUser findByUserNameOrPhone(@Param("userName") String userName);

    @Select("select * from sys_user where  phone = #{phone} order by create_time desc limit 1")
    public SysUser findByPhone(@Param("phone") String phone);
}
