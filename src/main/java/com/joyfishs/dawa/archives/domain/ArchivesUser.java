package com.joyfishs.dawa.archives.domain;

import java.math.BigDecimal;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
public class ArchivesUser {

    /** 人员Id **/
    private Long id;

    /** 人员姓名 **/
    private String name;

    /** 单位部门id **/
    private Long orgId;

    /** 受训角色 **/
    private String roleName;

    /** 单位名称 **/
    private String dwOrgName;

    /** 部门名称 **/
    private String bmOrgName;

    /** 年度学时要求 **/
    private BigDecimal yearClassHourRequire;

    /** 年度学时 **/
    private BigDecimal yearClassHour;

    /** 累计学时 **/
    private BigDecimal addUpClassHour;

    /** 项目学时 **/
    private BigDecimal projectClassHour;

}
