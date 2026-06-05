package com.joyfishs.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.person.domain.param.FacePhotoRequest;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.domain.LoginRes;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.entity.CaptchaLoginBody;
import com.joyfishs.system.entity.UsernamePasswordLoginBody;
import com.joyfishs.system.entity.WeiXinLoginBody;
import com.joyfishs.system.entity.vo.PersonVo;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.enums.DeviceType;
import com.joyfishs.system.enums.RoleType;
import com.joyfishs.system.service.SysLoginService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.Constants;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "登录验证")
@Validated
@Slf4j
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService sysLoginService;

    @ApiOperation(value = "平台管理登录")
    @PostMapping("/login")
    @Log(title = "管理员登录系统", businessType = BusinessType.LOGIN)
    public AjaxResult<?> login(@Validated @RequestBody UsernamePasswordLoginBody loginBody) {
        // 生成令牌
        LoginRes result = sysLoginService.login(loginBody, RoleType.MANAGER, true);
        if(result.getCode()!=200){
            throw new CustomException(result.getErrorMsg());
        }
        return AjaxResult.success().put(Constants.TOKEN, result.getToken()).put("identity", result.getIdentity() ? "yes" : "no");
    }

    @ApiOperation(value = "学员用户名密码登录")
    @PostMapping("/studentLogin")
    @Log(title = "学员用户名密码登录系统", businessType = BusinessType.LOGIN)
    public R<LoginRes> studentLogin(@Validated @RequestBody UsernamePasswordLoginBody loginBody) {
        // 生成令牌
        LoginRes result = sysLoginService.login(loginBody, RoleType.STUDENT, true);
        return R.ok(result);
    }

    @ApiOperation(value = "学员手机验证码登录")
    @PostMapping("/studentCaptchaLogin")
    @Log(title = "学员手机验证码登录系统", businessType = BusinessType.LOGIN)
    public R<LoginRes> studentCaptchaLogin(@Validated @RequestBody CaptchaLoginBody loginBody) {
        LoginRes result = sysLoginService.captchaLogin(loginBody, true);
        return R.ok(result);
    }

    @ApiOperation(value = "学员网页微信扫码登录")
    @PostMapping("/studentWeiXinWebLogin")
    @Log(title = "学员网页微信扫码登录", businessType = BusinessType.LOGIN)
    public R<LoginRes> studentWeiXinWebLogin(@Validated @RequestBody WeiXinLoginBody loginBody) {
        LoginRes result = sysLoginService.weiXinLogin(loginBody, DeviceType.WEB, true);
        return R.ok(result);
    }

    @ApiOperation(value = "学员APP微信登录")
    @PostMapping("/studentWeiXinAppLogin")
    @Log(title = "学员APP微信登录", businessType = BusinessType.LOGIN)
    public R<LoginRes> studentWeiXinAPPLogin(@Validated @RequestBody WeiXinLoginBody loginBody) {
        LoginRes result = sysLoginService.weiXinLogin(loginBody, DeviceType.APP, true);
        return R.ok(result);
    }

    @ApiOperation(value = "学员刷脸登录")
    @PostMapping("/faceLogin")
    @Log(title = "刷脸登录", businessType = BusinessType.LOGIN)
    public R<LoginRes> faceLogin(@Validated @RequestBody FacePhotoRequest loginBody) {
        LoginRes result = sysLoginService.faceLogin(loginBody, DeviceType.APP, true);
        return R.ok(result);
    }

    @ApiOperation(value = "学员小程序登录")
    @PostMapping("/studentMiniProgramLogin")
    @Log(title = "学员小程序登录", businessType = BusinessType.LOGIN)
    public R<LoginRes> studentMiniProgramLogin(@Validated @RequestBody WeiXinLoginBody loginBody) {
        LoginRes result = sysLoginService.weiXinLogin(loginBody, DeviceType.MINI_PROGRAM, true);
        return R.ok(result);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @PostMapping("/getLoginUser")
    public AjaxResult<?> getLoginUser() {
        log.info("SysLoginController - getLoginUser userId:{}", SecurityUtil.getUserId());
        String identity = sysLoginService.setLoginUserRole();
        return AjaxResult.success(SecurityUtil.getLoginUser()).put("identity", identity);
    }

    @ApiOperation(value = "获取当前用户信息")
    @PostMapping("/getUserInfo")
    public AjaxResult<?> getUserInfo() {
        log.info("SysLoginController - getUserInfo userId:{}", SecurityUtil.getUserId());
        PersonVo personVo = sysLoginService.getUserInfo(SecurityUtil.getUserId());
        return AjaxResult.success(personVo);
    }

}
