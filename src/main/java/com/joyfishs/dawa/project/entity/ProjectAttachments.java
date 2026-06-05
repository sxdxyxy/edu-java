package com.joyfishs.dawa.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *项目附件
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "附件")
@TableName("xm_project_attachments")
public class ProjectAttachments {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;
    /**
     * oss文件Id
     */
    private String ossId;

    /** 附件类型：1=资料，2=凭证 **/
    private Integer type;

    /** 附件文件名称 **/
    private String fileName;

    private String url;
    /**
     * 冗余
     */
    private String fileKey;

}
