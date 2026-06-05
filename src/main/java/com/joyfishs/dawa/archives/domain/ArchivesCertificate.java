package com.joyfishs.dawa.archives.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesCertificate {

    /** 证书ID **/
    private Long id;

    /** 姓名 **/
    private String personName;

    /** 人员所属党组织ID **/
    @TableField(exist = false)
    private Long persionOrgId;

    /** 单位信息 **/
    private String dwOrgName;

    /** 部门信息 **/
    private String bmOrgName;

    /** 证书名称 **/
    private String name;

    /** 证书类型 **/
    private String type;

    /** 注册日期 **/
    @JsonFormat(pattern= DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date registerDate;

    /** 失效日期 **/
    @JsonFormat(pattern= DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date validityEndDate;

    /** 证书状态 **/
    private String state;

}
