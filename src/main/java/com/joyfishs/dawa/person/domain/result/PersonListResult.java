package com.joyfishs.dawa.person.domain.result;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.system.annotation.Excel;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "PersonListResult", description = "学员列表结果集")
public class PersonListResult implements Serializable {
    @ApiModelProperty
    private Long id;

    @ApiModelProperty("姓名")
    @Excel(name = "姓名")
    private String name;

    @ApiModelProperty("用户名")
    @Excel(name = "用户名")
    private String userName;

    @ApiModelProperty("单位信息")
    @Excel(name = "单位信息")
    private String unit;

    @ApiModelProperty("部门信息")
    @Excel(name = "部门信息")
    private String dept;

    @ApiModelProperty("学时")
    @Excel(name = "年度学时要求")
    private BigDecimal planClassHour;

    @ApiModelProperty("注册日期")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    @Excel(name = "注册时间", dateFormat = DatePattern.NORM_DATE_PATTERN)
    Date registerDate;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    Date createTime;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("部门信息")
    private Long orgId;

    @ApiModelProperty("人员id")
    private Long personId;
}
