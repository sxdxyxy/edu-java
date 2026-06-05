package com.joyfishs.dawa.person.domain.result;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author ykfnb
 */

@Data
@ApiModel(value = "IDCardOCRResult", description = "身份证OCR结果")
public class IDCardOCRResult implements Serializable {

    private Long personId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("身份证号码")
    private String cardNum;

    @ApiModelProperty("身份证照片（人像面）")
    private String frontFullImg;

    @ApiModelProperty("身份证照片（国徽面）")
    private String backFullImg;
}
