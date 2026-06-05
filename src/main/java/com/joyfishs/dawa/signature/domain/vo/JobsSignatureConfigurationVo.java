package com.joyfishs.dawa.signature.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "JobsSignatureConfiguration", description = "岗位签名配置")
public class JobsSignatureConfigurationVo{
    private Long id;

    @ApiModelProperty("工种，1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工")
    private Integer workType;

    @ApiModelProperty("工种名称")
    private String workTypeName;

    @ApiModelProperty("需签署文件名称")
    private String documentName;

    @ApiModelProperty("上传的模板文件url")
    private String fileUrl;

    @ApiModelProperty("上传的模板文件key")
    private String fileName;

    @ApiModelProperty("组织机构id")
    private Long orgId;
}
