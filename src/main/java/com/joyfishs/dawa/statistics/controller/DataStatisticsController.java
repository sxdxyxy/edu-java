package com.joyfishs.dawa.statistics.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.joyfishs.dawa.statistics.domain.*;
import com.joyfishs.dawa.statistics.service.DataStatisticsService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 */
@Slf4j
@RestController
@Api(tags = "数据统计和首页统计")
@RequestMapping("/safetySupervision/statistics")
public class DataStatisticsController extends BaseController {

    @Autowired
    private DataStatisticsService dataStatisticsService;

    @GetMapping("/orgStatistics")
    @ApiOperation(value = "统计列表-按部门")
    public TableDataInfo<?> orgStatistics(Long orgId) {
        startPage();
        List<DataStatisticsOrg> list = dataStatisticsService.listDataStatisticsOrg(orgId);
        return getDataTable(list);
    }

    @GetMapping("/personalStatistics")
    @ApiOperation(value = "统计列表-个人统计")
    public TableDataInfo<?> personalStatistics(Long orgId, String personName, Integer asc, Integer year) {
        startPage();
        List<DataStatisticsPersonal> list = dataStatisticsService.listDataStatisticsPersonal(orgId, personName, asc,year);
        return getDataTable(list);
    }

    @GetMapping("/personalStatistics/classHourTop")
    @ApiOperation(value = "个人统计-公司学时前十统计列表")
    public AjaxResult<?> personalStatisticsClassHourTop(Long orgId, Integer type) {
        List<DataStatisticsPersonal> list = dataStatisticsService.listPersonalClassHourTop(orgId, type);
        return AjaxResult.success(list);
    }

    @GetMapping("/homeStatistics")
    @ApiOperation(value = "首页统计数据")
    public R<HomeStatistics> homeStatistics() {
        return R.ok(dataStatisticsService.getHomeStatistics(SecurityUtil.getOrgId()));
    }

    @GetMapping("/eventsList")
    @ApiOperation(value = "首页动态列表")
    public R<List<EventsList>> eventsList() {
        return R.ok(dataStatisticsService.getHomeEventsList(SecurityUtil.getOrgId()));
    }

    @GetMapping("/activityCount")
    @ApiOperation(value = "活跃人数")
    public R<List<ActiveRecord>> activityCount() {
        List<ActiveRecord> result = dataStatisticsService.getHomeActiveRecord(SecurityUtil.getOrgId(),8);
        return R.ok(result);
    }

    @GetMapping("/overviewStatistics")
    @ApiOperation(value = "统计概述")
    public R<OverviewStatistics> overviewStatistics() {
        return R.ok(dataStatisticsService.getOverviewStatistics(SecurityUtil.getOrgId()));
    }

    /**
     * T20: 安全码统计卡片（今日新增、有效码数、过期码数）
     */
    @GetMapping("/safetyCodeStatistics")
    @ApiOperation(value = "安全码统计")
    public R<Map<String, Object>> safetyCodeStatistics() {
        return R.ok(dataStatisticsService.getSafetyCodeStatistics(SecurityUtil.getOrgId()));
    }

    /**
     * T21: 资质证件到期预警列表
     */
    @GetMapping("/qualificationExpiryWarnings")
    @ApiOperation(value = "资质证件到期预警")
    public R<List<Map<String, Object>>> qualificationExpiryWarnings() {
        return R.ok(dataStatisticsService.getQualificationExpiryWarnings(SecurityUtil.getOrgId()));
    }

    /**
     * T22: 违章记分排行榜（top 10）
     */
    @GetMapping("/violationRankTop10")
    @ApiOperation(value = "违章记分排行榜top10")
    public R<List<Map<String, Object>>> violationRankTop10() {
        return R.ok(dataStatisticsService.getViolationRankTop10(SecurityUtil.getOrgId()));
    }

    /**
     * T23: 准入记录今日统计（入场人数、离场人数、在场人数）
     */
    @GetMapping("/accessRecordTodayStatistics")
    @ApiOperation(value = "准入记录今日统计")
    public R<Map<String, Object>> accessRecordTodayStatistics() {
        return R.ok(dataStatisticsService.getAccessRecordTodayStatistics(SecurityUtil.getOrgId()));
    }

    /**
     * T24: 实操考核通过率统计
     */
    @GetMapping("/assessmentPassRateStatistics")
    @ApiOperation(value = "实操考核通过率统计")
    public R<Map<String, Object>> assessmentPassRateStatistics() {
        return R.ok(dataStatisticsService.getAssessmentPassRateStatistics(SecurityUtil.getOrgId()));
    }

}
