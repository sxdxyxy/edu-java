package com.joyfishs.dawa.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 实操考核实体类
 *
 * @author safe-edu
 * @since 2026-03-29
 */
@Data
@TableName(value = "practical_assessments", autoResultMap = true)
public class PracticalAssessment {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 关联项目 ID
     */
    private Long projectId;

    /**
     * 关联项目部 ID（组织机构）
     */
    private Long orgId;

    /**
     * 考核类型：safety/equipment/emergency/skill
     */
    private String assessmentType;

    /**
     * 考核日期
     */
    private Date assessmentDate;

    /**
     * 考核地点
     */
    private String location;

    /**
     * 考评员 ID
     */
    private Long examinerId;

    /**
     * 得分（百分制）
     */
    private Integer score;

    /**
     * 考核结果：pass/fail/excellent
     */
    private String result;

    /**
     * 考核视频 URL
     */
    private String videoUrl;

    /**
     * 证据照片 JSON
     */
    private String evidencePhotos;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    // ===== 扩展字段（从 sys_user 关联查询）=====
    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 用户手机号
     */
    @TableField(exist = false)
    private String phone;

    /**
     * 身份证号
     */
    @TableField(exist = false)
    private String idCardNo;

    /**
     * 考评员姓名
     */
    @TableField(exist = false)
    private String examinerName;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    private String projectName;

    // 手动添加 getter 和 setter 方法，以确保兼容性
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

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getExaminerId() {
        return examinerId;
    }

    public void setExaminerId(Long examinerId) {
        this.examinerId = examinerId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getEvidencePhotos() {
        return evidencePhotos;
    }

    public void setEvidencePhotos(String evidencePhotos) {
        this.evidencePhotos = evidencePhotos;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getExaminerName() {
        return examinerName;
    }

    public void setExaminerName(String examinerName) {
        this.examinerName = examinerName;
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
