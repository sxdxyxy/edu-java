package com.joyfishs.dawa.archives.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesUserTerminalTrainDetail {

    /** 终端培训ID **/
    private Long id;

    /** 培训名称 **/
    private String name;

    /** 培训时间 **/
    private String time;

    /** 主持人 **/
    private String host;

    /** 会议人数 **/
    private Integer personSize;

    /** 学习内容 **/
    private String studyContent;

    /** 联系人 **/
    private String contact;

    /** 备注 **/
    private String remark;

    /** 签到时间 **/
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private Date signInTime;

    /** 培训状态 0-未完成 1-已完成 **/
    private Integer state;

}
