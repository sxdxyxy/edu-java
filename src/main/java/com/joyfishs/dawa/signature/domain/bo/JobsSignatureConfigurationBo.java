package com.joyfishs.dawa.signature.domain.bo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轮播图
 */
@Data
@ApiModel(value = "JobsSignatureConfiguration", description = "岗位签名配置")
public class JobsSignatureConfigurationBo {
    private Long id;

    @ApiModelProperty("工种，1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工")
    @NotNull(message = "工种不能空")
    private Integer workType;

    @ApiModelProperty("需签署文件名称")
    @NotEmpty(message = "需签署文件名称不能空")
    private String documentName;

    @ApiModelProperty("上传的文件模板url")
    @NotEmpty(message = "上传的文件模板url不能空")
    private String fileUrl;

    @ApiModelProperty("上传的文件模板key")
    @NotEmpty(message = "上传的文件模板名称不能空")
    private String fileName;

    @ApiModelProperty("组织机构id")
    @NotNull(message = "组织机构id不能空")
    private Long orgId;
}
