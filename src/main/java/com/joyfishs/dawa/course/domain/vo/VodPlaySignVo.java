package com.joyfishs.dawa.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "VodPlaySignVo", description = "播放器签名结果")
public class VodPlaySignVo {

    @ApiModelProperty("fileID")
    private String fileID;

    @ApiModelProperty("appID")
    private String appID;

    @ApiModelProperty("签名")
    private String psign;

}
