package com.joyfishs.dawa.course.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.joyfishs.system.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "课程")
@TableName("xm_course")
public class Course extends BaseEntity {

    /**
     * id自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 课程类别
     *1-系统课程  2-自主课程 3-合作课程
     */
    private Integer type;

    /**
     * 创建单位ID
     */
    @NotBlank(message = "单位ID不能为空格")
    private Long orgId;

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类别（0006）
     */
    private Integer courseType;
    /*课程类别名称*/
    @TableField(exist = false)
    private String courseTypeName;

    /**
     * 课时
     */
    @TableField(exist = false)
    private String hour;

    /**
     * 课件总时长
     */
    @TableField(exist = false)
    private String totalDuration;

    /**
     * 学习内容
     */
    private String learningContent;

    /**
     * 所属行业（数据字典待填充）
     */
    private Integer industryInvolved;

    /**
     * 岗位（字典待填充）
     */
    private Integer job;

    /**
     * 封面图路径
     */
    private String coverPath;

    /**
     * 课程讲师ID
     */
    private Long lecturer;

    /**
     * 课程开始日期
     */
    private LocalDateTime startDate;

    /**
     * 课程结束日期
     */
    private LocalDateTime dateClosed;

    /**
     * 课程介绍详情/备注
     */
    private String courseIntroduce;

    /**
     * 三方课程ID
     */
    private Long thirdPartyId;

    /** 课件集合 **/
    @TableField(exist = false)
    private List<Courseware> coursewareList;

    /** 统计编辑课程题目数量 **/
    @TableField(exist = false)
    private Map<String,Integer> map;

    /** 统计列表课程题目数量 **/
    @TableField(exist = false)
    private Integer questionCount;

    /* 接收前台传入的标签名称 */
    @TableField(exist = false)
    private List<String> tags;

    @TableField(exist = false)
    private List<CourseTag> courseTags;

    /* 接收标签value */
    @TableField(exist = false)
    private String tag;

    /**
     * 1-系统课程  2-自主课程 3-合作课程
     * @param typeStr
     * @return
     */
    public static Integer queryType(String typeStr) {
        if (typeStr == null) {
            return 0;
        }
        switch (typeStr) {
            case "sys":
            case "1":
                return 1;
            case "self":
            case "2":
                return 2;
            case "tp":
            case "3":
                return 3;
            default:
                return 0;
        }
    }
}
