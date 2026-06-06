package com.joyfishs.dawa.violation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 违章记录实体类
 *
 * @author safe-edu
 * @since 2026-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "violation_records", autoResultMap = true)
@ApiModel(value = "ViolationRecord", description = "违章记录")
public class ViolationRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键 ID")
    private Long id;

    /**
     * 关联用户 ID
     */
    @ApiModelProperty("关联用户 ID")
    private Long userId;

    /**
     * 人员 ID（xm_person.id）
     */
    @ApiModelProperty("人员 ID（xm_person.id）")
    private Long personId;

    /**
     * 关联项目 ID
     */
    @ApiModelProperty("关联项目 ID")
    private Long projectId;

    /**
     * 关联项目部 ID（组织机构）
     */
    @ApiModelProperty("关联项目部 ID")
    private Long orgId;

    /**
     * 违章类型
     */
    @ApiModelProperty("违章类型")
    private String violationType;

    /**
     * 严重程度：minor/major/critical
     */
    @ApiModelProperty("严重程度：minor/major/critical")
    private String severity;

    /**
     * 记分值（1-12 分）
     */
    @ApiModelProperty("记分值（1-12 分）")
    private Integer score;

    /**
     * 本次扣分分值
     */
    @ApiModelProperty("本次扣分分值")
    private Integer deductAmount;

    /**
     * 是否触发再培训
     */
    @ApiModelProperty("是否触发再培训")
    private Boolean triggerRetraining;

    /**
     * 关联的再培训记录 ID
     */
    @ApiModelProperty("关联的再培训记录 ID")
    private Long retrainingRecordId;

    /**
     * 违章描述
     */
    @ApiModelProperty("违章描述")
    private String description;

    /**
     * 证据照片（JSON 数组）
     */
    @ApiModelProperty("证据照片（JSON 数组）")
    private String evidencePhotos;

    /**
     * 违章地点
     */
    @ApiModelProperty("违章地点")
    private String location;

    /**
     * 处理人 ID
     */
    @ApiModelProperty("处理人 ID")
    private Long handlerId;

    /**
     * 操作人 ID（录入人ID）
     */
    @ApiModelProperty("操作人 ID（录入人ID）")
    private Long operatorId;

    /**
     * 处理时间
     */
    @ApiModelProperty("处理时间")
    private Date processedAt;

    /**
     * 状态：pending/processed/appealed/appeal_approved/appeal_rejected
     */
    @ApiModelProperty("状态：pending/processed/appealed/appeal_approved/appeal_rejected")
    private String status;

    /**
     * 创建时间（冗余字段，兼容原表结构，BaseEntity已有createTime）
     */
    @ApiModelProperty("创建时间")
    private Date createdAt;

    /**
     * 乐观锁版本号（防并发扣分扣错分）
     * <p>
     * 同一人员多条违章同时录入时,后到的 update 会失败抛
     * {@link com.baomidou.mybatisplus.extension.plugins.handler.optimistic.OptimisticLockerInnerInterceptor}。
     * 业务侧应捕获 {@code ObjectOptimisticLockingFailureException} 后重试。
     * </p>
     */
    @Version
    @ApiModelProperty("乐观锁版本号")
    private Integer version;

    // ===== 扩展字段（从 sys_user 关联查询）=====
    /**
     * 人员姓名
     */
    @TableField(exist = false)
    @ApiModelProperty("人员姓名")
    private String personName;

    /**
     * 人员工种
     */
    @TableField(exist = false)
    @ApiModelProperty("人员工种")
    private Integer personWorkType;

    /**
     * 工种名称
     */
    @TableField(exist = false)
    @ApiModelProperty("工种名称")
    private String workTypeName;

    /**
     * 用户姓名
     */
    @TableField(exist = false)
    @ApiModelProperty("用户姓名")
    private String userName;

    /**
     * 用户手机号
     */
    @TableField(exist = false)
    @ApiModelProperty("用户手机号")
    private String phone;

    /**
     * 身份证号
     */
    @TableField(exist = false)
    @ApiModelProperty("身份证号")
    private String idCardNo;

    /**
     * 项目名称
     */
    @TableField(exist = false)
    @ApiModelProperty("项目名称")
    private String projectName;
}