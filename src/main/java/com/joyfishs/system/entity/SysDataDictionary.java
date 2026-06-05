package com.joyfishs.system.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.joyfishs.system.config.validation.Update;

import lombok.Data;

/** 系统词典分类表 **/
@Data
public class SysDataDictionary extends BaseEntity {

    /** 词典分类ID **/
    @TableId(type = IdType.AUTO)
    @NotNull(message = "词典分类ID不能为空", groups = {Update.class})
    private Long id;

    /** 词典标识(键) **/
    private String dictionaryCode;

    /** 词典名称 **/
    @NotEmpty(message = "词典名称不能为空")
    private String dictionaryName;

}
