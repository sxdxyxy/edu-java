package com.joyfishs.system.config.weixin;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.joyfishs.system.enums.DeviceType;
import com.joyfishs.utils.StringUtils;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {

    private List<MpConfig> configs;

    @Data
    public static class MpConfig {
        /**
         * 使用平台
         */
        private String platform;
        /**
         * 设置微信公众号的appid
         */
        private String appId;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;
    }

    public MpConfig getByPlatform(DeviceType type) {
        for (MpConfig c : configs) {
            if (StringUtils.equals(type.name(), c.getPlatform())) {
                return c;
            }
        }
        return null;
    }
}
