package com.joyfishs.system.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.config.redis.RedisCache;
import com.joyfishs.system.entity.SysAppVersionNotice;
import com.joyfishs.system.mapper.SysAppVersionNoticeMapper;
import com.joyfishs.utils.Constants;

/**
 * 版本通知service
 */
@Service
public class SysAppVersionNoticeService extends ServiceImpl<SysAppVersionNoticeMapper, SysAppVersionNotice> {

	@Autowired
	RedisCache redisCache;

	/** 查询通知 */
	public Map<String, String> getNoticeVersion(Integer appType) {
		Map<String, String> cacheMap = redisCache.getCacheMap(Constants.SYS_APP_VERSION_NOTICE_KEY + appType);
		if(cacheMap == null || cacheMap.isEmpty()){
			SysAppVersionNotice notice = baseMapper.getNoticeByAppType(appType);
			if(notice != null){
				Map<String, String> map = this.setAppVersionNotice(notice);
				return map;
			}
		}
		return cacheMap;
	}

	private Map<String, String> setAppVersionNotice(SysAppVersionNotice t) {
		Map<String, String> map = new HashMap<>();
		map.put("noticeVersion", t.getNoticeVersion());
		map.put("notClose", t.getNotClose().toString());
		map.put("alwaysShow", t.getAlwaysShow().toString());
		map.put("noticeContent", t.getNoticeContent());
		redisCache.setCacheMap(Constants.SYS_APP_VERSION_NOTICE_KEY + t.getAppType(), map);
		return map;
	}
}
