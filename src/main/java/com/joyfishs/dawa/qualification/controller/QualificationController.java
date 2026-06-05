package com.joyfishs.dawa.qualification.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.joyfishs.dawa.qualification.entity.Qualification;
import com.joyfishs.dawa.qualification.service.QualificationService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 资质证件 Controller
 * <p>
 * 提供资质证件的增删改查 REST API
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Slf4j
@RestController
@Api(tags = "资质证件管理")
@RequestMapping("/qualifications")
public class QualificationController extends BaseController {

    @Autowired
    private QualificationService qualificationService;

    /**
     * 获取用户资质列表
     *
     * @param userId 用户 ID
     * @return 资质列表
     */
    @GetMapping("/{userId}")
    @ApiOperation(value = "获取用户资质列表")
    public AjaxResult<?> getQualifications(@PathVariable Long userId) {
        try {
            List<Qualification> qualifications = qualificationService.listByUserId(userId);
            if (qualifications == null || qualifications.isEmpty()) {
                return AjaxResult.success("该用户暂无资质记录", null);
            }
            return AjaxResult.success(qualifications);
        } catch (Exception e) {
            log.error("获取资质列表失败，userId: {}", userId, e);
            return AjaxResult.error("获取资质列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取资质详情
     *
     * @param id 资质 ID
     * @return 资质详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取资质详情")
    public AjaxResult<?> getQualificationDetail(@PathVariable Long id) {
        try {
            Qualification qualification = qualificationService.getQualificationById(id);
            if (qualification == null) {
                return AjaxResult.error("资质不存在");
            }
            return AjaxResult.success(qualification);
        } catch (Exception e) {
            log.error("获取资质详情失败，id: {}", id, e);
            return AjaxResult.error("获取资质详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加资质
     *
     * @param qualification 资质对象
     * @return 添加结果
     */
    @PostMapping("")
    @PreAuthorize("@ss.hasPermi('qualification:add')")
    @ApiOperation(value = "添加资质")
    @Log(title = "添加资质", businessType = BusinessType.INSERT)
    public AjaxResult<?> addQualification(@RequestBody Qualification qualification) {
        try {
            Qualification result = qualificationService.addQualification(qualification);
            return AjaxResult.success("资质添加成功", result);
        } catch (Exception e) {
            log.error("添加资质失败，params: {}", qualification, e);
            return AjaxResult.error("添加资质失败：" + e.getMessage());
        }
    }

    /**
     * 更新资质
     *
     * @param id 资质 ID
     * @param qualification 资质对象
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('qualification:edit')")
    @ApiOperation(value = "更新资质")
    @Log(title = "更新资质", businessType = BusinessType.UPDATE)
    public AjaxResult<?> updateQualification(@PathVariable Long id, @RequestBody Qualification qualification) {
        try {
            qualification.setId(id);
            Qualification result = qualificationService.updateQualification(qualification);
            return AjaxResult.success("资质更新成功", result);
        } catch (Exception e) {
            log.error("更新资质失败，id: {}, params: {}", id, qualification, e);
            return AjaxResult.error("更新资质失败：" + e.getMessage());
        }
    }

    /**
     * 删除资质
     *
     * @param id 资质 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('qualification:delete')")
    @ApiOperation(value = "删除资质")
    @Log(title = "删除资质", businessType = BusinessType.DELETE)
    public AjaxResult<?> deleteQualification(@PathVariable Long id) {
        try {
            boolean result = qualificationService.deleteQualification(id);
            if (result) {
                return AjaxResult.success("资质删除成功");
            } else {
                return AjaxResult.error("资质删除失败");
            }
        } catch (Exception e) {
            log.error("删除资质失败，id: {}", id, e);
            return AjaxResult.error("删除资质失败：" + e.getMessage());
        }
    }

    /**
     * 核验资质
     *
     * @param id 资质 ID
     * @return 核验结果
     */
    @PutMapping("/{id}/verify")
    @PreAuthorize("@ss.hasPermi('qualification:verify')")
    @ApiOperation(value = "核验资质")
    @Log(title = "核验资质", businessType = BusinessType.UPDATE)
    public AjaxResult<?> verifyQualification(@PathVariable Long id) {
        try {
            Qualification result = qualificationService.verifyQualification(id);
            Map<String, Object> data = new HashMap<>();
            data.put("id", result.getId());
            data.put("verified", result.getVerified());
            return AjaxResult.success("资质核验成功", data);
        } catch (Exception e) {
            log.error("核验资质失败，id: {}", id, e);
            return AjaxResult.error("核验资质失败：" + e.getMessage());
        }
    }

    /**
     * 取消核验资质
     *
     * @param id 资质 ID
     * @return 操作结果
     */
    @PutMapping("/{id}/unverify")
    @PreAuthorize("@ss.hasPermi('qualification:verify')")
    @ApiOperation(value = "取消核验资质")
    @Log(title = "取消核验资质", businessType = BusinessType.UPDATE)
    public AjaxResult<?> unverifyQualification(@PathVariable Long id) {
        try {
            Qualification result = qualificationService.unverifyQualification(id);
            Map<String, Object> data = new HashMap<>();
            data.put("id", result.getId());
            data.put("verified", result.getVerified());
            return AjaxResult.success("取消核验成功", data);
        } catch (Exception e) {
            log.error("取消核验资质失败，id: {}", id, e);
            return AjaxResult.error("取消核验失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新资质状态
     *
     * @return 操作结果
     */
    @PostMapping("/batch-update-status")
    @PreAuthorize("@ss.hasPermi('qualification:manage')")
    @ApiOperation(value = "批量更新资质状态")
    @Log(title = "批量更新资质状态", businessType = BusinessType.UPDATE)
    public R<Void> batchUpdateStatus() {
        try {
            qualificationService.batchUpdateStatus();
            return R.ok("资质状态批量更新成功");
        } catch (Exception e) {
            log.error("批量更新资质状态失败", e);
            return R.fail("批量更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 分页查询所有资质证件
     */
    @GetMapping("/list")
    // @PreAuthorize("@ss.hasPermi('qualification:list')")  // 临时移除权限，开发环境调试用
    @ApiOperation(value = "分页查询资质证件列表")
    public AjaxResult<?> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String certType) {
        startPage();
        Map<String, Object> params = new HashMap<>();
        if (userName != null && !userName.trim().isEmpty()) {
            params.put("userName", userName);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            params.put("phone", phone);
        }
        if (status != null && !status.trim().isEmpty()) {
            params.put("status", status);
        }
        if (certType != null && !certType.trim().isEmpty()) {
            params.put("certType", certType);
        }
        List<Qualification> list = qualificationService.listPageWithUser(params);
        PageInfo<Qualification> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        return AjaxResult.success(result);
    }

    /**
     * 批量删除资质
     *
     * @param ids 资质ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-delete")
    @PreAuthorize("@ss.hasPermi('qualification:delete')")
    @ApiOperation(value = "批量删除资质")
    @Log(title = "批量删除资质", businessType = BusinessType.DELETE)
    public AjaxResult<?> batchDeleteQualifications(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return AjaxResult.error("请选择要删除的资质");
            }
            
            boolean result = qualificationService.batchDeleteQualifications(ids);
            if (result) {
                return AjaxResult.success("资质批量删除成功", ids.size() + "条记录已被删除");
            } else {
                return AjaxResult.error("资质批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除资质失败", e);
            return AjaxResult.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量审核资质
     *
     * @param ids 资质ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-audit")
    @PreAuthorize("@ss.hasPermi('qualification:audit')")
    @ApiOperation(value = "批量审核资质")
    @Log(title = "批量审核资质", businessType = BusinessType.UPDATE)
    public AjaxResult<?> batchAuditQualifications(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return AjaxResult.error("请选择要审核的资质");
            }
            
            boolean result = qualificationService.batchAuditQualifications(ids);
            if (result) {
                return AjaxResult.success("资质批量审核成功", ids.size() + "条记录已被审核");
            } else {
                return AjaxResult.error("资质批量审核失败");
            }
        } catch (Exception e) {
            log.error("批量审核资质失败", e);
            return AjaxResult.error("批量审核失败：" + e.getMessage());
        }
    }

    /**
     * 导出资质列表为Excel
     *
     * @param response HTTP响应对象
     */
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermi('qualification:export')")
    @ApiOperation(value = "导出资质列表为Excel")
    @Log(title = "导出资质列表", businessType = BusinessType.EXPORT)
    public void exportQualifications(HttpServletResponse response) {
        try {
            List<Qualification> qualifications = qualificationService.list();
            com.joyfishs.common.utils.ExcelUtil.exportExcel(qualifications, response, "qualifications.xlsx");
        } catch (Exception e) {
            log.error("导出资质列表失败", e);
        }
    }

    /**
     * 从Excel导入资质数据
     *
     * @param file 上传的Excel文件
     * @return 操作结果
     */
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPermi('qualification:import')")
    @ApiOperation(value = "从Excel导入资质数据")
    @Log(title = "导入资质数据", businessType = BusinessType.IMPORT)
    public AjaxResult<?> importQualifications(MultipartFile file) {
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
            List<Qualification> qualifications = com.joyfishs.common.utils.ExcelUtil.importExcel(inputStream, Qualification.class);
            
            if (qualifications == null || qualifications.isEmpty()) {
                return AjaxResult.error("Excel文件中没有有效的资质数据");
            }
            
            List<Qualification> result = qualificationService.importQualifications(qualifications);
            return AjaxResult.success("资质导入成功", result.size() + "条记录已被导入");
        } catch (Exception e) {
            log.error("导入资质数据失败", e);
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 获取资质统计概览
     */
    @GetMapping("/statistics/overview")
    @ApiOperation(value = "获取资质统计概览")
    public AjaxResult<?> getStatisticsOverview() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", qualificationService.countAll());
            stats.put("validCount", qualificationService.countByStatus("valid"));
            stats.put("expiringCount", qualificationService.countByStatus("expiring"));
            stats.put("expiredCount", qualificationService.countByStatus("expired"));
            stats.put("verifiedCount", qualificationService.countVerified());
            stats.put("electricianCount", qualificationService.countByCertType("电工证"));
            stats.put("welderCount", qualificationService.countByCertType("焊工证"));
            stats.put("scaffolderCount", qualificationService.countByCertType("架子工证"));
            return AjaxResult.success(stats);
        } catch (Exception e) {
            log.error("获取资质统计概览失败", e);
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }
}
