package com.joyfishs.sms.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.joyfishs.sms.domain.SmsResult;
import com.joyfishs.sms.domain.SysSms;
import com.joyfishs.sms.service.SysSmsService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.enums.SmsType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @author ykfnb
 */
@Slf4j
@RestController
@Api(tags = "发送验证码短信")
@RequestMapping("/sys/sms")
public class SysSmsController extends BaseController {

    @Autowired
    private SysSmsService sysSmsService;

    @ApiIgnore
    @GetMapping("/getCode")
    public AjaxResult<?> getCode(
            @RequestParam(value = "phone", required = false, defaultValue = "") String phone,
            @RequestParam(value = "type", required = false, defaultValue = "") String type) {
        SmsResult res = sysSmsService.saveSysSms(phone, SmsType.getSmsType(type));
        return toAjax(res.isSuccess());
    }

    @GetMapping("/loginCaptcha")
    @ApiOperation(value = "发送登录短信验证码")
    public R<Void> loginCaptcha(@ApiParam(value = "接收手机号", required = true) @RequestParam(required = true) @NotBlank(message = "手机号不能为空") String phone) {
        SmsResult result = sysSmsService.saveSysSms(phone, SmsType.LOGIN);
        if (!result.isSuccess()) {
            log.error("登录验证码短信发送异常 => {}", result);
            return R.fail(result.getMessage());
        }
        return R.ok();
    }

    @GetMapping("/registerCaptcha")
    @ApiOperation(value = "发送注册短信验证码")
    public R<Void> registerCaptcha(@ApiParam(value = "接收手机号", required = true) @RequestParam(required = true) @NotBlank(message = "手机号不能为空") String phone) {
        SmsResult result = sysSmsService.saveSysSms(phone, SmsType.REGISTER);
        if (!result.isSuccess()) {
            log.error("注册验证码短信发送异常 => {}", result);
            return R.fail(result.getMessage());
        }
        return R.ok();
    }

    @GetMapping("/changePhoneCaptcha")
    @ApiOperation(value = "发送修改手机号时的验证码")
    public R<Void> changePhoneCaptcha(@ApiParam(value = "接收手机号", required = true) @RequestParam(required = true) @NotBlank(message = "手机号不能为空") String phone) {
        SmsResult result = sysSmsService.saveSysSms(phone, SmsType.CHANGE_MOBILE);
        if (!result.isSuccess()) {
            log.error("修改手机号验证码短信发送异常 => {}", result);
            return R.fail(result.getMessage());
        }
        return R.ok();
    }

    @ApiOperation(value = "短信验证码列表查询")
    @GetMapping("/list")
    public TableDataInfo<SysSms> list(@RequestParam(value = "phone", required = false) String phone) {
        startPage();
        List<SysSms> result = sysSmsService.queryPageList(phone);
        return getDataTable(result);
    }

    @ApiOperation(value = "注册短信验证码新增")
    @Log(title = "手动添加注册短信验证码", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@RequestBody SysSms sysSms) {
        String verifyCode = RandomUtil.randomString(RandomUtil.BASE_NUMBER, 6);
        SysSms sms = new SysSms()
                .setUserId(null)
                .setPhone(sysSms.getPhone())
                .setType(SmsType.REGISTER.getValue())
                .setCode(verifyCode)
                .setSmsContent(null)
                .setCreateTime(DateUtil.offsetMinute(new Date(), 60))
                .setIsPush(1);
        return toAjax2(sysSmsService.save(sms));
    }
}
