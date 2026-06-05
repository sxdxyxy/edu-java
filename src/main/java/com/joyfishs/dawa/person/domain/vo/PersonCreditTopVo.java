package com.joyfishs.dawa.person.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PersonCreditTopVo", description = "学员积分排行榜")
public class PersonCreditTopVo {
    @ApiModelProperty("名次")
    private int index;

    @ApiModelProperty("学员昵称")
    private String nickname;

    @ApiModelProperty("积分")
    private Integer credit;
}
