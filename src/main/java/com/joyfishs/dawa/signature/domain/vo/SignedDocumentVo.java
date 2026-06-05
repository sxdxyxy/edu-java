package com.joyfishs.dawa.signature.domain.vo;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "SignedDocumentVo", description = "需签署文件")
public class SignedDocumentVo {
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("岗位签名配置id")
    private Long configurationId;

    @ApiModelProperty("签署文件名称")
    private String documentName;

    @ApiModelProperty("模板文件url，变成图片预览是在url后面加上?ci-process=doc-preview&page=1")
    private String templateFileUrl;

    @ApiModelProperty("一人一档id")
    private Long archivesId;

    @ApiModelProperty("签署人id")
    private Long personId;

    @ApiModelProperty("已签署文件url，变成图片预览是在url后面加上?ci-process=doc-preview&page=1")
    private String fileUrl;

    @ApiModelProperty("签字图片文件url")
    private String signatureImageUrl;

    private LocalDateTime createTime;
}
