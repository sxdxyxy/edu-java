package com.joyfishs.dawa.person.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "PersonCreditVo", description = "学员积分列表")
public class PersonCreditVo {

    @ApiModelProperty("学员id")
    private Long personId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("人员部门")
    private String orgName;

    @ApiModelProperty("积分")
    private Integer credit;
}
