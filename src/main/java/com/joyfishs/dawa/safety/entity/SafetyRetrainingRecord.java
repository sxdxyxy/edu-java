package com.joyfishs.dawa.safety.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 安全再培训记录实体类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_safety_retraining_record", autoResultMap = true)
@ApiModel(value = "SafetyRetrainingRecord", description = "安全再培训记录")
public class SafetyRetrainingRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 状态：待处理
     */
    public static final String STATUS_PENDING = "pending";

    /**
     * 状态：进行中
     */
    public static final String STATUS_ONGOING = "ongoing";

    /**
     * 状态：已完成
     */
    public static final String STATUS_COMPLETED = "completed";

    /**
     * 状态：未通过
     */
    public static final String STATUS_FAILED = "failed";

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    /**
     * 人员ID
     */
    @ApiModelProperty("人员ID")
    private Long personId;

    /**
     * 触发的违章记录ID
     */
    @ApiModelProperty("触发的违章记录ID")
    private Long violationRecordId;

    /**
     * 违章代码
     */
    @ApiModelProperty("违章代码")
    private String violationCode;

    /**
     * 培训课程ID
     */
    @ApiModelProperty("培训课程ID")
    private Long trainingCourseId;

    /**
     * 培训学时
     */
    @ApiModelProperty("培训学时")
    private Integer trainingHours;

    /**
     * 状态（pending/ongoing/completed/failed）
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 开始日期
     */
    @ApiModelProperty("开始日期")
    private Date startDate;

    /**
     * 完成日期
     */
    @ApiModelProperty("完成日期")
    private Date endDate;

    /**
     * 恢复的积分分值
     */
    @ApiModelProperty("恢复的积分分值")
    private Integer scoreRestored;

    /**
     * 培训计划ID（系统匹配的培训计划）
     */
    @ApiModelProperty("培训计划ID")
    private Long trainingPlanId;

    /**
     * 确认人ID（管理员确认时填写）
     */
    @ApiModelProperty("确认人ID")
    private Long confirmedBy;

    /**
     * 确认时间
     */
    @ApiModelProperty("确认时间")
    private Date confirmedAt;

    /**
     * 创建人ID
     */
    @ApiModelProperty("创建人ID")
    private Long operatorId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remarks;

    // 注: 创建时间/更新时间由 BaseEntity.create_time/update_time 统一提供 (硬编码 @TableField).
    //   V20260621 已 drop 掉本表的 created_at/updated_at 冗余列, 实体里
    //   不能保留 createdAt/updatedAt 字段 — 否则 MyBatis-Plus 自动映射
    //   会把 SELECT 列里加上不存在的 created_at → Unknown column 500.
    //   之前因实体字段未同步导致 /safety/retraining/list 报"系统繁忙".
}
