package com.joyfishs.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysMenu;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("select max(int_code) from sys_menu where parent_id = #{parentId}")
    public Integer findMaxIntCodeByParentId(@Param("parentId") Long parentId);

}
