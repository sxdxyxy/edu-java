package com.joyfishs.dawa.course.domain.vo;

import com.joyfishs.dawa.course.domain.bo.CoursewareBo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CoursewareVo", description = "课件")
public class CoursewareVo extends CoursewareBo {

    @ApiModelProperty("文件大小")
    private String sizeStr;

    @ApiModelProperty("时长")
    private String duration;

    @ApiModelProperty("浏览量")
    private Integer viewCount;

    @ApiModelProperty("fileId")
    private String fileId;

    @ApiModelProperty("封面图路径")
    private String coverPath;

    @ApiModelProperty("文件名后缀")
    private String fileExtension;

    @ApiModelProperty("是否第三方课件")
    private Boolean thirdParty;

    @ApiModelProperty("是否转码完成 true=完成  false=在转码中")
    private Boolean transcode;

    @ApiModelProperty("下载次数")
    private Integer downCount;

    @ApiModelProperty("课件类别名称")
    private String typeName;

    private String publishName;

}
