package com.joyfishs.dawa.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import lombok.Data;

/**
 * 考试预约
 */
@Data
@TableName("xm_student_subscribe_exam")
public class StudentSubscribeExam extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /* 项目Id */
    private Long projectId;

    /* 人员ID */
    private Long PersonId;

    /* 预约状态  状态 0-（否）默认  1-已预约  2-已经考过 */
    private Integer status;

}
