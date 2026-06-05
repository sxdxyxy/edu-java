package com.joyfishs.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysAppVersionForce;

@Mapper
public interface SysAppVersionForceMapper extends BaseMapper<SysAppVersionForce> {

	/** 查询最近的一条强制升级版本 */
	@Select(" select force_version from sys_app_version_force where is_delete = 0 and app_type = #{appType} order by create_time desc limit 1 ")
	String getVersion(@Param("appType") Integer appType);
}
