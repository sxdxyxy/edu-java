package com.joyfishs.dawa.archives.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ArchivesProjectUser {

    /** 人员ID **/
    private Long id;

    /** 姓名 **/
    private String name;

    /*组织ID*/
    private Long orgId;

    /** 单位信息 **/
    private String dwOrgName;

    /** 部门信息 **/
    private String bmOrgName;

    /** 受训角色 **/
    private String roleName;

    /** 应修学时 **/
    private String mustClassHour;

    /** 已修学时 **/
    private String alreadyClassHour;

    /** 总练习题量 **/
    private Integer addUpExercises;

    /** 已练习题量 **/
    private Integer alreadyExercises;

    /** 答对题量 **/
    private Integer yesTopic;

    /** 正确率 **/
    private String yesRate;

    /** 考试成绩 **/
    private Integer testResult;

    /** 补考成绩 **/
    private String mendTestResult;

    /** 考试耗时 **/
    private String testTime;

    /** 考试状态 1-合格 2-不合格 3-未考试 **/
    private Integer testState;

    /* 考试合格分数 */
    private Integer passScore;

}
