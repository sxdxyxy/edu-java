package com.joyfishs.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysAppVersionNotice;

@Mapper
public interface SysAppVersionNoticeMapper extends BaseMapper<SysAppVersionNotice> {

	/** 查询最后一条通知 */
	@Select(" select id, app_type, notice_version, not_close, " +
			" always_show, notice_content from sys_app_version_notice " +
			" where is_delete = 0 and app_type = #{appType} " +
			" order by create_time desc limit 1 ")
	SysAppVersionNotice getNoticeByAppType(@Param("appType") Integer appType);
}
