package com.joyfishs.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * APP版本强制升级配置表
 */
@Data
@Accessors(chain = true)
@TableName("sys_app_version_force")
public class SysAppVersionForce extends BaseEntity {
	@TableId(type = IdType.AUTO)
	Long id;

	/** 1:安卓 2:ios */
	String appType;

	/** 强制升级的版本 */
	String forceVersion;
}
