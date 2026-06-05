package com.joyfishs.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.experimental.Accessors;

/** 系统用户角色 **/
@Data
@Accessors(chain = true)
public class SysUserRole {

    /** 主键ID **/
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID **/
    private Long userId;

    /** 角色ID **/
    private Long roleId;

}
