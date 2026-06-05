package com.joyfishs.dawa.person.controller;

import com.github.pagehelper.PageInfo;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.domain.param.PersonRegisterReq;
import com.joyfishs.dawa.person.domain.result.PersonListResult;
import com.joyfishs.dawa.person.domain.result.PersonRegisterTableData;
import com.joyfishs.dawa.person.entity.PersonRegister;
import com.joyfishs.dawa.person.service.PersonRegisterService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.config.security.captcha.CaptchaService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.LoginRes;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.entity.UsernamePasswordLoginBody;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.enums.RoleType;
import com.joyfishs.system.enums.SmsType;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.service.SysLoginService;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.Constants;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.joyfishs.utils.page.TableSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "人员注册")
@RequestMapping("/person/register")
public class PersonRegisterController extends BaseController {

    @Autowired
    private PersonRegisterService personRegisterService;

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SysLoginService sysLoginService;
    @Autowired
    private CaptchaService captchaService;

    // 人员注册列表
    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('person:register:list')")
    @ApiOperation(value = "查询人员注册列表")
    public PersonRegisterTableData pageList(@RequestParam Long orgId) {
        startPage();
        List<PersonListResult> list = personRegisterService.findList(orgId);
        SysOrg sysOrg = sysOrgService.findUnitByOrgId(orgId);
        PersonRegisterTableData rspData = new PersonRegisterTableData();
        rspData.setInvitationCode(sysOrg.getInvitationCode());
        rspData.setCode(HttpStatus.OK.value());
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo<PersonListResult>(list).getTotal());
        rspData.setPageNo(TableSupport.buildPageRequest().getPageNum());
        return rspData;
    }

    @PostMapping("/invitation")
    @ApiOperation(value = "根据邀请码查询单位")
    public AjaxResult<?> invitationCode(String invitationCode) {
        log.info("PersonRegisterController - invitation invitationCode:{}", invitationCode);
        SysOrg sysOrg = sysOrgService.getByInvitationCode(invitationCode);
        if (null == sysOrg) {
            throw new CustomException("邀请码错误");
        }
        return AjaxResult.success().put("orgId", sysOrg.getId()).put("orgName", sysOrg.getName());
    }

    @PostMapping("/add")
    @Log(title = "人员注册", businessType = BusinessType.INSERT)
    @ApiOperation(value = "人员注册")
    public AjaxResult<?> add(@RequestBody @Validated PersonRegisterReq req) {
        //注册验证码校验
        if (!captchaService.verifyCaptcha(req.getPhone(), req.getCaptcha(), SmsType.REGISTER)) {
            return AjaxResult.error("输入的验证码不正确，请核对后重新输入。");
        }
        //检查手机号是否已经注册过
        PersonRegister dbRegister = personRegisterService.findByPhone(req.getPhone());
        //如果不是删除状态不允许继续注册
        if (null != dbRegister && dbRegister.getIsDelete() == YesOrNoState.NO.getState()) {
            return AjaxResult.error("该手机号已经注册过，请使用其他手机号注册。");
        }
        //注册新的人员信息
        PersonRegister register = personRegisterService.add(req);
        // 生成令牌,注册完之后登陆,拿到token,修改人员信息
        UsernamePasswordLoginBody loginBody = new UsernamePasswordLoginBody();
        loginBody.setUsername(register.getUserName());
        loginBody.setPassword(StringUtils.isEmpty(req.getPassword()) ? SysUser.DEFAULT_PASSWORD : req.getPassword());
        LoginRes result = sysLoginService.login(loginBody, RoleType.STUDENT, false);
        return AjaxResult.success().put(Constants.TOKEN, result.getToken()).put("personId", register.getPersonId());
    }

}
