package com.joyfishs.sms.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.sms.core.SendSmsService;
import com.joyfishs.sms.domain.SmsResult;
import com.joyfishs.sms.domain.SysSms;
import com.joyfishs.sms.mapper.SysSmsMapper;
import com.joyfishs.system.config.security.captcha.CaptchaService;
import com.joyfishs.system.enums.SmsType;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysSmsService extends ServiceImpl<SysSmsMapper, SysSms> implements CaptchaService {

    @Autowired
    private SendSmsService sendSmsService;
    @Autowired
    private PersonService personService;

    /**
     * 获取验证码
     *
     * @return
     */
    public SmsResult saveSysSms(String phone, SmsType smsType) {
        if (StringUtils.isEmpty(phone)) {
            if (SecurityUtil.isLogin()) {
                phone = personService.getByUserId(SecurityUtil.getUserId()).getPhone();
            } else {
                throw new CustomException("手机号码不能为空");
            }
        }

        // 检查1分钟内是否获取过验证码
        String createTime = DateUtil.format(DateUtil.offsetMinute(new Date(), -1), DatePattern.NORM_DATETIME_PATTERN);
        if (baseMapper.findByPhone(phone, smsType.getValue(), createTime) > 0) {
            throw new CustomException("您的请求过于频繁，请间隔1分钟后重新获取");
        }
        //如果是发送登录验证码，手机号必须存在
        if (SmsType.LOGIN.equals(smsType) && ObjectUtil.isNull(personService.findByPhone(phone))) {
            throw new CustomException("此手机号未注册");
        }

        // 获取验证码
        String verifyCode = RandomUtil.randomString(RandomUtil.BASE_NUMBER, 6);

        // 发送短信
        String[] templateParamSet = {verifyCode, "5"};
        if (SmsType.CHANGE_MOBILE.equals(smsType)) {//此模板只有一个参数
            templateParamSet = new String[]{verifyCode};
        }
        SmsResult result = sendSmsService.sendSmsVerify(phone, smsType.getTemplateId(), templateParamSet);

        // 保存历史记录
        SysSms sysSms = new SysSms()
                .setUserId(SecurityUtil.isLogin() ? SecurityUtil.getUserId() : null)
                .setPhone(phone)
                .setType(smsType.getValue())
                .setCode(verifyCode)
                .setSmsContent(null)
                .setIsPush(1);
        save(sysSms);
        return result;
    }

    /**
     * 检查验证码
     */
    @Override
    public boolean verifyCaptcha(String phone, String rawCode, SmsType type) {
        String createTime = DateUtil.format(DateUtil.offsetMinute(new Date(), -5), DatePattern.NORM_DATETIME_PATTERN);
        SysSms sysSms = baseMapper.findByUserIdAndCode(phone, type.getValue(), rawCode, createTime);
        if (sysSms != null && StringUtils.isNotNull(sysSms.getId())) {
            sysSms.setIsUse(1);
            updateById(sysSms);
            return true;
        }
        return false;
    }

    public List<SysSms> queryPageList(String phone) {
        LambdaQueryWrapper<SysSms> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(SysSms::getCreateTime);
        if (StringUtils.isNotEmpty(phone)) {
            lqw.eq(SysSms::getPhone, phone);
        }
        List<SysSms> result = baseMapper.selectList(lqw);
        return result;
    }

    public List<SysSms> queryPageList() {
        return queryPageList(null);
    }
}
