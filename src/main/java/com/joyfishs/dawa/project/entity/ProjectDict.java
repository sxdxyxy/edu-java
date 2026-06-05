package com.joyfishs.dawa.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_project_dict")
@ApiModel(value = "ProjectDict", description = "培训项目种类")
public class ProjectDict extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 单位code
     */
    private String code;

    /**
     * 类型 1-培训种类
     */
    private Integer type;

    /**
     * 种类名称
     */
    private String name;

    /**
     * 值
     */
    private String value;
}
