package com.joyfishs.dawa.person.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.person.domain.vo.PersonCreditTopVo;
import com.joyfishs.dawa.person.domain.vo.PersonCreditVo;
import com.joyfishs.dawa.person.entity.PersonCreditDetail;
import com.joyfishs.dawa.person.service.PersonCreditService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.page.TableDataInfo;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "人员积分")
@RequestMapping("/person/credit")
public class PersonCreditController extends BaseController {
    private final PersonCreditService personCreditService;
    @ApiOperation(value = "获取当前登录人员积分")
    @GetMapping("/get")
    @ResponseBody
    public R<PersonCreditVo> findByPersonId(@RequestParam(required = false) Long personId) {
        return R.ok(personCreditService.findByPersonId(ObjectUtil.isNull(personId) ? SecurityUtil.getPersonId() : personId));
    }

    @ApiOperation(value = "积分明细", notes = "获取指定人员或者当前登录人员积分明细")
    @GetMapping("/list")
    @ResponseBody
    public TableDataInfo<PersonCreditDetail> findCreditDetailListByPersonId(@RequestParam(required = false) Long personId) {
        startPage();
        List<PersonCreditDetail> result = personCreditService.findCreditDetailListByPersonId(ObjectUtil.isNull(personId) ? SecurityUtil.getPersonId() : personId);
        return getDataTable(result);
    }

    @ApiOperation(value = "分页列表积分", notes = "分页列表积分")
    @GetMapping("/listByPage")
    @ResponseBody
    public TableDataInfo<PersonCreditVo> listByPage(@RequestParam String name) {
        startPage();
        List<PersonCreditVo> result = personCreditService.listByPage(name, SecurityUtil.getOrgId());
        return getDataTable(result);
    }

    @ApiOperation(value = "积分排行榜", notes = "排行榜前10位")
    @GetMapping("/topTen")
    @ResponseBody
    public R<List<PersonCreditTopVo>> getTopTen() {
        return R.ok(personCreditService.getTopTen());
    }
}
