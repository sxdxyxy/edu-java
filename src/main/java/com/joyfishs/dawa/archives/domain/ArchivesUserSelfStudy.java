package com.joyfishs.dawa.archives.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesUserSelfStudy {

    /** 课程ID **/
    private Long id;

    /** 课程名称 **/
    private String courseName;

    /** 课时（分） **/
    private Integer classHour;

    /** 已学学时 **/
    private String alreadyClassHour;

}
