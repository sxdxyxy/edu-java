package com.joyfishs.dawa.person.domain.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.joyfishs.dawa.person.entity.PersonChangeLog;
import com.joyfishs.dawa.student.domain.MyCourseList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "PersonDetailsVo", description = "详情")
public class PersonDetailsVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("人员部门")
    private String orgName;

    @ApiModelProperty("积分")
    private Integer credit;

    @ApiModelProperty("累计学时")
    private BigDecimal totalClassHours;

    @ApiModelProperty("年度计划学时")
    private BigDecimal planClassHour;

    @ApiModelProperty("年度学时")
    private BigDecimal yearClassHour;

    /**
     * 本年度所学课程
     */
    private List<MyCourseList> courseList = new ArrayList<>();
    /**
     * 变动列表
     */
    private List<PersonChangeLog> changeLogs;
}
