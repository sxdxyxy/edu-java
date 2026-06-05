package com.joyfishs.dawa.mobile.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.banner.domain.vo.BannerVo;
import com.joyfishs.dawa.banner.service.BannerService;
import com.joyfishs.dawa.mobile.service.MobileHomeService;
import com.joyfishs.dawa.notice.domain.vo.NoticeVo;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 移动端首页
 */
@Slf4j
@RestController
@Api(tags = "移动端首页")
@RequestMapping("/mobile/home")
public class MobileHomeController extends BaseController {
    @Autowired
    private MobileHomeService mobileHomeService;
    @Autowired
    private BannerService bannerService;;
    @GetMapping("/remind")
    @ApiOperation(value = "学习提醒")
    public TableDataInfo<?> remindList() {
        Long personId = SecurityUtil.getPersonId();
        startPage();
        List<Project> remindList = mobileHomeService.remindList(personId);
        return getDataTable(remindList);
    }

    @GetMapping("/banner")
    @ApiOperation(value = "轮播图")
    public R<List<BannerVo>> banner() {
        List<BannerVo> list = bannerService.queryList();
        return R.ok(list);
    }

    @GetMapping("/news")
    @ApiOperation(value = "新闻资讯")
    public TableDataInfo<NoticeVo> news(String title) {
        Long personId = null;
        if (SecurityUtil.isLogin()) {
            personId = SecurityUtil.getPersonId();
        }
        startPage();
        List<NoticeVo> noticeList = mobileHomeService.newsBulletin(personId, 1, title);
        return getDataTable(noticeList);
    }

    @GetMapping("/notice")
    @ApiOperation(value = "通知公告")
    public TableDataInfo<NoticeVo> notice(String title) {
        Long personId = null;
        if (SecurityUtil.isLogin()) {
            personId = SecurityUtil.getPersonId();
        }
        startPage();
        List<NoticeVo> noticeList = mobileHomeService.newsBulletin(personId, 2, title);
        return getDataTable(noticeList);
    }

    @GetMapping("/mustRead")
    @ApiOperation(value = "必读文件")
    public TableDataInfo<?> newsBulletin(String title) {
        Long personId = null;
        if (SecurityUtil.isLogin()) {
            personId = SecurityUtil.getPersonId();
        }
        startPage();
        List<NoticeVo> noticeList = mobileHomeService.newsBulletin(personId, 3, title);
        return getDataTable(noticeList);
    }
}
