package com.joyfishs.dawa.course.service;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import cn.hutool.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@Service
public class VodService {

    @Value("${vod.secretId}")
    private String secretId;
    @Value("${vod.secretKey}")
    private String secretKey;
    @Value("${vod.playKey}")
    private String playKey;
    @Value("${vod.appId}")
    private String appId;
    @Value("${vod.subAppId}")
    private String subAppId;

    private static final String HMAC_ALGORITHM = "HmacSHA1"; //签名算法
    private static final String CONTENT_CHARSET = "UTF-8";

    public String getSubAppId() {
        return subAppId;
    }

    public static byte[] byteMerger(byte[] byte1, byte[] byte2) {
        byte[] byte3 = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
    }
    public void test() throws Exception {
        String signature = getPlaySignature("3270835009917841811");
        System.out.println("signature : " + signature);
    }

    // 获取签名
    public String getUploadSignature(Long coursewareId) throws Exception {
        String strSign = "";
        String contextStr = "";
        long currentTime = System.currentTimeMillis();
        int random = new Random().nextInt(java.lang.Integer.MAX_VALUE);
        int signValidDuration = 3600 * 24; // 签名有效期：1天
        // 生成原始参数字符串
        long endTime = (currentTime + signValidDuration);
        contextStr += "secretId=" + java.net.URLEncoder.encode(secretId, "utf8");
        contextStr += "&currentTimeStamp=" + currentTime;
        contextStr += "&expireTime=" + endTime;
        contextStr += "&random=" + random;
        contextStr += "&vodSubAppId=" + subAppId;
        // 视频后续任务
        contextStr += "&procedure=basicVideoPreset";
        // 带上课件id，处理上传完成回调时能对应上
        contextStr += "&sourceContext=" + coursewareId;
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(this.secretKey.getBytes(CONTENT_CHARSET), mac.getAlgorithm());
            mac.init(secretKey);

            byte[] hash = mac.doFinal(contextStr.getBytes(CONTENT_CHARSET));
            byte[] sigBuf = byteMerger(hash, contextStr.getBytes("utf8"));
            strSign = base64Encode(sigBuf);
            strSign = strSign.replace(" ", "").replace("\n", "").replace("\r", "");
        } catch (Exception e) {
            throw e;
        }
        return strSign;
    }

    private String base64Encode(byte[] buffer) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(buffer);
    }

    public String getPlaySignature(String fileId) {
        String AudioVideoType = "RawAdaptive";
        Integer RawAdaptiveDefinition = 10;
        Integer ImageSpriteDefinition = 10;
        Long currentTime = Instant.now().getEpochSecond();
        Long psignExpire = currentTime + 7200;
        String urlTimeExpire = HexUtil.toHex(psignExpire);
        HashMap<String, Object> urlAccessInfo = new HashMap<String, Object>();
        urlAccessInfo.put("t", urlTimeExpire);
        HashMap<String, Object> contentInfo = new HashMap<String, Object>();
        contentInfo.put("audioVideoType", AudioVideoType);
        contentInfo.put("rawAdaptiveDefinition", RawAdaptiveDefinition);
        contentInfo.put("imageSpriteDefinition", ImageSpriteDefinition);

        try {
            Algorithm algorithm = Algorithm.HMAC256(playKey);
            String token = JWT.create().withClaim("appId", subAppId).withClaim("fileId", fileId)
                    .withClaim("contentInfo", contentInfo)
                    .withClaim("currentTimeStamp", currentTime).withClaim("expireTimeStamp", psignExpire)
                    .withClaim("urlAccessInfo", urlAccessInfo).sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            // Invalid Signing configuration / Couldn't convert Claims.
        }
        return null;
    }
}
