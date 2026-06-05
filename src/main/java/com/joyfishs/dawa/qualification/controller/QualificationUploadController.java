package com.joyfishs.dawa.qualification.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.joyfishs.dawa.qualification.entity.Qualification;
import com.joyfishs.dawa.qualification.service.QualificationService;
import com.joyfishs.dawa.qualification.util.QualificationFileUploadUtil;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 资质证件上传与审核 Controller
 * <p>
 * 实现资质证件上传、审核等功能（按照任务需求）
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-30
 */
@Slf4j
@RestController
@Api(tags = "资质证件上传与审核")
@RequestMapping("/qualifications")
public class QualificationUploadController extends BaseController {

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private QualificationFileUploadUtil fileUploadUtil;

    /**
     * 上传资质证件
     *
     * @param file 证件照片文件（JPG/PNG）
     * @param userId 用户ID
     * @param certType 证件类型
     * @param certNo 证件编号
     * @param issueDate 发证日期
     * @param expiryDate 到期日期
     * @param issuingAuthority 发证机关
     * @return 上传结果
     */
    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermi('qualification:upload')")
    @ApiOperation(value = "上传资质证件")
    @Log(title = "上传资质证件", businessType = BusinessType.INSERT)
    public AjaxResult<?> uploadQualification(
            @RequestPart("file") MultipartFile file,
            @RequestParam Long userId,
            @RequestParam String certType,
            @RequestParam String certNo,
            @RequestParam(required = false) String issueDate,
            @RequestParam String expiryDate,
            @RequestParam String issuingAuthority) {
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("请选择要上传的文件");
            }

            // 保存文件
            String storedFilePath;
            try {
                storedFilePath = fileUploadUtil.saveQualificationFile(file, userId, certType);
            } catch (IOException e) {
                log.error("保存文件失败", e);
                return AjaxResult.error("文件保存失败：" + e.getMessage());
            }

            // 创建资质对象
            Qualification qualification = new Qualification();
            qualification.setUserId(userId);
            qualification.setCertType(certType);
            qualification.setCertNo(certNo);
            if (issueDate != null && !issueDate.trim().isEmpty()) {
                qualification.setIssueDate(java.time.LocalDate.parse(issueDate));
            }
            qualification.setExpiryDate(java.time.LocalDate.parse(expiryDate));
            qualification.setIssuingAuthority(issuingAuthority);
            qualification.setCertPhotoUrl(storedFilePath); // 存储的文件路径
            qualification.setStatus(Qualification.STATUS_PENDING); // 设置为待审核状态
            qualification.setVerified(false); // 初始未核验

            // 保存资质信息
            Qualification savedQualification = qualificationService.addQualification(qualification);

            Map<String, Object> result = new HashMap<>();
            result.put("qualificationId", savedQualification.getId());
            result.put("filePath", storedFilePath);
            result.put("accessUrl", fileUploadUtil.buildFileUrl(storedFilePath)); // 可访问的URL
            result.put("qualificationInfo", savedQualification);

            return AjaxResult.success("资质证件上传成功", result);
        } catch (Exception e) {
            log.error("上传资质证件失败", e);
            return AjaxResult.error("上传资质证件失败：" + e.getMessage());
        }
    }

    /**
     * 待审核资质列表
     *
     * @return 待审核资质列表
     */
    @GetMapping("/pending-review")
    @PreAuthorize("@ss.hasPermi('qualification:review')")
    @ApiOperation(value = "获取待审核资质列表")
    public AjaxResult<?> getPendingReviewList() {
        try {
            // 查询待审核的资质列表
            List<Qualification> pendingList = qualificationService.getPendingQualifications();
            
            return AjaxResult.success(pendingList);
        } catch (Exception e) {
            log.error("获取待审核资质列表失败", e);
            return AjaxResult.error("获取待审核资质列表失败：" + e.getMessage());
        }
    }

    /**
     * 审核资质
     *
     * @param reviewData 审核数据（id, approved）
     * @return 审核结果
     */
    @PostMapping("/review")
    @PreAuthorize("@ss.hasPermi('qualification:review')")
    @ApiOperation(value = "审核资质")
    @Log(title = "审核资质", businessType = BusinessType.UPDATE)
    public AjaxResult<?> reviewQualification(@RequestBody Map<String, Object> reviewData) {
        try {
            Long id = (Long) reviewData.get("id");
            Boolean approved = (Boolean) reviewData.get("approved");

            if (id == null) {
                return AjaxResult.error("资质ID不能为空");
            }
            if (approved == null) {
                return AjaxResult.error("审核结果不能为空");
            }

            Qualification qualification = qualificationService.getQualificationById(id);
            if (qualification == null) {
                return AjaxResult.error("资质不存在");
            }

            if (approved) {
                // 审核通过：设置为已核验，状态为有效
                qualification.setVerified(true);
                qualification.setStatus(Qualification.STATUS_VALID); // 设置为有效状态
            } else {
                // 审核驳回：设置为驳回状态
                qualification.setStatus(Qualification.STATUS_REJECTED); // 设置为驳回状态
                qualification.setVerified(false); // 未核验
            }

            qualificationService.updateQualification(qualification);

            String action = approved ? "通过" : "驳回";
            return AjaxResult.success("资质审核" + action + "成功");
        } catch (Exception e) {
            log.error("审核资质失败", e);
            return AjaxResult.error("审核资质失败：" + e.getMessage());
        }
    }
}