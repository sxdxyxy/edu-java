package com.joyfishs.dawa.archives.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesProjectUserAnswer {

    /** 答题ID **/
    private Long id;

    /** 交卷时间 **/
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private Date handDate;

    /** 考试时长 **/
    private String testTime;

    /** 考试成绩 **/
    private Integer testResult;

}
