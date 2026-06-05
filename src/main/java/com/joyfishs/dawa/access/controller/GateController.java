package com.joyfishs.dawa.access.controller;

import com.joyfishs.dawa.access.entity.AccessRecord;
import com.joyfishs.dawa.access.service.AccessRecordService;
import com.joyfishs.dawa.safetycode.entity.SafetyCode;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.utils.AjaxResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 闸机控制器
 * <p>
 * 处理闸机扫描安全码后的回调API
 * </p>
 *
 * @author safe-edu
 * @since 2026-04-26
 */
@Slf4j
@RestController
@RequestMapping("/access/gate")
public class GateController {

    @Autowired
    private SafetyCodeService safetyCodeService;

    @Autowired
    private AccessRecordService accessRecordService;

    /**
     * 闸机回调接口
     * <p>
     * 闸机扫描安全码后调用此接口，根据安全码颜色判断是否允许通行
     * </p>
     *
     * @param request 闸机回调请求
     * @return 通行结果
     */
    @PostMapping("/callback")
    @ApiOperation("闸机回调接口")
    public AjaxResult<?> gateCallback(@RequestBody GateCallbackRequest request) {
        // 参数校验
        if (request.getPersonId() == null) {
            return AjaxResult.error(403, "人员ID不能为空");
        }
        if (request.getProjectId() == null) {
            return AjaxResult.error(403, "项目ID不能为空");
        }
        if (request.getGateId() == null || request.getGateId().trim().isEmpty()) {
            return AjaxResult.error(403, "闸机ID不能为空");
        }
        if (request.getAction() == null || request.getAction().trim().isEmpty()) {
            return AjaxResult.error(403, "动作不能为空");
        }

        // 查找安全码
        SafetyCode safetyCode = safetyCodeService.getByUserIdAndProject(request.getPersonId(), request.getProjectId());
        if (safetyCode == null) {
            log.warn("闸机回调-无安全码: personId={}, projectId={}, gateId={}",
                    request.getPersonId(), request.getProjectId(), request.getGateId());
            return AjaxResult.error(403, "无安全码，禁止通行");
        }

        // 检查安全码状态
        if (!SafetyCode.STATUS_ACTIVE.equals(safetyCode.getStatus())) {
            log.warn("闸机回调-安全码未激活: personId={}, projectId={}, status={}",
                    request.getPersonId(), request.getProjectId(), safetyCode.getStatus());
            return AjaxResult.error(403, "安全码未激活，禁止通行");
        }

        // 检查颜色并判断是否允许通行
        String color = safetyCode.getColor();
        boolean allow = SafetyCode.COLOR_GREEN.equals(color);  // 绿色允许
        boolean warn = SafetyCode.COLOR_YELLOW.equals(color);   // 黄色警告但允许
        boolean deny = SafetyCode.COLOR_RED.equals(color);     // 红色拒绝

        // 如果是红色，或者非绿色的其他情况，都拒绝
        if (deny || (!allow && !warn)) {
            log.info("闸机回调-禁止通行: personId={}, color={}, gateId={}",
                    request.getPersonId(), color, request.getGateId());
            Map<String, Object> result = new HashMap<>();
            result.put("allow", false);
            result.put("color", color);
            return new AjaxResult<>(403, "禁止通行", result);
        }

        // 记录通行
        try {
            if ("exit".equalsIgnoreCase(request.getAction())) {
                // 出场记录：查找当前在场记录并更新
                java.util.List<AccessRecord> currentOnsite = accessRecordService.listCurrentOnSite(request.getProjectId());
                AccessRecord existingRecord = currentOnsite.stream()
                        .filter(r -> r.getUserId().equals(request.getPersonId()))
                        .findFirst()
                        .orElse(null);
                if (existingRecord != null) {
                    accessRecordService.recordExit(existingRecord.getId());
                } else {
                    // 如果没有找到在场记录，创建一个出场记录
                    AccessRecord exitRecord = new AccessRecord();
                    exitRecord.setUserId(request.getPersonId());
                    exitRecord.setProjectId(request.getProjectId());
                    exitRecord.setGateId(request.getGateId());
                    exitRecord.setSafetyCodeColor(color);
                    exitRecord.setExitTime(new Date());
                    accessRecordService.save(exitRecord);
                }
            } else {
                // 进场记录
                accessRecordService.recordAccess(
                        request.getPersonId(),
                        request.getProjectId(),
                        "normal",  // 准入类型
                        color,      // 安全码颜色快照
                        request.getGateId(),
                        null,       // cameraSnapshot
                        null,       // location
                        null        // remarks
                );
            }
        } catch (Exception e) {
            log.error("闸机回调-记录通行失败: personId={}, error={}",
                    request.getPersonId(), e.getMessage());
            // 记录失败不影响通行决策
        }

        // 返回通行结果
        Map<String, Object> result = new HashMap<>();
        result.put("allow", true);
        result.put("color", color);

        String message;
        if (warn) {
            result.put("warning", "请注意安全");
            message = "警告";
            log.info("闸机回调-警告通行: personId={}, color={}, gateId={}",
                    request.getPersonId(), color, request.getGateId());
        } else {
            message = "允许通行";
            log.info("闸机回调-允许通行: personId={}, color={}, gateId={}",
                    request.getPersonId(), color, request.getGateId());
        }

        return AjaxResult.success(message, result);
    }

    /**
     * 闸机回调请求DTO
     */
    @lombok.Data
    public static class GateCallbackRequest {
        /**
         * 人员ID
         */
        private Long personId;

        /**
         * 项目ID
         */
        private Long projectId;

        /**
         * 闸机ID
         */
        private String gateId;

        /**
         * 动作：entry-进场，exit-出场
         */
        private String action;
    }
}
