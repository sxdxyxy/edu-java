package com.joyfishs.dawa.archives.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesProject {

    /** 项目ID **/
    private Long id;

    /** 项目名称 **/
    private String name;

    /** 项目时间 **/
    private String time;

    /** 培训方式 **/
    private String way;

    /** 人数 **/
    private Integer peopleSize;

    /** 创建人 **/
    private String creater;

    /** 创建时间 **/
    @JsonFormat(pattern= DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private Date createTime;

    /** 项目状态 1-未发布 2-未开始 3-进行中  4-已完成  5-已中止 */
    private String statusName;

}
