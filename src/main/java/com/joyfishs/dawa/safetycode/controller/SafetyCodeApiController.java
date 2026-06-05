package com.joyfishs.dawa.safetycode.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.access.entity.AccessRecord;
import com.joyfishs.dawa.access.service.AccessRecordService;
import com.joyfishs.dawa.safetycode.entity.SafetyCode;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 安全码 API Controller
 * <p>
 * 提供安全码的生成和查询 REST API（按照任务需求）
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-30
 */
@Slf4j
@RestController
@Api(tags = "安全码API")
@RequestMapping("/safety-codes")
public class SafetyCodeApiController extends BaseController {

    @Autowired
    private SafetyCodeService safetyCodeService;

    @Autowired
    private AccessRecordService accessRecordService;

    /**
     * 按用户ID生成安全码
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（可选）
     * @return 生成的安全码
     */
    @GetMapping("/generate/{userId}")
    @PreAuthorize("@ss.hasPermi('safety:code:generate')")
    @ApiOperation(value = "按用户ID生成安全码")
    @Log(title = "按用户ID生成安全码", businessType = BusinessType.INSERT)
    public AjaxResult<?> generateSafetyCodeByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) Long projectId) {
        try {
            SafetyCode safetyCode = safetyCodeService.generateSafetyCode(userId, projectId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", safetyCode.getUserId());
            result.put("color", safetyCode.getColor());
            result.put("status", safetyCode.getStatus());
            result.put("validFrom", safetyCode.getValidFrom());
            result.put("validTo", safetyCode.getValidTo());
            
            return AjaxResult.success("安全码生成成功", result);
        } catch (Exception e) {
            log.error("生成安全码失败，userId: {}, projectId: {}", userId, projectId, e);
            return AjaxResult.error("生成安全码失败：" + e.getMessage());
        }
    }

    /**
     * 查询当前用户安全码（支持按项目部过滤）
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（可选，用于按项目部过滤）
     * @return 当前安全码
     */
    @GetMapping("/{userId}")
    @ApiOperation(value = "查询当前安全码")
    public AjaxResult<?> getCurrentSafetyCode(
            @PathVariable Long userId,
            @RequestParam(required = false) Long projectId) {
        try {
            SafetyCode safetyCode = safetyCodeService.getByUserIdAndProject(userId, projectId);
            if (safetyCode == null) {
                return AjaxResult.success("该用户暂无安全码", null);
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("userId", safetyCode.getUserId());
            result.put("color", safetyCode.getColor());
            result.put("status", safetyCode.getStatus());
            result.put("validFrom", safetyCode.getValidFrom());
            result.put("validTo", safetyCode.getValidTo());
            result.put("code", safetyCode.getCode());
            result.put("projectId", safetyCode.getProjectId());

            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("查询安全码失败，userId: {}, projectId: {}", userId, projectId, e);
            return AjaxResult.error("查询安全码失败：" + e.getMessage());
        }
    }

    /**
     * 核验安全码并记录准入
     *
     * @param code 安全码
     * @param gateId 闸机 ID（可选）
     * @return 核验结果
     */
    @GetMapping("/verify-and-record/{code}")
    @ApiOperation(value = "核验安全码并记录准入")
    @Log(title = "准入核验", businessType = BusinessType.INSERT)
    public R<Map<String, Object>> verifyAndRecord(@PathVariable String code, @RequestParam(required = false) String gateId) {
        try {
            SafetyCode safetyCode = safetyCodeService.verifySafetyCode(code);
            if (safetyCode == null) {
                return R.fail("安全码无效或已过期");
            }
            
            // 只有绿码允许准入记录（或者根据业务逻辑，黄码也可以记录但有警告）
            String accessType = "normal";
            if ("red".equals(safetyCode.getColor())) {
                return R.fail("红码禁止入场");
            } else if ("yellow".equals(safetyCode.getColor())) {
                accessType = "temporary"; // 黄码视为临时/限制准入
            }
            
            // 自动记录准入
            AccessRecord record = accessRecordService.recordAccess(
                safetyCode.getUserId(), 
                safetyCode.getProjectId(), 
                accessType, 
                safetyCode.getColor(), 
                gateId, 
                null, 
                null, 
                "扫码核验自动记录"
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("safetyCode", safetyCode);
            result.put("accessRecord", record);
            result.put("allowAccess", !"red".equals(safetyCode.getColor()));
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("核验并记录准入失败，code: {}", code, e);
            return R.fail("准入核验失败：" + e.getMessage());
        }
    }
    
    /**
     * 核验安全码
     *
     * @param code 安全码
     * @return 核验结果
     */
    @GetMapping("/verify/{code}")
    @ApiOperation(value = "核验安全码")
    public R<Map<String, Object>> verifySafetyCode(@PathVariable String code) {
        try {
            SafetyCode safetyCode = safetyCodeService.verifySafetyCode(code);
            if (safetyCode == null) {
                return R.fail("安全码无效或已过期");
            }
            
            // 获取用户信息（如果有需要的话）
            Map<String, Object> result = new HashMap<>();
            result.put("userId", safetyCode.getUserId());
            result.put("color", safetyCode.getColor());
            result.put("status", safetyCode.getStatus());
            result.put("validFrom", safetyCode.getValidFrom());
            result.put("validTo", safetyCode.getValidTo());
            result.put("projectId", safetyCode.getProjectId());
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("核验安全码失败，code: {}", code, e);
            return R.fail("安全码核验失败：" + e.getMessage());
        }
    }
}