package com.joyfishs.dawa.safety.qrcode;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 本地文件系统二维码存储 (dev-sts 用)
 * <p>
 * 路径规则: ${safety.qr.local.base-path}/{bizTag}/{yyyyMMdd}/{objectId}.png
 * URL 形式: {url-prefix}/{bizTag}/{yyyyMMdd}/{objectId}.png
 * <br>
 * 默认: /tmp/qrcode/safety-code/20260610/abc123.png
 * </p>
 *
 * <h3>dev-sts 静态目录暴露</h3>
 * 容器内 /tmp/qrcode 需挂 volume (待 docker-compose 配).
 * 或用 spring.web.resources.static-locations 暴露 (见 application-dev.yml).
 * <br>
 * 当前 dev-sts 用 host 8091,url-prefix 配 http://127.0.0.1:8091/qrcode.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "safety.qr.storage", havingValue = "local", matchIfMissing = true)
public class LocalQrCodeStorage implements QrCodeStorage {

    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Value("${safety.qr.local.base-path:/tmp/qrcode}")
    private String basePath;

    @Value("${safety.qr.local.url-prefix:http://127.0.0.1:8091/qrcode}")
    private String urlPrefix;

    @Override
    public String storeAndGetUrl(String content, int width, int height, String bizTag) {
        try {
            String dateDir = LocalDate.now().format(DATE_DIR);
            String fileName = IdUtil.objectId() + ".png";
            // 用 bizTag 做一层子目录隔离, 避免不同业务的二维码混在一起
            File target = new File(basePath, bizTag + "/" + dateDir + "/" + fileName);
            FileUtil.mkParentDirs(target);

            // 调 Hutool 生成 PNG 字节流, 直接写本地文件
            QrConfig config = new QrConfig(width, height);
            byte[] pngBytes = QrCodeUtil.generatePng(content, config);
            FileUtil.writeBytes(pngBytes, target);

            // 拼 URL: 移除 basePath 末尾 / 与 url-prefix 末尾 / 的双重分隔问题
            String url = urlPrefix.endsWith("/") ? urlPrefix.substring(0, urlPrefix.length() - 1) : urlPrefix;
            url = url + "/" + bizTag + "/" + dateDir + "/" + fileName;
            log.debug("Local QR stored: {}", url);
            return url;
        } catch (Exception e) {
            // 不要让二维码失败阻塞主流程 (之前 SafetyCodeService 也是 try-catch warn)
            log.warn("LocalQrCodeStorage failed, content={}, bizTag={}", content, bizTag, e);
            return null;
        }
    }
}
