package com.joyfishs.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.config.redis.RedisCache;
import com.joyfishs.system.entity.SysAppVersionForce;
import com.joyfishs.system.mapper.SysAppVersionForceMapper;
import com.joyfishs.utils.Constants;

/**
 * @program: ruoyi
 * @description: 强制升级版本服务
 * @author: Yjhon
 * @create: 2021-12-02 11:52
 */
@Service
public class SysAppVersionForceService extends ServiceImpl<SysAppVersionForceMapper, SysAppVersionForce> {

	@Autowired
	RedisCache redisCache;

	/** 查询强制升级版本号 */
	public String getForceVersion(Integer appType) {
		String cacheVersion = redisCache.getCacheObject(Constants.SYS_APP_VERSION_FORCE_KEY + appType);
		if(cacheVersion == null){
			String version = baseMapper.getVersion(appType);
			if(version != null){
				redisCache.setCacheObject(Constants.SYS_APP_VERSION_FORCE_KEY + appType,version);
			}
			return version;
		}
		return cacheVersion;
	}


}
