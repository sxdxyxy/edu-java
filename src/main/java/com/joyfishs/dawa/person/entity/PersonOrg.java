package com.joyfishs.dawa.person.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 人员项目关联表
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@TableName(value ="xm_person_org")
public class PersonOrg {

    /**  id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 人员id */
    private Long personId;

    /** 组织-项目id */
    private Long orgId;
}
