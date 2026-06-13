package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 安全违章申诉记录实体类
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_safety_appeal_record", autoResultMap = true)
@ApiModel(value = "AppealRecord", description = "安全违章申诉记录")
public class AppealRecord {

    /** 审批结果：待审批 */
    public static final String RESULT_PENDING = "pending";

    /** 审批结果：通过 */
    public static final String RESULT_APPROVED = "approved";

    /** 审批结果：驳回 */
    public static final String RESULT_REJECTED = "rejected";

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("关联违章记录ID")
    @TableField("violation_record_id")
    private Long violationRecordId;

    @ApiModelProperty("申诉人（作业人员）ID")
    @TableField("person_id")
    private Long personId;

    @ApiModelProperty("申诉理由")
    @TableField("appeal_reason")
    private String appealReason;

    @ApiModelProperty("申诉补充证据照片URL列表（JSON数组）")
    @TableField("appeal_evidence")
    private String appealEvidence;

    @ApiModelProperty("申诉时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("appeal_time")
    private Date appealTime;

    @ApiModelProperty("审批人ID（安全主管）")
    @TableField("reviewer_id")
    private Long reviewerId;

    @ApiModelProperty("审批结果 pending/approved/rejected")
    @TableField("review_result")
    private String reviewResult;

    @ApiModelProperty("审批意见")
    @TableField("review_comment")
    private String reviewComment;

    @ApiModelProperty("审批时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("review_time")
    private Date reviewTime;

    @ApiModelProperty("返还积分分值")
    @TableField("score_restored")
    private Integer scoreRestored;

    @ApiModelProperty("积分返还时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("score_restored_at")
    private Date scoreRestoredAt;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("updated_at")
    private Date updatedAt;

    @TableField("create_by")
    private Long createBy;

    @TableField("update_by")
    private Long updateBy;

    @TableField("is_delete")
    private Integer isDelete;
}
