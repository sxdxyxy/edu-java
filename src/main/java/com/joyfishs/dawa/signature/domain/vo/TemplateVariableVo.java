package com.joyfishs.dawa.signature.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "TemplateVariableVo", description = "文档模板变量")
public class TemplateVariableVo {

    @ApiModelProperty("变量名称")
    private String variable;

    @ApiModelProperty("值描述")
    private String desc;

    @ApiModelProperty("示例值")
    private String sample;

    public TemplateVariableVo(String variable, String desc, String sample) {
        this.variable = variable;
        this.desc = desc;
        this.sample = sample;
    }
}
