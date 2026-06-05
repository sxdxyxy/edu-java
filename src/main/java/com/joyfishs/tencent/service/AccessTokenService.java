package com.joyfishs.tencent.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.system.config.redis.RedisCache;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccessTokenService {

    @Autowired
    private TencentParamService tencentParamService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 获取 Access Token
     * @return
     */
    public String getAccessToken(){
        log.info("AccessTokenService - getAccessToken");

        Object accessTokenRedisValue = redisCache.getCacheObject(tencentParamService.accessTokenRedisKey);
        log.info("AccessTokenService - getAccessToken accessTokenRedisValue:{}", accessTokenRedisValue);
        if(StringUtils.isNotNull(accessTokenRedisValue) && StringUtils.isNotEmpty(accessTokenRedisValue.toString())){
            return accessTokenRedisValue.toString();
        }

        String getAccessTokenUrl_ = String.format("%s?app_id=%s&secret=%s&grant_type=client_credential&version=1.0.0",
                tencentParamService.getAccessTokenUrl, tencentParamService.appId, tencentParamService.appSecret);
        log.info("AccessTokenService - getAccessToken getAccessTokenUrl_:{}", getAccessTokenUrl_);

        String body = HttpRequest.get(getAccessTokenUrl_).timeout(20000).execute().body();
        log.info("AccessTokenService - getAccessToken body:{}", body);

        JSONObject bodyJson = JSONObject.parseObject(body);
        if(StringUtils.isNotEmpty(bodyJson.getString("code")) && "0".equals(bodyJson.getString("code"))){
            String access_token = bodyJson.getString("access_token");
            log.info("AccessTokenService - getAccessToken access_token:{}", access_token);

            redisCache.setCacheObject(tencentParamService.accessTokenRedisKey, access_token, 20, TimeUnit.MINUTES);

            return access_token;
        }else {
            throw new CustomException("获取AccessToken失败");
        }
    }


}
