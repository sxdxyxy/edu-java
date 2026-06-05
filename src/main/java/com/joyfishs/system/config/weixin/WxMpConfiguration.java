package com.joyfishs.system.config.weixin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

@Slf4j
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration {
    private static Map<String, WxMpService> mpServices = Maps.newHashMap();
    private final WxMpProperties properties;

    @Autowired
    public WxMpConfiguration(WxMpProperties properties) {
        this.properties = properties;
    }

    public static WxMpService getMpServices(String appId) {
        return mpServices.get(appId);
    }
    @PostConstruct
    public void initServices() {
        List<WxMpProperties.MpConfig> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new WxRuntimeException("添加下相关配置，注意别配错了！");
        }

        mpServices = configs.stream().map(a -> {
            WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
            configStorage.setAppId(a.getAppId());
            configStorage.setSecret(a.getSecret());
            configStorage.setToken(a.getToken());
            configStorage.setAesKey(a.getAesKey());

            WxMpService mpService = new WxMpServiceImpl();
            mpService.setWxMpConfigStorage(configStorage);
            log.info("配置的appId={}，平台={}", a.getAppId(), a.getPlatform());
            return mpService;
        }).collect(Collectors.toMap(s -> s.getWxMpConfigStorage().getAppId(), a -> a, (o, n) -> o));
    }
}
