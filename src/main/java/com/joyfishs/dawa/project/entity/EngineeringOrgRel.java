package com.joyfishs.dawa.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工程项目与单位关联表
 */
@Data
@TableName("xm_engineering_org_rel")
public class EngineeringOrgRel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工程项目ID */
    private Long engineeringId;

    /** 单位ID */
    private Long orgId;

    /** 角色类型：owner=建设单位，design=设计单位，construct=施工单位，supervisor=监理单位 */
    private String roleType;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 以下为关联查询的组织信息（非数据库字段） */
    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String code;
}
