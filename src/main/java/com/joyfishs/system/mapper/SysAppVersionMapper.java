package com.joyfishs.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysAppVersion;

@Mapper
public interface SysAppVersionMapper extends BaseMapper<SysAppVersion> {

	@Select("select id, title, description, version_num, app_type, url " +
			"from sys_app_version " +
			"where is_delete = 0 and app_type = #{appType} " +
			"order by create_time desc limit 1 ")
	SysAppVersion getNewVersion(@Param("appType") Integer appType);

}
