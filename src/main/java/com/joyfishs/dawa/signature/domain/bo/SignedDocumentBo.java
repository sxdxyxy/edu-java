package com.joyfishs.dawa.signature.domain.bo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.joyfishs.system.config.validation.Update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轮播图
 */
@Data
@ApiModel(value = "SignedDocument", description = "签署文件")
public class SignedDocumentBo {

    @NotNull(message = "id不能为空", groups = {Update.class})
    private Long id;

    @ApiModelProperty("岗位签名配置id")
    @NotNull
    private Long configurationId;

    @ApiModelProperty("一人一档id")
    @NotNull
    private Long archivesId;

    @ApiModelProperty("签字图片文件url")
    @NotEmpty(message = "签字图片文件url不能空")
    private String signatureImageUrl;

    @ApiModelProperty("签字图片文件Key")
    @NotEmpty(message = "签字图片文件名称不能空")
    private String signatureImageName;
}
