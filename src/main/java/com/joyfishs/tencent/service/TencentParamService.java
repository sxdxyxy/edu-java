package com.joyfishs.tencent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TencentParamService {
    public final String accessTokenRedisKey = "sys:tencent:access:token";
    public final String SignTicketRedisKey = "sys:tencent:sign:ticket";

    @Value("${tencent.appId}")
    public String appId;
    @Value("${tencent.appSecret}")
    public String appSecret;
    @Value("${tencent.licence}")
    public String licence;

    @Value("${tencent.getAccessTokenUrl}")
    public String getAccessTokenUrl;
    @Value("${tencent.getSignTicketUrl}")
    public String getSignTicketUrl;
    @Value("${tencent.uploadFaceidUrl}")
    public String uploadFaceidUrl;

}
