package com.joyfishs.system.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.joyfishs.system.config.validation.Update;

import lombok.Data;

/**
 * 系统角色表
 **/
@Data
public class SysRole extends BaseEntity {

    public static final Long PLATFORM_MANAGER_ROLE = 2L;

    public static final Long STUDENT_ROLE = 3L;
    // D6-2: 公司管理员 (sys_role.code='company_manager'), id=9
    //   跟 DB 对齐, 数据按 org_id 隔离, 见 dawaRole.js COMPANY_ADMIN
    public static final Long COMPANY_MANAGER_ROLE = 9L;

    /**
     * 角色ID
     **/
    @NotNull(message = "角色ID不能为空", groups = {Update.class})
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     **/
    @NotEmpty(message = "角色名称不能为空")
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 角色所属组织ID
     **/
    private Long orgCode;

    /**
     * 父级角色ID
     **/
    private Long parentId;

}
