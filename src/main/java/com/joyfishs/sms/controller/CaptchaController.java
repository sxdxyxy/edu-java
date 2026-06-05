package com.joyfishs.sms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: yangkaifeng
 * @Date: 2023/05/24 14:48
 * @Description:
 */
@Slf4j
@RestController
@Api(tags = "验证码图片")
@RequestMapping("/sys")
public class CaptchaController {

//    /**
//     * 生成验证码
//     */
//    @GetMapping("/captchaImage")
//    public R<Map<String, Object>> getCode() {
//        Map<String, Object> ajax = new HashMap<>();
//        // 保存验证码信息
//        String uuid = IdUtil.simpleUUID();
//        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
//        // 生成验证码
//        CaptchaType captchaType = captchaProperties.getType();
//        boolean isMath = CaptchaType.MATH == captchaType;
//        Integer length = isMath ? captchaProperties.getNumberLength() : captchaProperties.getCharLength();
//        CodeGenerator codeGenerator = ReflectUtils.newInstance(captchaType.getClazz(), length);
//        AbstractCaptcha captcha = SpringUtils.getBean(captchaProperties.getCategory().getClazz());
//        captcha.setGenerator(codeGenerator);
//        captcha.createCode();
//        String code = captcha.getCode();
//        if (isMath) {
//            ExpressionParser parser = new SpelExpressionParser();
//            Expression exp = parser.parseExpression(StringUtils.remove(code, "="));
//            code = exp.getValue(String.class);
//        }
//        RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
//        ajax.put("uuid", uuid);
//        ajax.put("img", captcha.getImageBase64());
//        return R.ok(ajax);
//    }

}
