package com.joyfishs.tencent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NonceTicketService {

    @Autowired
    private TencentParamService tencentParamService;
    @Autowired
    private AccessTokenService accessTokenService;

    public String getNonceTicket(String userId){
        log.info("NonceTicketService - getNonceTicket");

        String getSignTicketUrl_ = String.format("%s?app_id=%s&access_token=%s&type=NONCE&version=1.0.0&user_id=%s",
                tencentParamService.getSignTicketUrl, tencentParamService.appId ,accessTokenService.getAccessToken(), userId);
        log.info("NonceTicketService - getNonceTicket getSignTicketUrl_:{}", getSignTicketUrl_);

        String body = HttpRequest.get(getSignTicketUrl_).timeout(20000).execute().body();
        log.info("NonceTicketService - getNonceTicket body:{}", body);

        JSONObject bodyJson = JSONObject.parseObject(body);
        if(StringUtils.isNotEmpty(bodyJson.getString("code")) && "0".equals(bodyJson.getString("code"))){
            JSONArray tickets = bodyJson.getJSONArray("tickets");
            String ticket = tickets.getJSONObject(0).getString("value");
            log.info("NonceTicketService - getNonceTicket ticket:{}", ticket);

            return ticket;
        }else {
            throw new CustomException("获取SignTicket失败");
        }
    }

}
