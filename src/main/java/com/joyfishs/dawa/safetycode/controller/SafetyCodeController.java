package com.joyfishs.dawa.safetycode.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.safety.qrcode.QrCodeStorage;
import com.joyfishs.dawa.safetycode.entity.SafetyCode;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

/**
 * 安全码 Controller
 * <p>
 * 提供安全码的查询、生成、更新等 REST API
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Slf4j
@RestController
@Api(tags = "安全码管理")
@RequestMapping("/safety-code")
public class SafetyCodeController extends BaseController {

    private final SafetyCodeService safetyCodeService;
    // D6: 用于列表展示时二维码懒加载 (DB 未存时兜底生成, 但正常情况直接读 qrCodeData 字段)
    private final QrCodeStorage qrCodeStorage;

    public SafetyCodeController(SafetyCodeService safetyCodeService, QrCodeStorage qrCodeStorage) {
        this.safetyCodeService = safetyCodeService;
        this.qrCodeStorage = qrCodeStorage;
    }

    /**
     * 获取用户安全码（支持按项目部过滤）
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（可选，用于按项目部过滤）
     * @return 安全码信息
     */
    @GetMapping("/{userId}")
    @ApiOperation(value = "获取用户安全码")
    public AjaxResult<?> getSafetyCode(
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
            log.error("获取安全码失败，userId: {}, projectId: {}", userId, projectId, e);
            return AjaxResult.error("获取安全码失败：" + e.getMessage());
        }
    }

    /**
     * 生成安全码
     *
     * @param params 请求参数（userId: 用户 ID，projectId: 项目 ID，score: 安全分，remarks: 备注）
     * @return 生成的安全码
     */
    @PostMapping("/generate")
    @PreAuthorize("@ss.hasPermi('safety:code:generate')")
    @ApiOperation(value = "生成安全码")
    @Log(title = "生成安全码", businessType = BusinessType.INSERT)
    public AjaxResult<?> generateSafetyCode(@RequestBody(required = false) Map<String, Object> params) {
        try {
            Long userId = null;

            // 从请求体中获取 userId，如果未提供则使用当前登录用户
            if (params != null && params.containsKey("userId")) {
                Object userIdObj = params.get("userId");
                if (userIdObj instanceof Number) {
                    userId = ((Number) userIdObj).longValue();
                } else if (userIdObj instanceof String) {
                    userId = Long.parseLong((String) userIdObj);
                }
            }

            if (userId == null) {
                userId = SecurityUtil.getUserId();
            }

            SafetyCode safetyCode = safetyCodeService.generateSafetyCode(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", safetyCode.getUserId());
            result.put("color", safetyCode.getColor());
            result.put("status", safetyCode.getStatus());
            result.put("validFrom", safetyCode.getValidFrom());
            result.put("validTo", safetyCode.getValidTo());
            result.put("code", safetyCode.getCode());
            result.put("qrCode", safetyCode.getQrCode());

            return AjaxResult.success("安全码生成成功", result);
        } catch (Exception e) {
            log.error("生成安全码失败", e);
            return AjaxResult.error("生成安全码失败：" + e.getMessage());
        }
    }

    /**
     * 刷新安全码
     *
     * @param userId 用户 ID（可选，为空则使用当前登录用户）
     * @return 刷新后的安全码
     */
    @PostMapping("/refresh/{userId}")
    @PreAuthorize("@ss.hasPermi('safety:code:refresh')")
    @ApiOperation(value = "刷新安全码")
    @Log(title = "刷新安全码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> refreshSafetyCode(@PathVariable Long userId) {
        try {
            SafetyCode safetyCode = safetyCodeService.refreshSafetyCode(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", safetyCode.getUserId());
            result.put("color", safetyCode.getColor());
            result.put("status", safetyCode.getStatus());
            result.put("validFrom", safetyCode.getValidFrom());
            result.put("validTo", safetyCode.getValidTo());
            
            return AjaxResult.success("安全码刷新成功", result);
        } catch (Exception e) {
            log.error("刷新安全码失败，userId: {}", userId, e);
            return AjaxResult.error("刷新安全码失败：" + e.getMessage());
        }
    }

    /**
     * 更新安全码颜色
     *
     * @param userId 用户 ID
     * @param color 颜色标识：green/yellow/red
     * @return 更新结果
     */
    @PutMapping("/{userId}/color")
    @PreAuthorize("@ss.hasPermi('safety:code:update')")
    @ApiOperation(value = "更新安全码颜色")
    @Log(title = "更新安全码颜色", businessType = BusinessType.UPDATE)
    public AjaxResult<?> updateColor(@PathVariable Long userId, @RequestBody Map<String, String> params) {
        try {
            String color = params.get("color");
            if (color == null || color.trim().isEmpty()) {
                return AjaxResult.error("颜色参数不能为空");
            }
            
            SafetyCode safetyCode = safetyCodeService.updateColor(userId, color);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", safetyCode.getUserId());
            result.put("color", safetyCode.getColor());
            result.put("status", safetyCode.getStatus());
            
            return AjaxResult.success("颜色更新成功", result);
        } catch (Exception e) {
            log.error("更新安全码颜色失败，userId: {}, params: {}", userId, params, e);
            return AjaxResult.error("更新颜色失败：" + e.getMessage());
        }
    }

    /**
     * 重新评估安全码颜色
     * <p>
     * 根据最新违章记录、资质状态、培训情况重新计算颜色
     * </p>
     *
     * @param userId 用户 ID
     * @param projectId 项目部 ID（可选，用于范围评估）
     * @param violationScore 违章扣分
     * @param safetyTrainingPassed 安全培训是否合格
     * @return 评估结果
     */
    @PostMapping("/{userId}/reevaluate")
    @PreAuthorize("@ss.hasPermi('safety:code:evaluate')")
    @ApiOperation(value = "重新评估安全码颜色")
    @Log(title = "重新评估安全码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> reevaluateColor(
            @PathVariable Long userId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Integer violationScore,
            @RequestParam(required = false) Boolean safetyTrainingPassed) {
        try {
            SafetyCode safetyCode = safetyCodeService.reevaluateColor(userId, projectId, violationScore, safetyTrainingPassed);

            Map<String, Object> result = new HashMap<>();
            result.put("userId", safetyCode.getUserId());
            result.put("color", safetyCode.getColor());
            result.put("status", safetyCode.getStatus());

            return AjaxResult.success("评估完成", result);
        } catch (Exception e) {
            log.error("重新评估安全码失败，userId: {}", userId, e);
            return AjaxResult.error("评估失败：" + e.getMessage());
        }
    }

    /**
     * 验证安全码有效性
     *
     * @param userId 用户 ID
     * @return 验证结果
     */
    @GetMapping("/{userId}/validate")
    @ApiOperation(value = "验证安全码有效性")
    public R<Boolean> validateSafetyCode(@PathVariable Long userId) {
        try {
            boolean isValid = safetyCodeService.validate(userId);
            return R.ok(isValid);
        } catch (Exception e) {
            log.error("验证安全码失败，userId: {}", userId, e);
            return R.fail("验证失败：" + e.getMessage());
        }
    }

    /**
     * 暂停安全码
     *
     * @param userId 用户 ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/suspend")
    @PreAuthorize("@ss.hasPermi('safety:code:manage')")
    @ApiOperation(value = "暂停安全码")
    @Log(title = "暂停安全码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> suspend(@PathVariable Long userId) {
        try {
            SafetyCode safetyCode = safetyCodeService.suspend(userId);
            if (safetyCode == null) {
                return AjaxResult.error("未找到该用户的安全码");
            }
            return AjaxResult.success("安全码已暂停");
        } catch (Exception e) {
            log.error("暂停安全码失败，userId: {}", userId, e);
            return AjaxResult.error("暂停失败：" + e.getMessage());
        }
    }

    /**
     * 恢复安全码
     *
     * @param userId 用户 ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/restore")
    @PreAuthorize("@ss.hasPermi('safety:code:manage')")
    @ApiOperation(value = "恢复安全码")
    @Log(title = "恢复安全码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> restore(@PathVariable Long userId) {
        try {
            SafetyCode safetyCode = safetyCodeService.restore(userId);
            if (safetyCode == null) {
                return AjaxResult.error("未找到该用户的安全码");
            }
            return AjaxResult.success("安全码已恢复");
        } catch (Exception e) {
            log.error("恢复安全码失败，userId: {}", userId, e);
            return AjaxResult.error("恢复失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询所有安全码记录
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('safety:code:list')")
    @ApiOperation(value = "分页查询安全码列表")
    public AjaxResult<?> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status) {
        startPage();
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(userName)) {
            params.put("userName", userName);
        }
        if (StringUtils.isNotEmpty(phone)) {
            params.put("phone", phone);
        }
        if (StringUtils.isNotEmpty(status)) {
            params.put("status", status);
        }
        List<SafetyCode> list = safetyCodeService.listWithUser(params);

        // 生成列表展示用的二维码
        generateQrCodeForList(list);
        PageInfo<SafetyCode> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        return AjaxResult.success(result);
    }

    /**
     * 为列表中的安全码补全二维码 URL
     * <p>
     * D6 改造: 不再每次重生成 base64. 优先从 qrCodeData 字段读 (DB 已存),
     * 字段为空时 (老数据 / 早期生成) 才走 QrCodeStorage 兜底生成一次.
     * </p>
     */
    private void generateQrCodeForList(List<SafetyCode> list) {
        for (SafetyCode code : list) {
            if (com.joyfishs.utils.StringUtils.isEmpty(code.getCode())) {
                continue;
            }
            // 已有 URL (新数据) 直接用
            if (com.joyfishs.utils.StringUtils.isNotEmpty(code.getQrCodeData())) {
                code.setQrCode(code.getQrCodeData());
                continue;
            }
            // 旧数据兜底: 生成一次并存到 qrCodeData (供下次列表直接读)
            // 注意: 这里不写回 DB, 因为此方法在 list 请求路径上, 写 DB 会让 list 变慢
            // 真正持久化靠 generateSafetyCode (新数据都带 URL)
            try {
                String url = qrCodeStorage.storeAndGetUrl(code.getCode(), 150, 150, "safety-code");
                if (com.joyfishs.utils.StringUtils.isNotEmpty(url)) {
                    code.setQrCode(url);
                }
            } catch (Exception e) {
                log.warn("Failed to fallback-generate QR for code {}", code.getCode(), e);
            }
        }
    }
    
    /**
     * 批量删除安全码
     */
    @DeleteMapping("/batch")
    @PreAuthorize("@ss.hasPermi('safety:code:delete')")
    @ApiOperation(value = "批量删除安全码")
    @Log(title = "批量删除安全码", businessType = BusinessType.DELETE)
    public AjaxResult<?> batchDelete(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return AjaxResult.error("请选择要删除的安全码");
            }
            
            boolean success = safetyCodeService.removeByIds(ids);
            if (success) {
                return AjaxResult.success("批量删除成功，共删除" + ids.size() + "条记录");
            } else {
                return AjaxResult.error("批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除安全码失败", e);
            return AjaxResult.error("批量删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量更新安全码颜色
     */
    @PutMapping("/batch/color")
    @PreAuthorize("@ss.hasPermi('safety:code:update')")
    @ApiOperation(value = "批量更新安全码颜色")
    @Log(title = "批量更新安全码颜色", businessType = BusinessType.UPDATE)
    public AjaxResult<?> batchUpdateColor(@RequestBody Map<String, Object> params) {
        try {
            String color = (String) params.get("color");
            @SuppressWarnings("unchecked")
            List<Long> userIds = (List<Long>) params.get("userIds");
            
            if (color == null || userIds == null || userIds.isEmpty()) {
                return AjaxResult.error("颜色和用户ID列表不能为空");
            }
            
            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    safetyCodeService.updateColor(userId, color);
                    successCount++;
                } catch (Exception e) {
                    log.warn("更新用户 {} 的安全码颜色失败: {}", userId, e.getMessage());
                }
            }
            
            return AjaxResult.success("批量更新完成，成功更新 " + successCount + "/" + userIds.size() + " 条记录");
        } catch (Exception e) {
            log.error("批量更新安全码颜色失败", e);
            return AjaxResult.error("批量更新失败：" + e.getMessage());
        }
    }

    /**
     * 更新安全码
     */
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('safety:code:update')")
    @ApiOperation(value = "更新安全码")
    @Log(title = "更新安全码", businessType = BusinessType.UPDATE)
    public AjaxResult<?> updateSafetyCode(@PathVariable Long id, @RequestBody @Valid SafetyCode safetyCode) {
        try {
            SafetyCode existing = safetyCodeService.getById(id);
            if (existing == null) {
                return AjaxResult.error("安全码记录不存在");
            }
            safetyCode.setId(id);
            boolean result = safetyCodeService.updateById(safetyCode);
            return result ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
        } catch (Exception e) {
            log.error("更新安全码失败，id: {}", id, e);
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }
}
