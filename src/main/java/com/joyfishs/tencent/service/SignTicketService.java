package com.joyfishs.tencent.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.system.config.redis.RedisCache;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SignTicketService {

    @Autowired
    private TencentParamService tencentParamService;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private RedisCache redisCache;

    /**
     * 获取 SIGN ticket
     * @return
     */
    public String getSignTicket(){
        log.info("SignTicketService - getSignTicket");

        Object SignTicketRedisValue = redisCache.getCacheObject(tencentParamService.SignTicketRedisKey);
        log.info("SignTicketService - getSignTicket SignTicketRedisValue:{}", SignTicketRedisValue);
        if(StringUtils.isNotNull(SignTicketRedisValue) && StringUtils.isNotEmpty(SignTicketRedisValue.toString())){
            return SignTicketRedisValue.toString();
        }

        String getSignTicketUrl_ = String.format("%s?app_id=%s&access_token=%s&type=SIGN&version=1.0.0",
                tencentParamService.getSignTicketUrl, tencentParamService.appId ,accessTokenService.getAccessToken());
        log.info("SignTicketService - getSignTicket getSignTicketUrl_:{}", getSignTicketUrl_);

        String body = HttpRequest.get(getSignTicketUrl_).timeout(20000).execute().body();
        log.info("SignTicketService - getSignTicket body:{}", body);

        JSONObject bodyJson = JSONObject.parseObject(body);
        if(StringUtils.isNotEmpty(bodyJson.getString("code")) && "0".equals(bodyJson.getString("code"))){
            JSONArray tickets = bodyJson.getJSONArray("tickets");
            String ticket = tickets.getJSONObject(0).getString("value");
            log.info("SignTicketService - getSignTicket ticket:{}", ticket);

            redisCache.setCacheObject(tencentParamService.SignTicketRedisKey, ticket, 20, TimeUnit.MINUTES);

            return ticket;
        }else {
            throw new CustomException("获取SignTicket失败");
        }
    }
}
