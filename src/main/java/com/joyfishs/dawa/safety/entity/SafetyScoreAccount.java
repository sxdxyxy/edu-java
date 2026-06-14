package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 安全积分账户实体类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_safety_score_account", autoResultMap = true)
@ApiModel(value = "SafetyScoreAccount", description = "安全积分账户")
public class SafetyScoreAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    @ApiModelProperty("人员ID")
    private Long personId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("项目部ID")
    private Long projectId;

    @ApiModelProperty("岗位类型（安全积分账户岗位）")
    private String workType;

    @ApiModelProperty("人员工种编码（xm_person.work_type，1-19）")
    private Integer personWorkType;

    @ApiModelProperty("初始积分")
    private Integer initialScore;

    @ApiModelProperty("当前积分")
    private Integer currentScore;

    @ApiModelProperty("年度清零日期")
    private Date annualResetDate;

    @ApiModelProperty("状态")
    private String status;

    // 注: 创建时间/更新时间由下方 create_time/update_time 字段 (V20260618 显式 @TableField 注入) 提供.
    //   V20260621 已 drop 掉本表的 created_at/updated_at 冗余列, 实体里
    //   不能保留带 @TableField("created_at"/"updated_at") 的字段 — 否则
    //   MyBatis-Plus SELECT 列里会引用不存在的列 → Unknown column 500.

    @TableField("create_by")
    private Long createBy;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_by")
    private Long updateBy;
    @TableField("update_time")
    private Date updateTime;
    @TableField("is_delete")
    private Integer isDelete;
    @TableField("delete_by")
    private Long deleteBy;
    @TableField("delete_time")
    private Date deleteTime;
    @TableField("delete_reason")
    private String deleteReason;
    @TableField("remark")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty("安全码颜色")
    private String color;
}
