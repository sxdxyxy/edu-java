package com.joyfishs.dawa.safety.qrcode;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.joyfishs.oss.domain.UploadResult;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.config.CosConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * OSS 存储实现 (生产用, 走腾讯云 COS)
 * <p>
 * 复用 {@link SysOssService} + {@link CosConfig} 已有的上传能力.
 * COS 路径: {bucketName}/qrcode/{yyyyMMdd}/{objectId}.png
 * 域名: {cos.domain}/qrcode/{yyyyMMdd}/{objectId}.png
 * </p>
 *
 * <h3>启用方式</h3>
 * <pre>
 *   safety.qr.storage=oss   (application-prod.yml)
 * </pre>
 *
 * <h3>前置条件</h3>
 * COS_SECRET_ID / COS_SECRET_KEY 环境变量必须配, 否则 SysOssService.upload 会抛 CustomException.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "safety.qr.storage", havingValue = "oss")
public class OssQrCodeStorage implements QrCodeStorage {

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private CosConfig cosConfig;

    @Override
    public String storeAndGetUrl(String content, int width, int height, String bizTag) {
        try {
            // 用 COS 现成的 fileKey 生成规则: qrcode 根目录 / yyyyMMdd / objectId.png
            String fileKey = sysOssService.buildFileKey(CosConfig.QR_CODE, "png");

            // 临时写到本地 (Hutool 只能生成 BufferedImage/File, 不接受 OutputStream 上传)
            // 写完即上传, FileUtil.createTempFile 默认 /tmp, 容器内 OK
            File tempFile = FileUtil.createTempFile();
            QrConfig config = new QrConfig(width, height);
            byte[] pngBytes = QrCodeUtil.generatePng(content, config);
            FileUtil.writeBytes(pngBytes, tempFile);

            // 上传 COS, 拿 URL
            UploadResult result = sysOssService.upload(tempFile, fileKey);

            // 清理临时文件
            try {
                FileUtil.del(tempFile);
            } catch (Exception cleanupErr) {
                log.warn("Temp file cleanup failed: {}", tempFile.getAbsolutePath(), cleanupErr);
            }

            log.debug("OSS QR stored: {}", result.getUrl());
            return result.getUrl();
        } catch (Exception e) {
            log.warn("OssQrCodeStorage failed, content={}, bizTag={}", content, bizTag, e);
            return null;
        }
    }
}
