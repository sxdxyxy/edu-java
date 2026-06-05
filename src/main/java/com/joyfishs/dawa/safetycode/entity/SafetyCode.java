package com.joyfishs.dawa.safetycode.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 安全码实体类
 * <p>
 * 用于管理用户的安全准入码，包含颜色标识、状态、有效期等信息
 * </p>
 *
 * @author OpenClaw
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "SafetyCode", description = "安全码")
@TableName("safety_codes")
public class SafetyCode extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 人员 ID（关联 xm_person.id）
     */
    @TableField("person_id")
    @ApiModelProperty(value = "人员 ID（关联 xm_person.id）")
    private Long personId;

    /**
     * 项目 ID
     */
    @TableField("project_id")
    @ApiModelProperty(value = "项目 ID")
    private Long projectId;

    /**
     * 组织机构 ID
     */
    @TableField("org_id")
    @ApiModelProperty(value = "组织机构 ID")
    private Long orgId;

    /**
     * 安全码值（加密存储）
     */
    @TableField("code")
    @ApiModelProperty(value = "安全码值（加密）")
    private String code;

    /**
     * 颜色标识：green/yellow/red
     */
    @TableField("color")
    @ApiModelProperty(value = "颜色标识：green-绿色/yellow-黄色/red-红色", example = "green")
    private String color;

    /**
     * 状态：active/suspended/expired
     */
    @TableField("status")
    @ApiModelProperty(value = "状态：active-激活/suspended-暂停/expired-过期", example = "active")
    private String status;

    /**
     * 有效期开始
     */
    @TableField("valid_from")
    @ApiModelProperty(value = "有效期开始")
    private LocalDateTime validFrom;

    /**
     * 有效期结束
     */
    @TableField("valid_to")
    @ApiModelProperty(value = "有效期结束")
    private LocalDateTime validTo;

    /**
     * 二维码数据（JSON 格式）
     */
    @TableField("qr_code_data")
    @ApiModelProperty(value = "二维码数据（JSON 格式）")
    private String qrCodeData;

    /**
     * 备注
     */
    @TableField("remarks")
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 位置信息
     */
    @TableField("location")
    @ApiModelProperty(value = "位置信息")
    private String location;

    /**
     * 项目 ID（扩展字段）- 已废弃，使用上面的 projectId
     */
    @Deprecated
    @TableField(exist = false)
    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectIdExt;

    /**
     * 安全分（扩展字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "安全分")
    private Integer score;

    /**
     * 二维码 URL（扩展字段）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "二维码 URL")
    private String qrCode;

    /**
     * 有效期至（扩展字段，同 validTo）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "有效期至")
    private LocalDateTime validUntil;

    /**
     * 最后更新时间（扩展字段，同 updatedAt）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdated;

    /**
     * 用户姓名（扩展字段，从 sys_user 关联查询）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    /**
     * 用户手机号（扩展字段，从 sys_user 关联查询）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户手机号")
    private String phone;

    /**
     * 项目名称（扩展字段，从 xm_project 关联查询）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * 身份证号（扩展字段，从 sys_user 关联查询）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "身份证号")
    private String idCardNo;

    /**
     * 颜色枚举值
     */
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_YELLOW = "yellow";
    public static final String COLOR_RED = "red";

    /**
     * 状态枚举值
     */
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_SUSPENDED = "suspended";
    public static final String STATUS_EXPIRED = "expired";
}