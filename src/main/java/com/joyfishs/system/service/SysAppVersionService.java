package com.joyfishs.system.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.config.redis.RedisCache;
import com.joyfishs.system.entity.SysAppVersion;
import com.joyfishs.system.mapper.SysAppVersionMapper;
import com.joyfishs.utils.Constants;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysAppVersionService extends ServiceImpl<SysAppVersionMapper, SysAppVersion> {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysAppVersionForceService sysAppVersionForceService;

    @Autowired
    private SysAppVersionNoticeService noticeService;

    /** 获取版本信息，强制升级，通知 */
    public Map<String, Object> getVersionCodeInfo(Integer appType,String currentVersion) {
        log.info("SysAppVersionService - getVersionCodeInfo - appType:{},appVersion:{}",appType,currentVersion);
        Map<String,Object> returnMap = new HashMap<>();
        Map<String, String> map = getVersionNumByAppType(appType);

        Boolean flag = false;
        if(!map.isEmpty()) flag = checkVersion(map.get("versionNum"),currentVersion); //判断是否大于当前版本
        returnMap.put("hasNewVersion",flag);
        if(flag) returnMap.put("newVersionInfo",map);

        Boolean ifForceUpdate = false;
        String forceVersion = sysAppVersionForceService.getForceVersion(appType); //查询强制升级版本号
        if(forceVersion != null && checkVersion(forceVersion,currentVersion)) ifForceUpdate = true;
        returnMap.put("ifForceUpdate",ifForceUpdate); //配置是否强制升级

        //查询通知版本
        Map<String,String> noticeMap = noticeService.getNoticeVersion(appType);
        if(noticeMap != null && !noticeMap.isEmpty() && checkVersion(noticeMap.get("noticeVersion"),currentVersion)){
            returnMap.put("hasNotice",true);
            returnMap.put("notice",noticeMap);
        }else {
            returnMap.put("hasNotice",false);
        }
        return returnMap;
    }

    /** 判断版本号大小 */
    private Boolean checkVersion(String version,String currentVersion){
        String[] sourceArr = version.split("\\.");
        String[] currentArr = currentVersion.split("\\.");
        boolean flag = false;
        for (int i = 0; i < sourceArr.length; i++) {
            if(Integer.parseInt(sourceArr[i]) > Integer.parseInt(currentArr[i])){
                flag = true;
                break;
            }
            if(Integer.parseInt(sourceArr[i]) < Integer.parseInt(currentArr[i])) return false;
        }
        return flag;  //返回true，大于当前版本，false，不大于当前版本
    }

    public Map<String, String> getVersionNumByAppType(Integer appType) {
        Map<String, String> redisMap = redisCache.getCacheMap(Constants.SYS_APP_VERSION_KEY + appType);
        if (!redisMap.isEmpty()) {
            return redisMap;
        }
        SysAppVersion appVersion = baseMapper.getNewVersion(appType);
        if (null != appVersion) {
            // 设置缓存
            Map<String, String> map = this.setAppVersionCache(appVersion);
            return map;
        } else {
            throw new CustomException("未查询到对应APP版本");
        }
    }

    public boolean executeSave(SysAppVersion t) {
        t.setCreateBy(SecurityUtil.getUserId());
        t.setCreateTime(new Date());
        boolean flag = save(t);
        if (flag) {
            // 设置缓存
            this.setAppVersionCache(t);
        }
        return flag;
    }

    public boolean executeUpdate(SysAppVersion t) {
        t.setUpdateTime(new Date());
        t.setUpdateBy(SecurityUtil.getUserId());
        boolean flag = updateById(t);
        if (flag) {
            // 设置缓存
            this.setAppVersionCache(t);
        }
        return flag;
    }

    private Map<String, String> setAppVersionCache(SysAppVersion t) {
        Map<String, String> map = new HashMap<>();
        map.put("versionNum", t.getVersionNum());
        map.put("description",t.getDescription());
        map.put("title",t.getTitle());
        map.put("url", t.getUrl());
        redisCache.setCacheMap(Constants.SYS_APP_VERSION_KEY + t.getAppType(), map);
        return map;
    }


    /** 清空缓存 */
    public void clearRedisCache(Integer appType) {
        redisCache.deleteObject(Constants.SYS_APP_VERSION_KEY + appType);
        redisCache.deleteObject(Constants.SYS_APP_VERSION_FORCE_KEY + appType);
        redisCache.deleteObject(Constants.SYS_APP_VERSION_NOTICE_KEY + appType);
    }
}
