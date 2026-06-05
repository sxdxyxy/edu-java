package com.joyfishs.dawa.access.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



/**
 * 准入记录数据传输对象
 * 
 * @author safe-edu
 * @since 2026-03-31
 */
@Data
@ApiModel(value = "AccessRecordDTO", description = "准入记录数据传输对象")
public class AccessRecordDTO {

    @ApiModelProperty(value = "关联用户 ID")
    private Long userId;

    @ApiModelProperty(value = "关联项目 ID")
    private Long projectId;

    @ApiModelProperty(value = "准入类型：normal/temporary/denied")
    private String accessType;

    @ApiModelProperty(value = "进场时安全码颜色")
    private String safetyCodeColor;

    @ApiModelProperty(value = "闸机编号")
    private String gateId;

    @ApiModelProperty(value = "抓拍照片 URL")
    private String cameraSnapshot;

    @ApiModelProperty(value = "位置信息")
    private String location;

    @ApiModelProperty(value = "备注")
    private String remarks;
}