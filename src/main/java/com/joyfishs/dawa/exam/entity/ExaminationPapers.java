package com.joyfishs.dawa.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 考试试卷题目
 */

@Data
@TableName("xm_examination_papers")
@Accessors(chain = true)
public class ExaminationPapers extends BaseEntity {

    /* id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /* 题目ID */
    private Long questionId;

    /* 项目ID */
    private Long projectId;

    /* 学员ID */
    private Long personId;

    /* 考试类型  1-模拟考试  2-正常考试 */
    private Integer examType;
}
