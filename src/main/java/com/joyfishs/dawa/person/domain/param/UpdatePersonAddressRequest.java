package com.joyfishs.dawa.person.domain.param;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新地址
 */
@ApiModel(value = "更新地址请求")
@Data
public class UpdatePersonAddressRequest {
    @ApiModelProperty(value = "人员ID", required = true)
    @NotNull(message = "人员ID不能为空")
    private Long id;

    /** 地址 */
    @ApiModelProperty(value = "地址", required = true)
    @NotEmpty(message = "地址不能为空")
    private String address;
}
