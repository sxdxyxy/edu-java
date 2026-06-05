package com.joyfishs.dawa.notice.domain.bo;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "必读文件")
public class MustReadBo extends NewsBo {

    @NotNull(message = "学时不能空")
    @ApiModelProperty(value = "学时", required = true)
    private BigDecimal classHour;

}
