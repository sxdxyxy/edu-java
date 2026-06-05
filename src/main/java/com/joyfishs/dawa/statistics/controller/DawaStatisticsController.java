package com.joyfishs.dawa.statistics.controller;

import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.dawa.violation.service.ViolationRecordService;
import com.joyfishs.dawa.qualification.service.QualificationService;
import com.joyfishs.dawa.access.service.AccessRecordService;
import com.joyfishs.dawa.assessment.service.PracticalAssessmentService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 智理安全数据统计控制器
 * <p>
 * 提供安全码、资质证件、违章记录、准入记录、实操考核等模块的综合统计数据
 * </p>
 *
 * @author safe-edu
 * @since 2026-03-29
 */
@Slf4j
@RestController
@Api(tags = "智理安全数据统计")
@RequestMapping("/dawa/statistics")
public class DawaStatisticsController extends BaseController {

    @Autowired
    private SafetyCodeService safetyCodeService;

    @Autowired
    private ViolationRecordService violationRecordService;

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private AccessRecordService accessRecordService;

    @Autowired
    private PracticalAssessmentService practicalAssessmentService;

    /**
     * 获取智理安全综合统计概览
     */
    @GetMapping("/overview")
    @ApiOperation(value = "获取智理安全综合统计概览")
    public AjaxResult<?> getOverview(@RequestParam(required = false) Long orgId) {
        try {
            Map<String, Object> overview = new HashMap<>();

            // 安全码统计
            Map<String, Object> safetyCodeStats = new HashMap<>();
            if (orgId != null) {
                safetyCodeStats.put("todayNewCount", safetyCodeService.countTodayNewCodes(orgId));
                safetyCodeStats.put("validCount", safetyCodeService.countValidCodes(orgId));
                safetyCodeStats.put("expiredCount", safetyCodeService.countExpiredCodes(orgId));
            } else {
                safetyCodeStats.put("totalCount", safetyCodeService.count());
                safetyCodeStats.put("todayNewCount", 0);
                safetyCodeStats.put("validCount", 0);
                safetyCodeStats.put("expiredCount", 0);
            }
            overview.put("safetyCode", safetyCodeStats);

            // 违章记录统计
            Map<String, Object> violationStats = new HashMap<>();
            violationStats.put("totalCount", violationRecordService.countAll());
            violationStats.put("pendingCount", violationRecordService.listPending().size());
            violationStats.put("processedCount", violationRecordService.countByStatus("processed"));
            violationStats.put("minorCount", violationRecordService.countBySeverity("minor"));
            violationStats.put("majorCount", violationRecordService.countBySeverity("major"));
            violationStats.put("criticalCount", violationRecordService.countBySeverity("critical"));
            overview.put("violation", violationStats);

            // 资质证件统计
            Map<String, Object> qualificationStats = new HashMap<>();
            qualificationStats.put("totalCount", qualificationService.count());
            qualificationStats.put("validCount", qualificationService.countByStatus("valid"));
            qualificationStats.put("expiringCount", qualificationService.countByStatus("expiring"));
            qualificationStats.put("expiredCount", qualificationService.countByStatus("expired"));
            overview.put("qualification", qualificationStats);

            // 准入记录统计
            Map<String, Object> accessStats = new HashMap<>();
            accessStats.put("totalCount", accessRecordService.count());
            accessStats.put("todayEntryCount", accessRecordService.countTodayEntry());
            accessStats.put("todayExitCount", accessRecordService.countTodayExit());
            overview.put("access", accessStats);

            // 实操考核统计
            Map<String, Object> assessmentStats = new HashMap<>();
            assessmentStats.put("totalCount", practicalAssessmentService.countAll());
            assessmentStats.put("passRate", practicalAssessmentService.getOverallPassRate());
            assessmentStats.put("passedCount", practicalAssessmentService.countPassed());
            assessmentStats.put("failedCount", practicalAssessmentService.countFailed());
            overview.put("assessment", assessmentStats);

            return AjaxResult.success(overview);
        } catch (Exception e) {
            log.error("获取智理安全综合统计概览失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取安全码专项统计
     */
    @GetMapping("/safety-code")
    @ApiOperation(value = "获取安全码专项统计")
    public AjaxResult<?> getSafetyCodeStatistics(@RequestParam(required = false) Long orgId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            if (orgId != null) {
                stats.put("todayNewCount", safetyCodeService.countTodayNewCodes(orgId));
                stats.put("validCount", safetyCodeService.countValidCodes(orgId));
                stats.put("expiredCount", safetyCodeService.countExpiredCodes(orgId));
            } else {
                stats.put("totalCount", safetyCodeService.count());
            }

            // 按颜色统计
            stats.put("greenCount", safetyCodeService.countByColor("green"));
            stats.put("yellowCount", safetyCodeService.countByColor("yellow"));
            stats.put("redCount", safetyCodeService.countByColor("red"));

            return AjaxResult.success(stats);
        } catch (Exception e) {
            log.error("获取安全码专项统计失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取违章记录专项统计
     */
    @GetMapping("/violation")
    @ApiOperation(value = "获取违章记录专项统计")
    public AjaxResult<?> getViolationStatistics(
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) Long projectId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalCount", violationRecordService.countAll());
            stats.put("pendingCount", violationRecordService.listPending().size());
            stats.put("processedCount", violationRecordService.countByStatus("processed"));
            stats.put("appealedCount", violationRecordService.countByStatus("appealed"));

            // 按严重程度统计
            stats.put("minorCount", violationRecordService.countBySeverity("minor"));
            stats.put("majorCount", violationRecordService.countBySeverity("major"));
            stats.put("criticalCount", violationRecordService.countBySeverity("critical"));

            // 获取 TOP10 违章人员
            stats.put("top10Violators", violationRecordService.getTop10ViolatorsByScore(orgId));

            return AjaxResult.success(stats);
        } catch (Exception e) {
            log.error("获取违章记录专项统计失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取资质证件专项统计
     */
    @GetMapping("/qualification")
    @ApiOperation(value = "获取资质证件专项统计")
    public AjaxResult<?> getQualificationStatistics(@RequestParam(required = false) Long orgId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalCount", qualificationService.count());
            stats.put("validCount", qualificationService.countByStatus("valid"));
            stats.put("expiringCount", qualificationService.countByStatus("expiring"));
            stats.put("expiredCount", qualificationService.countByStatus("expired"));
            stats.put("verifiedCount", qualificationService.countVerified());

            // 按证件类型统计
            stats.put("electricianCount", qualificationService.countByCertType("电工证"));
            stats.put("welderCount", qualificationService.countByCertType("焊工证"));
            stats.put("scaffolderCount", qualificationService.countByCertType("架子工证"));

            return AjaxResult.success(stats);
        } catch (Exception e) {
            log.error("获取资质证件专项统计失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取准入记录专项统计
     */
    @GetMapping("/access")
    @ApiOperation(value = "获取准入记录专项统计")
    public AjaxResult<?> getAccessStatistics(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String date) {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalCount", accessRecordService.count());
            stats.put("todayEntryCount", accessRecordService.countTodayEntry());
            stats.put("todayExitCount", accessRecordService.countTodayExit());
            stats.put("currentOnSiteCount", accessRecordService.countCurrentOnSite());

            // 按准入类型统计
            stats.put("normalCount", accessRecordService.countByAccessType("normal"));
            stats.put("temporaryCount", accessRecordService.countByAccessType("temporary"));
            stats.put("deniedCount", accessRecordService.countByAccessType("denied"));

            return AjaxResult.success(stats);
        } catch (Exception e) {
            log.error("获取准入记录专项统计失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取实操考核专项统计
     */
    @GetMapping("/assessment")
    @ApiOperation(value = "获取实操考核专项统计")
    public AjaxResult<?> getAssessmentStatistics(@RequestParam(required = false) Long orgId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalCount", practicalAssessmentService.countAll());
            stats.put("passRate", practicalAssessmentService.getOverallPassRate());
            stats.put("passedCount", practicalAssessmentService.countPassed());
            stats.put("failedCount", practicalAssessmentService.countFailed());

            // 按考核类型统计
            stats.put("safetyCount", practicalAssessmentService.countByType("safety"));
            stats.put("equipmentCount", practicalAssessmentService.countByType("equipment"));
            stats.put("emergencyCount", practicalAssessmentService.countByType("emergency"));
            stats.put("skillCount", practicalAssessmentService.countByType("skill"));

            return AjaxResult.success(stats);
        } catch (Exception e) {
            log.error("获取实操考核专项统计失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取趋势数据（近 7 天）
     */
    @GetMapping("/trend/7days")
    @ApiOperation(value = "获取近 7 天趋势数据")
    public AjaxResult<?> get7DaysTrend(@RequestParam(required = false) Long orgId) {
        try {
            Map<String, Object> trend = new HashMap<>();

            // 这里返回示例数据，实际应该从数据库查询
            trend.put("dates", new String[]{"Day1", "Day2", "Day3", "Day4", "Day5", "Day6", "Day7"});
            trend.put("newSafetyCodes", new Integer[]{10, 15, 8, 20, 12, 18, 14});
            trend.put("newViolations", new Integer[]{3, 5, 2, 4, 1, 3, 2});
            trend.put("newAccess", new Integer[]{50, 65, 48, 72, 55, 68, 52});

            return AjaxResult.success(trend);
        } catch (Exception e) {
            log.error("获取趋势数据失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }
}
