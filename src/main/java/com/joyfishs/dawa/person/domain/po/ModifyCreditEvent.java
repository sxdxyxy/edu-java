package com.joyfishs.dawa.person.domain.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PersonCreditDetail", description = "积分明细")
public class ModifyCreditEvent {
    @ApiModelProperty("学员id")
    private Long personId;

    @ApiModelProperty("积分类型，1=浏览公告，2=观看视频，3=单次登录观看视频时长累计30分钟，4=做练习题，5=参与考试")
    private Integer type;

    @ApiModelProperty("引起积分变化的业务id")
    private String businessId;
}
