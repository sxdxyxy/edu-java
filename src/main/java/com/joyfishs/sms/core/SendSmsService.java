package com.joyfishs.sms.core;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.joyfishs.sms.domain.SmsResult;
import com.joyfishs.utils.exception.SmsException;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;

import cn.hutool.core.lang.Validator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@Service
public class SendSmsService {

    @Value("${sms.accessKeyId}")
    private String accessKeyId;
    @Value("${sms.accessKeySecret}")
    private String accessKeySecret;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.sdkAppId}")
    private String sdkAppId;
    private SmsClient client;

    @PostConstruct
    public void init() {
        Credential credential = new Credential(accessKeyId, accessKeySecret);
        this.client = new SmsClient(credential, "");
    }

    public SmsResult sendSmsVerify(String phone, String templateId, String[] param) {
        log.info("SendSmsService - sendSmsVerify phone:{},param:{}", phone, param);
        // 验证手机号码是否正确
        if (!Validator.isMobile(phone)) {
            throw new SmsException("您的手机号码不对！");
        }
        SendSmsRequest req = new SendSmsRequest();
        String[] phoneNumberSet = {"+86" + phone};
        req.setPhoneNumberSet(phoneNumberSet);
        req.setTemplateParamSet(param);
        req.setTemplateID(templateId);
        req.setSign(signName);
        req.setSmsSdkAppid(sdkAppId);

        try {
            SendSmsResponse resp = client.SendSms(req);
            SmsResult.SmsResultBuilder builder = SmsResult.builder()
                    .isSuccess(true)
                    .message("send success")
                    .response(JSONObject.toJSONString(resp));
            for (SendStatus sendStatus : resp.getSendStatusSet()) {
                if (!"Ok".equals(sendStatus.getCode())) {
                    builder.isSuccess(false).message(sendStatus.getMessage());
                    break;
                }
            }
            return builder.build();
        } catch (Exception e) {
            throw new SmsException(e.getMessage());
        }
    }

}
