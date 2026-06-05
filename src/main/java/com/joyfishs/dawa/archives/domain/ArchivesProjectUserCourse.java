package com.joyfishs.dawa.archives.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesProjectUserCourse {

    /** 课程ID **/
    private Long id;

    /** 课程名称 **/
    private String courseName;

    /** 应修学时 **/
    private String mustClassHour;

    /** 已修学时 **/
    private String alreadyClassHour;

    /** 总题量 **/
    private Integer addUpExercises;

    /** 已答题量 **/
    private Integer alreadyExercises;

    /** 答对题量 **/
    private Integer yesExercises;

    /** 答对正确率 **/
    private String yesTopic;

    /** 完成状态 0-未完成 1-已完成 **/
    private Integer finishState;

}
