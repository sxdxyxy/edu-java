package com.joyfishs.dawa.signature.domain.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "OnePersonOneArchivesVo", description = "一人一档")
public class OnePersonOneArchivesVo {
    private Long id;

    @ApiModelProperty("档案编号")
    private String number;

    @ApiModelProperty("人员id")
    private Long personId;

    private Integer workType;

    @ApiModelProperty("工种，1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工")
    private String workTypeName;

    @ApiModelProperty("人员姓名")
    private String personName;

    @ApiModelProperty("用户性别 0-未知 1-男 2-女")
    private Integer sex;

    @ApiModelProperty("签名文档")
    private List<SignedDocumentVo> signedDocument;

    @ApiModelProperty("一人一档信息目录文件url")
    private String catalogueUrl;

    @ApiModelProperty("生成的压缩文件url")
    private String fileUrl;

    @ApiModelProperty("生成压缩文件是否完成")
    private Boolean jobComplete;
}
