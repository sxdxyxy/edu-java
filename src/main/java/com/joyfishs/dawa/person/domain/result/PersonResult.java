package com.joyfishs.dawa.person.domain.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.joyfishs.dawa.person.entity.PersonCertificate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author ykfnb
 */

@Data
@Accessors(chain = true)
@ApiModel(value = "PersonResult", description = "学员信息结果集")
public class PersonResult implements Serializable {
    @ApiModelProperty
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("用户性别 0-未知 1-男 2-女")
    private int sex;

    @ApiModelProperty("身份证号码")
    private String idCardNo;

    @ApiModelProperty("身份证照片")
    private String idCardNoFile;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("岗位 多个岗位用,分隔 字典待定")
    private String jobs;

    @ApiModelProperty("工种 多个工种用,分隔 字典待定")
    private String workType;

    @ApiModelProperty("学历 字典待定")
    private Integer degreeId;

    @ApiModelProperty("类型 多个类型用,分隔 字典待定")
    private String type;

    @ApiModelProperty("部门信息")
    private Long orgId;

    private Integer isAdmin;

    private Integer state;


    /**
     * 证书列表
     */
    private List<PersonCertificate> certificateList = new ArrayList<>();

    /* 组织名称 */
    private String orgName;

    private List<PersonChangeLogResult> changeLogList = new ArrayList<>();

}
