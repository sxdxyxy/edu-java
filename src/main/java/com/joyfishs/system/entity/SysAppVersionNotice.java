package com.joyfishs.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * APP版本通知实体
 */
@Data
@Accessors(chain = true)
@TableName("sys_app_version_notice")
public class SysAppVersionNotice extends BaseEntity {

	@TableId(type = IdType.AUTO)
	Long id;

	/** APP类型 1安卓  2iOS */
	String appType;

	/** 通知的版本号 */
	String noticeVersion;

	/** 不可关闭  1是0否 */
	Integer notClose;

	/** 一直显示 1是  2否 */
	Integer alwaysShow;

	/** 通知内容 */
	String noticeContent;
}
