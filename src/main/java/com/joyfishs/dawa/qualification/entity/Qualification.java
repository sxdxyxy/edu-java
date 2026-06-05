package com.joyfishs.dawa.qualification.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 资质证件实体类
 * <p>
 * 用于管理用户的资质证书，如电工证、焊工证等
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Qualification", description = "资质证件")
@TableName("qualifications")
public class Qualification extends BaseEntity {

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键 ID")
    private Long id;

    /**
     * 关联用户 ID
     */
    @TableField("user_id")
    @ApiModelProperty(value = "关联用户 ID")
    private Long userId;

    /**
     * 关联项目 ID
     */
    @TableField("project_id")
    @ApiModelProperty(value = "关联项目 ID")
    private Long projectId;

    /**
     * 证件类型：电工证/焊工证/高空作业证等
     */
    @TableField("cert_type")
    @ApiModelProperty(value = "证件类型：电工证/焊工证/高空作业证等", example = "电工证")
    private String certType;

    /**
     * 证件编号
     */
    @TableField("cert_no")
    @ApiModelProperty(value = "证件编号", example = "DZ20230001")
    private String certNo;

    /**
     * 发证日期
     */
    @TableField("issue_date")
    @ApiModelProperty(value = "发证日期")
    private LocalDate issueDate;

    /**
     * 到期日期
     */
    @TableField("expiry_date")
    @ApiModelProperty(value = "到期日期")
    private LocalDate expiryDate;

    /**
     * 发证机关
     */
    @TableField("issuing_authority")
    @ApiModelProperty(value = "发证机关", example = "应急管理部")
    private String issuingAuthority;

    /**
     * 证件照片 URL
     */
    @TableField("cert_photo_url")
    @ApiModelProperty(value = "证件照片 URL")
    private String certPhotoUrl;

    /**
     * 状态：valid/expiring/expired
     */
    @TableField("status")
    @ApiModelProperty(value = "状态：valid-有效/expiring-即将过期/expired-过期", example = "valid")
    private String status;

    /**
     * 是否核验：0-未核验，1-已核验
     */
    @TableField("verified")
    @ApiModelProperty(value = "是否核验：0-未核验，1-已核验", example = "false")
    private Boolean verified;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 持有人姓名（关联用户表获取）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "持有人姓名")
    private String holderName;

    /**
     * 用户手机号（关联用户表获取）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户手机号")
    private String phone;

    /**
     * 身份证号（关联用户表获取）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "身份证号")
    private String idCardNo;

    /**
     * 组织机构ID
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "组织机构ID")
    private Long orgId;

    /**
     * 项目名称（扩展字段，关联项目表获取）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * 资质类型（扩展字段，同 certType）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "资质类型")
    private String qualificationType;

    /**
     * 资质名称（扩展字段，同 certType）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "资质名称")
    private String qualificationName;

    /**
     * 证书编号（扩展字段，同 certNo）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "证书编号")
    private String certificateNumber;

    /**
     * 有效期状态（扩展字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "有效期状态：valid/expiring_soon/expired")
    private String expiryStatus;

    /**
     * 审核状态（扩展字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "审核状态：pending/approved/rejected")
    private String auditStatus;

    /**
     * 附件 URL（扩展字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "附件 URL")
    private String attachmentUrl;

    /**
     * 备注（扩展字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 状态枚举值
     */
    public static final String STATUS_VALID = "valid";
    public static final String STATUS_EXPIRING = "expiring";
    public static final String STATUS_EXPIRED = "expired";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_REJECTED = "rejected";

    /**
     * 常见证件类型
     */
    public static final String CERT_TYPE_ELECTRICIAN = "电工证";
    public static final String CERT_TYPE_WELDER = "焊工证";
    public static final String CERT_TYPE_HEIGHT_WORK = "高空作业证";
    public static final String CERT_TYPE_SAFETY_OFFICER = "安全员证";
    public static final String CERT_TYPE_SPECIAL_OPERATION = "特种作业操作证";

    /**
     * 获取证件名称
     */
    public String getQualificationName() {
        return this.qualificationName != null ? this.qualificationName : this.certType;
    }

    /**
     * 获取距离到期天数
     */
    public Integer getDaysToExpiry() {
        if (this.expiryDate == null) {
            return null;
        }
        return (int) java.time.LocalDate.now().until(this.expiryDate, java.time.temporal.ChronoUnit.DAYS);
    }

    /**
     * 检查证件是否即将过期
     */
    public boolean isExpiringSoon(int days) {
        return getDaysToExpiry() != null && getDaysToExpiry() <= days && getDaysToExpiry() > 0;
    }

    /**
     * 检查证件是否已过期
     */
    public boolean isExpired() {
        return getDaysToExpiry() != null && getDaysToExpiry() < 0;
    }

    /**
     * 检查证件是否有效
     */
    public boolean isValid() {
        return !isExpired();
    }

    // 手动添加getter和setter方法，以确保兼容性
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getCertPhotoUrl() {
        return certPhotoUrl;
    }

    public void setCertPhotoUrl(String certPhotoUrl) {
        this.certPhotoUrl = certPhotoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(String qualificationType) {
        this.qualificationType = qualificationType;
    }

    public void setQualificationName(String qualificationName) {
        this.qualificationName = qualificationName;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getExpiryStatus() {
        return expiryStatus;
    }

    public void setExpiryStatus(String expiryStatus) {
        this.expiryStatus = expiryStatus;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
