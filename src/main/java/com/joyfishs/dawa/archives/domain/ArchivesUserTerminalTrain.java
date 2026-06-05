package com.joyfishs.dawa.archives.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesUserTerminalTrain {

    /** 终端培训ID **/
    private Long id;

    /** 项目名称 **/
    private String projectName;

    /** 项目开始日期 **/
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date startDate;

    /** 项目结束日期 **/
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date endDate;

    /** 受训角色 **/
    private String roleName;

    /** 培训地址 **/
    private String trainAddr;

    /** 签到时间 **/
    @JsonFormat(pattern= DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private Date signInTime;

    /** 完成状态 0-未完成 1-已完成 **/
    private Integer finishState;

}
