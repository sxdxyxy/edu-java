package com.joyfishs.dawa.signature.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 签名配置
 */
@Data
@TableName(value = "xm_jobs_signature_configuration")
@ApiModel(value = "JobsSignatureConfiguration", description = "岗位签名配置")
public class JobsSignatureConfiguration extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("工种，1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工")
    private Integer workType;

    @ApiModelProperty("需签署文件名称")
    private String documentName;

    @ApiModelProperty("上传的文件模板url")
    private String fileUrl;

    @ApiModelProperty("上传的文件模板key")
    private String fileName;

    @ApiModelProperty("组织机构id")
    private Long orgId;

    @ApiModelProperty("项目部id")
    private Long projectId;
}
