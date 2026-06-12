package com.joyfishs.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * D6: 把 dev-sts 本地二维码目录暴露为静态资源.
 * <p>
 * 背景: LocalQrCodeStorage 写到 ${safety.qr.local.base-path}/, 默认 /tmp/qrcode/.
 *       但 Spring Boot 默认静态资源是 classpath:/static/, 不会自动 serve 容器内任意目录.
 * 配置:
 *   safety.qr.local.base-path = /tmp/qrcode  (默认)
 *   暴露路径                  = /qrcode/**  →  映射到 file:/tmp/qrcode/
 * <br>
 * 注意: 此配置只对 dev-sts (Local 模式) 有意义; 生产用 OSS, 直接走 CDN 域名.
 *       但写成 @Configuration 总是生效也无所谓 (空目录也不会 500).
 * </p>
 */
@Configuration
public class QrCodeResourceConfig implements WebMvcConfigurer {

    @Value("${safety.qr.local.base-path:/tmp/qrcode}")
    private String qrBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 把容器内 /tmp/qrcode/ 暴露在 URL /qrcode/** 下
        // Spring Resource 会把 file:/tmp/qrcode/xxx.png 映射到 /qrcode/xxx.png
        registry.addResourceHandler("/qrcode/**")
                .addResourceLocations("file:" + qrBasePath + "/")
                .setCachePeriod(3600);  // 1 小时缓存 (QR 内容不会变)
    }
}
