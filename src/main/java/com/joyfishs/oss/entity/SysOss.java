package com.joyfishs.oss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.joyfishs.system.entity.BaseEntity;

import lombok.Data;

@Data
public class SysOss extends BaseEntity {

    /** 附件ID **/
    @TableId(type = IdType.AUTO)
    private String id;

    /** 来源ID **/
    private String sourceId;

    /** 附件名称 **/
    private String fileName;

    /** 附件新名称,fileKey **/
    private String newName;

    /** 附件类型 **/
    private String fileType;

    /** 是否上传完成 **/
    private Boolean uploadFinish;

    /** URL地址 **/
    private String url;

}
