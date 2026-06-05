package com.joyfishs.dawa.person.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yangkaifeng
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_person_credit_detail")
@ApiModel(value = "PersonCreditDetail", description = "积分明细")
public class PersonCreditDetail {
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("学员id")
    private Long personId;

    @ApiModelProperty("积分类型，1=浏览公告，2=观看视频，3=单次登录观看视频时长累计30分钟，4=做练习题，5=参与考试")
    private Integer type;

    @ApiModelProperty("引起积分变化的业务id")
    private String businessId;

    @ApiModelProperty("积分：获得为+，减少为-")
    private Integer points;

    @ApiModelProperty("记录时间")
    private LocalDateTime createTime;
}
