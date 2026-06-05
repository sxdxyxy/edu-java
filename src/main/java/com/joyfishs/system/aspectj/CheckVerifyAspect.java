package com.joyfishs.system.aspectj;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.joyfishs.sms.service.SysSmsService;
import com.joyfishs.system.annotation.CheckVerify;
import com.joyfishs.system.enums.SmsType;
import com.joyfishs.utils.HttpServletUtil;
import com.joyfishs.utils.SpringUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@Aspect
@Component
public class CheckVerifyAspect {

    // 配置切入点
    @Pointcut("@annotation(com.joyfishs.system.annotation.CheckVerify)")
    public void CheckVerifyPointCut() { }

    @Before("CheckVerifyPointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        log.info("CheckVerifyAspect - doBefore");

        CheckVerify checkVerify = getAnnotation(point);

        String phone = "", type = "", codeValue = "";

        if (StringUtils.isNotEmpty(checkVerify.phone())) phone = HttpServletUtil.getRequest().getParameter(checkVerify.phone());
        else phone = HttpServletUtil.getRequest().getParameter("phone");
        log.info("CheckVerifyAspect - doBefore phone:{}", phone);
        if (StringUtils.isEmpty(phone)) throw new CustomException("未找到您所定义的手机号码表单字段！", 500);

        if (StringUtils.isNotEmpty(checkVerify.type())) type = HttpServletUtil.getRequest().getParameter(checkVerify.type());
        else type = HttpServletUtil.getRequest().getParameter("type");
        log.info("CheckVerifyAspect - doBefore type:{}", type);
        if (StringUtils.isEmpty(type)) throw new CustomException("未找到您所定义的类型表单字段！", 500);

        if (StringUtils.isNotEmpty(checkVerify.code())) codeValue = HttpServletUtil.getRequest().getParameter(checkVerify.code());
        else codeValue = HttpServletUtil.getRequest().getParameter("verifyCode");
        log.info("CheckVerifyAspect - doBefore code:{}", codeValue);
        if (StringUtils.isEmpty(codeValue)) throw new CustomException("未找到您所定义的验证码表单字段！", 500);


        boolean bool = SpringUtil.getBean(SysSmsService.class).verifyCaptcha(phone, codeValue, SmsType.getSmsType(type));
        log.info("CheckVerifyAspect - doBefore bool:{}", bool);

        if(!bool) throw new CustomException("您输入的验证码不正确，请核对后重新输入", 500);
    }

    private CheckVerify getAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) return method.getAnnotation(CheckVerify.class);
        return null;
    }

}
