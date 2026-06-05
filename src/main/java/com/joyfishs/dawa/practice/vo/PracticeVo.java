package com.joyfishs.dawa.practice.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

// 练习
@Data
@Accessors(chain = true)
public class PracticeVo implements Serializable {

    /** 课程id */
    private Long courseId;

    /** 课程名称 */
    private String courseName;

    /** 练习类型 1:未练习 2:未完成 3:已完成*/
    private int type;

    /** 练习分类->课程类别（0006） */
    private String practiceType;

    /** 练习分类名称*/
    private String practiceTypeName;

    /*public String getPracticeTypeName() {
        SysDataDictionaryItem dictionaryItem = Dictionary.getDictionaryItem("0006", this.practiceType);
        return dictionaryItem.getName();
    }*/

    /** 题数 */
    private int questionCount;

    /** 已答题数 */
    private int answeredCount;


    // 以下为项目相关字段
    // 2022-03-09 修改为 我的练习显示平台所有课程的习题 不关联项目

    /** 项目id */
    private Long projectId;

    /** 项目名称 */
    private String projectName;

    /** 是否为必修课程 1-选修  0-必修 */
//    private String isRequired;
}
