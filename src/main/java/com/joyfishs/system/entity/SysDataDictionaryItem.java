package com.joyfishs.system.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.joyfishs.system.config.validation.Update;

import lombok.Data;

/** 系统词典项表 **/
@Data
public class SysDataDictionaryItem extends BaseEntity {

    /** 词典项ID **/
    @TableId(type = IdType.AUTO)
    @NotNull(message = "词典项ID不能为空", groups = {Update.class})
    private Long id;

    /** 词典标识(键) **/
    @NotEmpty(message = "词典标识不能为空")
    private String dictionaryCode;

    /** 词典项目名称 **/
    @NotEmpty(message = "词典项名称不能为空")
    private String name;

    /** 词典项目值 **/
    @NotNull(message = "词典项值不能为空")
    private Integer value;

    /** 所属上级值顶级默认-1 **/
    @NotNull(message = "所属上级不能为空")
    private Integer parentid;

    /** 备注说明 **/
    private String description;

    /** 显示顺序 **/
    @NotNull(message = "显示顺序不能为空", groups = {Update.class})
    private Integer sortid;

}
