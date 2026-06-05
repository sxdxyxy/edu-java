package com.joyfishs.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;

import lombok.Data;

/**
 * 腾讯云上传参数
 */
@Component
@ConfigurationProperties(prefix = "cos")
@Data
public class CosConfig {
    /**
     * 通用目录
     */
    public static final String COMMON = "common";
    /**
     * 签名后文件
     */
    public static final String SignedDocument = "signedDoc";
    /**
     * 签名后文件
     */
    public static final String ARCHIVES_ZIP = "archives";
    /**
     * 临时文件
     */
    public static final String TEMPORARY = "temporary";
    /**
     * 小程序码
     */
    public static final String WXA_CODE = "wxacode";
    /**
     * 个人小程序二维码
     */
    public static final String QR_CODE = "qrcode";
    /**
     * PDF文件
     */
    public static final String PDF = "pdf";
    /**
     * ZIP文件
     */
    public static final String ZIP = "zip";
    @Value("${cos.secretId}")
    private String secretId = "腾讯云控制台项目配置secretId";
    @Value("${cos.secretKey}")
    private String secretKey = "腾讯云控制台项目配置secretKey";
    @Value("${cos.region}")
    private String region = "存储桶地域";
    @Value("${cos.bucketName}")
    private String bucketName = "存储桶名称";
    @Value("${cos.domain}")
    private String domain = "CDN加速域名";
    private Long expiration = 60L;
    private COSClient client;

    @Bean
    public COSClient cosClient() {
        COSCredentials cosCredentials = new BasicCOSCredentials(this.secretId, this.secretKey);
        Region region = new Region(this.region);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        client = new COSClient(cosCredentials, clientConfig);
        return client;
    }
}
