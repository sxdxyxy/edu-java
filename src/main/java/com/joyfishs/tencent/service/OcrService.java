package com.joyfishs.tencent.service;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 人脸识别
 * @author ykfnb
 */
@Slf4j
@Service
public class OcrService {

    @Value("${ocr.accessKeyId}")
    private String accessKeyId;
    @Value("${ocr.accessKeySecret}")
    private String accessKeySecret;
    private OcrClient client;
    private Map<String, Boolean> configMap = ImmutableMap.<String, Boolean>builder()
            .put("CropIdCard", true)
            .put("CopyWarn", true)
            .put("BorderCheckWarn", true)
            .put("ReshootWarn", true)
            .put("DetectPsWarn", true)
            .put("InvalidDateWarn", true)
            .put("Quality", true)
            .put("ReflectWarn", false)
            .build();
    @PostConstruct
    public void init() {
        Credential credential = new Credential(accessKeyId, accessKeySecret);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("ocr.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        this.client =   new OcrClient(credential, "ap-shanghai", clientProfile);
    }

    /**
     * 身份证识别
     * @param photoUrl 照片url
     * @param isBack 是正面还是反面
     */
    public IDCardOCRResponse idCardOCR(String photoUrl, boolean isBack) {
        try {
            IDCardOCRRequest req = new IDCardOCRRequest();
            req.setImageUrl(photoUrl);
            req.setCardSide(isBack ? "BACK" : "FRONT");
            req.setConfig(JSONUtil.toJsonStr(configMap));
            // 返回的resp是一个DetectFaceAttributesResponse的实例，与请求对象对应
            IDCardOCRResponse resp = client.IDCardOCR(req);
            //log.info("身份证识别响应：{}", IDCardOCRResponse.toJsonString(resp));
            return resp;
        } catch (TencentCloudSDKException e) {
            log.error("人脸检测出错：", e);
        }
        return null;
    }

}
