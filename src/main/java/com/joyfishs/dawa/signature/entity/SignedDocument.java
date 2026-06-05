package com.joyfishs.dawa.signature.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangkaifeng
 */
@Data
@TableName(value = "xm_signed_document")
@ApiModel(value = "SignedDocument", description = "已经签署文件")
public class SignedDocument {
    /**
     * 未签名的签名图片
     */
    public static final String UNSIGNED = "https://static.joyfishs.com/signature/unsigned.png";

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("岗位签名配置id")
    @NotNull
    private Long configurationId;

    @ApiModelProperty("签署文件名称")
    private String documentName;

    @ApiModelProperty("一人一档id")
    @NotNull
    private Long archivesId;

    @ApiModelProperty("签署人id")
    private Long personId;

    @ApiModelProperty("签署后文件url")
    private String fileUrl;

    @ApiModelProperty("签署后文件key")
    private String fileName;

    @ApiModelProperty("签字图片文件url")
    private String signatureImageUrl;

    @ApiModelProperty("签字图片文件Key")
    private String signatureImageName;

    private LocalDateTime createTime;
}
