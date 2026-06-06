package com.joyfishs.dawa.safety.controller;

import com.joyfishs.dawa.safety.entity.AppealRecord;
import com.joyfishs.dawa.safety.service.AppealRecordService;
import com.joyfishs.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 安全申诉 Controller
 * <p>
 * 提供安全申诉的查询接口 (审批/发起由 ViolationRecordController 内的 /violation/appeal/* 负责)
 * </p>
 *
 * @author safe-edu
 * @since 2026-06-06
 */
@Slf4j
@RestController
@RequestMapping("/safety/appeal")
@Api(tags = "安全申诉管理")
public class AppealController {

    @Autowired
    private AppealRecordService appealRecordService;

    /**
     * 查询待审批申诉列表 (安全主管工作台用)
     */
    @GetMapping("/pending")
    @PreAuthorize("@ss.hasPermi('safety:retraining:list')")
    @ApiOperation("查询待审批申诉")
    public AjaxResult<List<AppealRecord>> listPending() {
        try {
            List<AppealRecord> list = appealRecordService.listPendingAppeals();
            return AjaxResult.success(list);
        } catch (Exception e) {
            log.error("查询待审批申诉失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询指定人员的申诉记录
     */
    @GetMapping("/person/{personId}")
    @PreAuthorize("@ss.hasPermi('safety:retraining:list')")
    @ApiOperation("查询指定人员的申诉记录")
    public AjaxResult<List<AppealRecord>> listByPerson(@PathVariable Long personId) {
        try {
            List<AppealRecord> list = appealRecordService.listByPersonId(personId);
            return AjaxResult.success(list);
        } catch (Exception e) {
            log.error("查询人员申诉失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }
}
