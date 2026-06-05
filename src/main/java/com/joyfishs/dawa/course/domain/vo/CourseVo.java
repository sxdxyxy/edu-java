package com.joyfishs.dawa.course.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.joyfishs.dawa.course.entity.CourseTag;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.system.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@ApiModel(value = "CourseVo", description = "课程")
public class CourseVo extends BaseEntity {

    private Long id;

    private String type;

    /**
     * 创建单位ID
     */
    @NotBlank(message = "单位ID不能为空格")
    private Long orgId;

    private String courseCode;

    /**
     * 课程名称
     */
    @NotEmpty(message = "请输入课程名称")
    private String courseName;

    /**
     * 课程类别（0006）
     */
    @NotNull(message = "请选择课程类别")
    private Integer courseType;

    private String courseTypeName;

    /**
     * 课时
     */
    private String hour;

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

    /**
     * 课件集合
     **/
    private List<Courseware> coursewareList;

    /**
     * 统计编辑课程题目数量
     **/
    private Map<String, Integer> map;

    /**
     * 统计列表课程题目数量
     **/
    private Integer questionCount;

    /* 接收前台传入的标签名称 */
    private List<String> tags;

    private List<CourseTag> courseTags;

    /* 接收标签value */
    @TableField(exist = false)
    private String tag;
}
