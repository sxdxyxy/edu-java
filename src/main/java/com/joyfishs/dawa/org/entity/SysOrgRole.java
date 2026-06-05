package com.joyfishs.dawa.org.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目部内部角色表
 */
@Data
@TableName("sys_org_role")
public class SysOrgRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目部ID */
    private Long orgId;

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

    /** 角色类型 */
    private String roleType;

    /** 排序 */
    private Integer sort;

    /** 状态：0=禁用，1=启用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;
}
