package com.joyfishs.dawa.safety.controller;

import com.github.pagehelper.PageInfo;
import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safety.entity.SafetyRetrainingRecord;
import com.joyfishs.dawa.safety.service.SafetyRetrainingService;
import com.joyfishs.dawa.safety.service.SafetyScoreService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全再培训控制器
 * 提供再培训记录的 CRUD + 状态流转 + 积分恢复 API
 *
 * @author safe-edu
 * @since 2026-05-30
 */
@Slf4j
@RestController
@Component
@RequestMapping("/safety/retraining")
@Api(tags = "安全再培训管理")

public class SafetyRetrainingController {

    @Autowired
    private SafetyRetrainingService retrainingService;

    @Autowired
    private SafetyScoreService safetyScoreService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('safety:retraining:list')")
    @ApiOperation("查询再培训记录列表")
    public AjaxResult<Map<String, Object>> list(
            @ApiParam("状态筛选：pending/ongoing/completed/failed") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        com.baomidou.mybatisplus.core.metadata.IPage<SafetyRetrainingRecord> page = retrainingService.lambdaQuery()
                .eq(status != null && !status.isEmpty(), SafetyRetrainingRecord::getStatus, status)
                .orderByDesc(SafetyRetrainingRecord::getCreatedAt)
                .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize));

        Map<String, Object> result = new HashMap<>();
        result.put("rows", page.getRecords());
        result.put("total", page.getTotal());
        return AjaxResult.success(result);
    }

    @GetMapping("/person/{personId}")
    @PreAuthorize("@ss.hasPermi('safety:retraining:list')")
    @ApiOperation("查询指定人员的再培训记录")
    public AjaxResult<List<SafetyRetrainingRecord>> listByPerson(@PathVariable Long personId) {
        List<SafetyRetrainingRecord> records = retrainingService.lambdaQuery()
                .eq(SafetyRetrainingRecord::getPersonId, personId)
                .orderByDesc(SafetyRetrainingRecord::getCreatedAt)
                .list();
        return AjaxResult.success(records);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('safety:retraining:list')")
    @ApiOperation("获取再培训记录详情")
    public AjaxResult<SafetyRetrainingRecord> getById(@PathVariable Long id) {
        SafetyRetrainingRecord record = retrainingService.getById(id);
        if (record == null) {
            return AjaxResult.error("再培训记录不存在");
        }
        return AjaxResult.success(record);
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("@ss.hasPermi('safety:retraining:edit')")
    @ApiOperation("开始再培训")
    public AjaxResult<?> startRetraining(@PathVariable Long id) {
        try {
            retrainingService.startRetraining(id);
            return AjaxResult.success("已开始再培训");
        } catch (Exception e) {
            return AjaxResult.error("操作失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("@ss.hasPermi('safety:retraining:process')")
    @ApiOperation("确认再培训完成（管理员操作，自动恢复积分）")
    public AjaxResult<ScoreChangeResult> confirmCompletion(@PathVariable Long id) {
        try {
            Long confirmedBy = SecurityUtil.getUserId();
            ScoreChangeResult result = retrainingService.confirmCompletion(id, confirmedBy);
            if (result == null) {
                return AjaxResult.error("确认失败，请检查记录状态");
            }
            return AjaxResult.success("再培训已确认，积分恢复" + result.getChangeAmount() + "分", result);
        } catch (Exception e) {
            return AjaxResult.error("操作失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("@ss.hasPermi('safety:retraining:process')")
    @ApiOperation("完成再培训（通过/未通过）")
    public AjaxResult<ScoreChangeResult> completeRetraining(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean passed) {
        try {
            Long operatorId = SecurityUtil.getUserId();
            retrainingService.completeRetraining(id, passed, operatorId);
            SafetyRetrainingRecord record = retrainingService.getById(id);
            // 返回积分变动结果
            if (passed && record != null) {
                ScoreChangeResult scoreResult = new ScoreChangeResult();
                scoreResult.setChangeType(SafetyScoreService.CHANGE_TYPE_RETRAINING);
                scoreResult.setAfterScore(record.getPersonId() != null ? safetyScoreService.getCurrentScore(record.getPersonId()) : null);
                scoreResult.setRetrainingRecordId(id);
                scoreResult.setChangeAmount(record.getScoreRestored() != null ? record.getScoreRestored() : 4);
                return AjaxResult.success("再培训已完成，积分已恢复", scoreResult);
            }
            return AjaxResult.success(passed ? "再培训已完成" : "再培训未通过");
        } catch (Exception e) {
            return AjaxResult.error("操作失败：" + e.getMessage());
        }
    }

    @GetMapping("/pending/count")
    @PreAuthorize("@ss.hasPermi('safety:retraining:list')")
    @ApiOperation("获取待处理再培训数量")
    public AjaxResult<Integer> getPendingCount() {
        long count = retrainingService.lambdaQuery()
                .in(SafetyRetrainingRecord::getStatus,
                    SafetyRetrainingRecord.STATUS_PENDING,
                    SafetyRetrainingRecord.STATUS_ONGOING)
                .count();
        return AjaxResult.success((int) count);
    }

}
