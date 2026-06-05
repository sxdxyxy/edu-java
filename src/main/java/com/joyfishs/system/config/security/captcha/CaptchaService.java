package com.joyfishs.system.config.security.captcha;

import com.joyfishs.system.enums.SmsType;

public interface CaptchaService {
    /**
     * verify captcha
     *
     * @param phone   电话号码
     * @param rawCode 验证码
     * @param type    验证类型
     * @return isVerified
     */
    boolean verifyCaptcha(String phone, String rawCode, SmsType type);
}
