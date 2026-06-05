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
 * 安全积分变动流水实体类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_safety_score_transaction", autoResultMap = true)
@ApiModel(value = "SafetyScoreTransaction", description = "安全积分变动流水")
public class SafetyScoreTransaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变动类型：扣分
     */
    public static final String CHANGE_TYPE_DEDUCT = "deduct";

    /**
     * 变动类型：恢复
     */
    public static final String CHANGE_TYPE_RESTORE = "restore";

    /**
     * 变动类型：清零
     */
    public static final String CHANGE_TYPE_RESET = "reset";

    /**
     * 变动类型：调整
     */
    public static final String CHANGE_TYPE_ADJUST = "adjust";

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
     * 变动类型（deduct/restore/reset/adjust）
     */
    @ApiModelProperty("变动类型")
    private String changeType;

    /**
     * 变动分值（正数为恢复，负数为扣减）
     */
    @ApiModelProperty("变动分值")
    private Integer changeAmount;

    /**
     * 变动前积分
     */
    @ApiModelProperty("变动前积分")
    private Integer beforeScore;

    /**
     * 变动后积分
     */
    @ApiModelProperty("变动后积分")
    private Integer afterScore;

    /**
     * 触发原因
     */
    @ApiModelProperty("触发原因")
    private String triggerReason;

    /**
     * 关联违章记录ID
     */
    @ApiModelProperty("关联违章记录ID")
    private Long violationRecordId;

    /**
     * 关联再培训记录ID
     */
    @ApiModelProperty("关联再培训记录ID")
    private Long retrainingRecordId;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private Long operatorId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remarks;

    /**
     * 变动时间
     */
    @ApiModelProperty("变动时间")
    private Date createdAt;
}
