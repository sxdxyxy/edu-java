package com.joyfishs.dawa.person.domain.param;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人脸照片
 */
@ApiModel(value = "刷脸登录请求")
@Data
public class FacePhotoRequest {
    @ApiModelProperty(value = "已上传人脸照片的url", required = true)
    @NotBlank(message = "人脸照片url")
    private String facePhotoUrl;
}
