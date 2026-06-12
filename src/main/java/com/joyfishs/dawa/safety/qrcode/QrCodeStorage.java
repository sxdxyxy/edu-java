package com.joyfishs.dawa.safety.qrcode;

/**
 * 二维码存储抽象
 * <p>
 * 背景: 之前 base64 字符串直接塞进 safety_codes.qr_code_data / project_terminal_train,
 *       每行多 ~10KB 文本,扫表/备份/慢查询都受影响.
 * 改造: 把二维码字节流交给存储后端, 只把 URL 存到 DB (VARCHAR 512).
 * 后端: dev-sts 用 Local (写到 /tmp/qrcode/, 通过 Spring 静态目录暴露),
 *       prod 走 OSS (腾讯云 COS, 复用现有 SysOssService).
 * </p>
 *
 * <h3>实现选择</h3>
 * <ul>
 *   <li>{@link LocalQrCodeStorage} - @ConditionalOnProperty(safety.qr.storage=local, matchIfMissing=true)</li>
 *   <li>{@link OssQrCodeStorage}    - @ConditionalOnProperty(safety.qr.storage=oss)</li>
 * </ul>
 *
 * @author safe-edu
 * @since 2026-06-10
 */
public interface QrCodeStorage {

    /**
     * 把二维码字节流存到存储后端, 返回前端可直接 &lt;img src="..."&gt; 的 URL.
     *
     * @param content 二维码要编码的字符串 (如安全码值 UUID)
     * @param width   像素宽
     * @param height  像素高
     * @param bizTag  业务标签, 用于本地文件命名 / OSS key 前缀. 如 "safety-code" / "terminal-train"
     * @return 完整 URL (含协议 + 域名)
     */
    String storeAndGetUrl(String content, int width, int height, String bizTag);
}
