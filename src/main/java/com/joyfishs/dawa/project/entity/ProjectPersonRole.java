package com.joyfishs.dawa.project.entity;

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
 * 项目人员职务实体类
 * <p>
 * 用于管理项目中人员的职务分配，包含项目经理、安全主管、安全员、班组长、普通工人等角色
 * </p>
 *
 * @author OpenClaw
 * @since 2026-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "ProjectPersonRole", description = "项目人员职务")
@TableName("project_person_roles")
public class ProjectPersonRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键 ID")
    private Long id;

    /**
     * 人员 ID（关联 xm_person.id）
     */
    @TableField("person_id")
    @ApiModelProperty(value = "人员 ID（关联 xm_person.id）")
    private Long personId;

    /**
     * 项目 ID（关联 xm_project.id）
     */
    @TableField("project_id")
    @ApiModelProperty(value = "项目 ID（关联 xm_project.id）")
    private Long projectId;

    /**
     * 职务编码
     */
    @TableField("role_code")
    @ApiModelProperty(value = "职务编码")
    private String roleCode;

    /**
     * 职务编码常量
     */
    public static final String ROLE_PROJECT_MANAGER = "PROJECT_MANAGER";
    public static final String ROLE_SAFETY_SUPERVISOR = "SAFETY_SUPERVISOR";
    public static final String ROLE_SAFETY_OFFICER = "SAFETY_OFFICER";
    public static final String ROLE_TEAM_LEADER = "TEAM_LEADER";
    public static final String ROLE_WORKER = "WORKER";

    /**
     * 项目经理
     */
    public static final String ROLE_DESC_PROJECT_MANAGER = "项目经理";

    /**
     * 安全主管
     */
    public static final String ROLE_DESC_SAFETY_SUPERVISOR = "安全主管";

    /**
     * 安全员
     */
    public static final String ROLE_DESC_SAFETY_OFFICER = "安全员";

    /**
     * 班组长
     */
    public static final String ROLE_DESC_TEAM_LEADER = "班组长";

    /**
     * 普通工人
     */
    public static final String ROLE_DESC_WORKER = "普通工人";

    /**
     * 获取职务描述
     *
     * @param roleCode 职务编码
     * @return 职务描述
     */
    public static String getRoleDescription(String roleCode) {
        if (ROLE_PROJECT_MANAGER.equals(roleCode)) {
            return ROLE_DESC_PROJECT_MANAGER;
        } else if (ROLE_SAFETY_SUPERVISOR.equals(roleCode)) {
            return ROLE_DESC_SAFETY_SUPERVISOR;
        } else if (ROLE_SAFETY_OFFICER.equals(roleCode)) {
            return ROLE_DESC_SAFETY_OFFICER;
        } else if (ROLE_TEAM_LEADER.equals(roleCode)) {
            return ROLE_DESC_TEAM_LEADER;
        } else if (ROLE_WORKER.equals(roleCode)) {
            return ROLE_DESC_WORKER;
        }
        return roleCode;
    }

    /**
     * 校验是否为有效的职务编码
     *
     * @param roleCode 职务编码
     * @return 是否有效
     */
    public static boolean isValidRoleCode(String roleCode) {
        return ROLE_PROJECT_MANAGER.equals(roleCode)
            || ROLE_SAFETY_SUPERVISOR.equals(roleCode)
            || ROLE_SAFETY_OFFICER.equals(roleCode)
            || ROLE_TEAM_LEADER.equals(roleCode)
            || ROLE_WORKER.equals(roleCode);
    }
}
