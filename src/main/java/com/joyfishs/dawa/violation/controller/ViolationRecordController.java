package com.joyfishs.dawa.violation.controller;

import com.github.pagehelper.PageInfo;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import com.joyfishs.dawa.violation.dto.ViolationAndDeductRequest;
import com.joyfishs.dawa.violation.dto.ViolationAndDeductResult;
import com.joyfishs.dawa.violation.dto.ViolationRecordResult;
import com.joyfishs.dawa.violation.service.ViolationRecordService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.common.utils.ExcelUtil;
import com.joyfishs.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 违章记录控制器
 * 
 * @author safe-edu
 * @since 2026-03-29
 */
@RestController
@RequestMapping("/violation")
public class ViolationRecordController extends BaseController {

    @Autowired
    private ViolationRecordService violationRecordService;

    /**
     * 查询用户的违章记录列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> listByUser(@PathVariable Long userId) {
        List<ViolationRecord> list = violationRecordService.listByUserId(userId);
        Integer totalScore = violationRecordService.getTotalScore(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalScore", totalScore);
        return AjaxResult.success(result);
    }

    /**
     * 查询项目的违章记录列表
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> listByProject(@PathVariable Long projectId) {
        List<ViolationRecord> list = violationRecordService.listByProjectId(projectId);
        return AjaxResult.success(list);
    }

    /**
     * 查询待处理的违章记录
     */
    @GetMapping("/pending")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> listPending() {
        List<ViolationRecord> list = violationRecordService.listPending();
        return AjaxResult.success(list);
    }

    /**
     * 查询严重违章记录
     */
    @GetMapping("/severe")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> listSevere() {
        List<ViolationRecord> list = violationRecordService.listSevereViolations();
        return AjaxResult.success(list);
    }

    /**
     * 违章即扣分（Phase 2 核心接口）
     * <p>
     * 违章录入 + 自动扣分 + 并行触发后续影响（闸机/通知/再培训/准入锁）
     * </p>
     */
    @PostMapping("/violation-and-deduct")
    @PreAuthorize("@ss.hasPermi('violation:edit')")
    public AjaxResult<?> violationAndDeduct(
            @RequestParam Long personId,
            @RequestParam Long projectId,
            @RequestParam String violationCode,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String evidencePhotos,
            @RequestParam(required = false) String location) {
        try {
            Long operatorId = SecurityUtil.getUserId();
            ViolationAndDeductResult result = violationRecordService.recordViolationAndDeduct(
                    personId, projectId, violationCode, description, evidencePhotos, location, operatorId);
            if (result == null || !result.getSuccess()) {
                return AjaxResult.error(result != null ? result.getErrorMessage() : "处理失败");
            }
            return AjaxResult.success("违章已记录，积分已扣减", result);
        } catch (Exception e) {
            return AjaxResult.error("违章记录失败：" + e.getMessage());
        }
    }

    /**
     * 违章即扣分（JSON Body 版本,前端 axios 传 JSON 时用这个）
     * <p>
     * 入参与 query 版本一致,只是走 application/json。
     * </p>
     */
    @PostMapping("/violation-and-deduct-json")
    @PreAuthorize("@ss.hasPermi('violation:edit')")
    public AjaxResult<?> violationAndDeductJson(@RequestBody ViolationAndDeductRequest request) {
        try {
            if (request == null) {
                return AjaxResult.error("请求体不能为空");
            }
            Long operatorId = SecurityUtil.getUserId();
            ViolationAndDeductResult result = violationRecordService.recordViolationAndDeduct(
                    request.getPersonId(),
                    request.getProjectId(),
                    request.getViolationCode(),
                    request.getDescription(),
                    request.getEvidencePhotos(),
                    request.getLocation(),
                    operatorId);
            if (result == null || !result.getSuccess()) {
                return AjaxResult.error(result != null ? result.getErrorMessage() : "处理失败");
            }
            return AjaxResult.success("违章已记录，积分已扣减", result);
        } catch (Exception e) {
            return AjaxResult.error("违章记录失败：" + e.getMessage());
        }
    }

    /**
     * 记录违章行为（违章录入后自动扣分、可能触发再培训）
     */
    @PostMapping("/record")
    @PreAuthorize("@ss.hasPermi('violation:edit')")
    public AjaxResult<?> addViolation(@RequestBody ViolationRecord violation) {
        try {
            ViolationRecordResult result = violationRecordService.recordViolation(violation);
            return AjaxResult.success("违章记录成功", result);
        } catch (Exception e) {
            return AjaxResult.error("记录失败：" + e.getMessage());
        }
    }

    /**
     * 处理违章记录
     */
    @PostMapping("/process/{violationId}")
    @PreAuthorize("@ss.hasPermi('violation:process')")
    public AjaxResult<?> processViolation(
            @PathVariable Long violationId,
            @RequestParam Long handlerId,
            @RequestParam String status) {
        try {
            ViolationRecord updated = violationRecordService.processViolation(violationId, handlerId, status);
            return AjaxResult.success("处理成功", updated);
        } catch (Exception e) {
            return AjaxResult.error("处理失败：" + e.getMessage());
        }
    }

    /**
     * 发起申诉（作业人员操作）
     */
    @PostMapping("/appeal/{violationId}")
    @PreAuthorize("@ss.hasPermi('violation:edit')")
    public AjaxResult<?> appealViolation(
            @PathVariable Long violationId,
            @RequestParam String appealReason,
            @RequestParam(required = false) String appealEvidence) {
        try {
            ViolationRecord updated = violationRecordService.appealViolation(violationId, appealReason, appealEvidence);
            return AjaxResult.success("申诉提交成功", updated);
        } catch (Exception e) {
            return AjaxResult.error("申诉失败：" + e.getMessage());
        }
    }

    /**
     * 审核违章申诉
     */
    @PostMapping("/review-appeal/{violationId}")
    @PreAuthorize("@ss.hasPermi('violation:review')")
    @Log(title = "审核违章申诉", businessType = BusinessType.UPDATE)
    public AjaxResult<?> reviewAppeal(
            @PathVariable Long violationId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks) {
        try {
            Long handlerId = SecurityUtil.getUserId();
            ViolationRecord updated = violationRecordService.reviewAppeal(violationId, approved, handlerId, remarks);
            return AjaxResult.success("审核操作成功", updated);
        } catch (Exception e) {
            return AjaxResult.error("审核操作失败：" + e.getMessage());
        }
    }

    /**
     * 删除违章记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('violation:delete')")
    public AjaxResult<?> deleteViolation(@PathVariable Long id) {
        try {
            violationRecordService.removeById(id);
            return AjaxResult.success("违章记录删除成功");
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取违章记录详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> getViolationDetail(@PathVariable Long id) {
        try {
            ViolationRecord record = violationRecordService.getById(id);
            if (record == null) {
                return AjaxResult.error("违章记录不存在");
            }
            return AjaxResult.success(record);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询所有违章记录
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long orgId) {
        startPage();
        Map<String, Object> params = new HashMap<>();
        if (userName != null && !userName.trim().isEmpty()) {
            params.put("userName", userName);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            params.put("phone", phone);
        }
        if (severity != null && !severity.trim().isEmpty()) {
            params.put("severity", severity);
        }
        if (status != null && !status.trim().isEmpty()) {
            params.put("status", status);
        }
        if (projectId != null) {
            params.put("projectId", projectId);
        }
        if (orgId != null) {
            params.put("orgId", orgId);
        }
        List<ViolationRecord> list = violationRecordService.listPageWithUser(params);
        PageInfo<ViolationRecord> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        return AjaxResult.success(result);
    }

    /**
     * 批量删除违章记录
     */
    @PostMapping("/batch-delete")
    @PreAuthorize("@ss.hasPermi('violation:delete')")
    @Log(title = "违章记录", businessType = BusinessType.DELETE)
    public AjaxResult<?> batchDeleteViolations(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return AjaxResult.error("请选择要删除的违章记录");
            }
            
            boolean result = violationRecordService.batchDeleteViolations(ids);
            if (result) {
                return AjaxResult.success("违章记录批量删除成功", ids.size() + "条记录已被删除");
            } else {
                return AjaxResult.error("违章记录批量删除失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量处理违章
     */
    @PostMapping("/batch-process")
    @PreAuthorize("@ss.hasPermi('violation:process')")
    @Log(title = "违章记录", businessType = BusinessType.UPDATE)
    public AjaxResult<?> batchProcessViolations(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) params.get("ids");
            String status = (String) params.get("status");
            Long handlerId = (Long) params.get("handlerId");
            
            if (ids == null || ids.isEmpty()) {
                return AjaxResult.error("请选择要处理的违章记录");
            }
            
            if (status == null || status.trim().isEmpty()) {
                return AjaxResult.error("请指定处理状态");
            }
            
            if (handlerId == null) {
                return AjaxResult.error("请指定处理人");
            }
            
            boolean result = violationRecordService.batchProcessViolations(ids, handlerId, status);
            if (result) {
                return AjaxResult.success("违章记录批量处理成功", ids.size() + "条记录已被处理");
            } else {
                return AjaxResult.error("违章记录批量处理失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("批量处理失败：" + e.getMessage());
        }
    }

    /**
     * 从Excel导入违章数据
     */
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermi('violation:import')")
    @Log(title = "违章记录", businessType = BusinessType.IMPORT)
    public AjaxResult<?> importViolations(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("请选择要上传的文件");
            }
            
            // 验证文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                return AjaxResult.error("仅支持Excel文件格式(.xlsx或.xls)");
            }
            
            InputStream inputStream = file.getInputStream();
            List<ViolationRecord> violations = ExcelUtil.importExcel(inputStream, ViolationRecord.class);
            
            if (violations == null || violations.isEmpty()) {
                return AjaxResult.error("Excel文件中没有有效的违章数据");
            }
            
            List<ViolationRecord> result = violationRecordService.importViolations(violations);
            return AjaxResult.success("违章记录导入成功", result.size() + "条记录已被导入");
        } catch (Exception e) {
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 获取违章统计概览
     */
    @GetMapping("/statistics/overview")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> getOverviewStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", violationRecordService.countAll());
            stats.put("pendingCount", violationRecordService.listPending().size());
            stats.put("processedCount", violationRecordService.countByStatus("processed"));
            stats.put("appealedCount", violationRecordService.countByStatus("appealed"));
            stats.put("minorCount", violationRecordService.countBySeverity("minor"));
            stats.put("majorCount", violationRecordService.countBySeverity("major"));
            stats.put("criticalCount", violationRecordService.countBySeverity("critical"));
            return AjaxResult.success(stats);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 更新违章记录
     */
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('violation:edit')")
    @Log(title = "违章记录", businessType = BusinessType.UPDATE)
    public AjaxResult<?> updateViolation(
            @PathVariable Long id,
            @RequestBody ViolationRecord violation) {
        try {
            ViolationRecord existing = violationRecordService.getById(id);
            if (existing == null) {
                return AjaxResult.error("违章记录不存在");
            }
            violation.setId(id);
            ViolationRecord updated = violationRecordService.updateViolation(violation);
            return AjaxResult.success("更新成功", updated);
        } catch (Exception e) {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 查询用户的累计记分
     */
    @GetMapping("/user/{userId}/total-score")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> getTotalScore(@PathVariable Long userId) {
        try {
            Integer totalScore = violationRecordService.getTotalScore(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("totalScore", totalScore);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取违章记分排行榜 TOP10
     */
    @GetMapping("/top10")
    @PreAuthorize("@ss.hasPermi('violation:list')")
    public AjaxResult<?> getTop10Violators(@RequestParam(required = false) Long orgId) {
        try {
            List<Map<String, Object>> top10 = violationRecordService.getTop10ViolatorsByScore(orgId);
            return AjaxResult.success(top10);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 导出违章记录为 Excel（带筛选条件）
     */
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('violation:export')")
    @Log(title = "违章记录", businessType = BusinessType.EXPORT)
    public void exportViolations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            HttpServletResponse response) {
        try {
            java.util.List<Long> managedOrgIds = SecurityUtil.getManagedOrgIds();
            List<ViolationRecord> violations = violationRecordService.listPage(userId, projectId, severity, status, managedOrgIds);
            ExcelUtil.exportExcel(violations, response, "违章记录_" + System.currentTimeMillis() + ".xlsx");
        } catch (Exception e) {
            System.err.println("导出违章记录失败：" + e.getMessage());
        }
    }
}
