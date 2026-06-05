package com.joyfishs.dawa.archives.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesGraduationCertificate {

    private Long id;

    private Long projectId;

    private String projectName;

    private Long personId;

    private String personName;

    private String result;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private String timeCompletion;

    private String dwOrgName;

    private String bmOrgName;

    private String trainWay;

    private String trainWayName;
}
