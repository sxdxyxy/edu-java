package com.joyfishs.dawa.person.domain.po;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "IDCardOCRRequest", description = "身份证识别请求")
public class IDCardOCRRequest {

    @ApiModelProperty("身份证照片url")
    @NotEmpty
    private String idPhotoFaceUrl;

    @ApiModelProperty("是否反面（国徽面）")
    @NotNull
    private Boolean back;

}
