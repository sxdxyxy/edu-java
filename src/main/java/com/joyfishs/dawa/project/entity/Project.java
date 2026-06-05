package com.joyfishs.dawa.project.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.project.domain.vo.ProjectCourseList;
import com.joyfishs.dawa.project.domain.vo.ProjectPersonList;
import com.joyfishs.system.entity.BaseEntity;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@TableName(value = "xm_project")
@ApiModel(value = "Project", description = "项目")
public class Project extends BaseEntity {
    @ApiModelProperty
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("项目所属组织")
    private Long orgId;

    @ApiModelProperty("项目名")
    @NotBlank(message = "项目名不能为空")
    private String projectName;

    @ApiModelProperty("项目类型 0-系统项目  1-自主项目  2-年度培训计划  3-单位培训计划")
    private Integer projectType;

    @ApiModelProperty("人数")
    private Integer personNumber;

    @ApiModelProperty("培训种类id")
    private Integer trainClass;

    @TableField(exist = false)
    private String trainClassName;

    /**
     * 课程类型 1-必修课  2-选修课
     */
    private Integer trainType;

    /**
     * 培训方式 1=培训  2=考试  3=培训+练习  4=培训+练习+考试
     */
    private Integer trainWay;

    @TableField(exist = false)
    private String trainWayName;

    public String getTrainWayName() {
        switch (this.trainWay) {
            case 1:
                return "培训";
            case 2:
                return "考试";
            case 3:
                return "培训+练习";
            case 4:
                return "培训+练习+考试";
            default:
                return null;
        }
    }

    /**
     * 人员类型
     */
    private Integer personType;

    /**
     * 培训开始时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date trainSdate;

    /**
     * 培训结束时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date trainEdate;

    /**
     * 练习开始时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date practiceSdate;

    /**
     * 练习结束时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date practiceEdate;

    /**
     * 考试开始时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date examSdate;

    /**
     * 考试结束时间
     */
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private Date examEdate;

    /**
     * 考试状态  1-未开始  2-进行中 3-已结束
     */
    private Integer examState;

    /**
     * 考试次数
     */
    private Integer examNumber;

    /**
     * 补考次数
     */
    private Integer resitNumber;

    /**
     * 模拟考试 1-允许  0-不允许
     */
    private String mockExam;

    /**
     * 项目状态 1-未发布 2-未开始 3-进行中  4-已完成  5-已中止
     */
    private Integer projectStatus;

    /**
     * 总课时
     */
    private Integer totalCourseHours;

    /**
     * 总学时要求
     */
    private Integer totalLearnHours;

    /**
     * 总题量
     */
    private Integer totalTopicNumber;

    /**
     * 组卷策略-总分
     */
    private Integer totalScore;

    /**
     * 合格分数
     */
    private Integer passScore;

    /**
     * 考试时长-分钟
     */
    private Integer examTime;

    /**
     * 考试开启条件-
     */
    private Integer examOpenCondition;

    /**
     * 默认题目数量
     */
    private Integer topicNumber;

    /**
     * 只需要看完视频并完成答题，无论对错，都获得对应学时。
     * 正确率共3个数值：0%（两个题都错，也记录学时）
     * 50%，答对一题记录学时
     * 100% 答对2题记录学时
     * 答题通过率控制
     */
    private Integer passRatio;

    /**
     * 单位ID列表 接收参数
     */
    @TableField(exist = false)
    private List<String> unitIds;

    /**
     * 必选题设置
     */
    @TableField(exist = false)
    private List<String> requiredTopicIds;

    /**
     * 课程列表 接收参数
     */
    @TableField(exist = false)
    private List<ProjectRelate> lessonIds;

    /**
     * 受训人员列表 接收参数
     */
    @TableField(exist = false)
    private List<String> personIds;

    /**
     * 组卷策略详情
     */
    @TableField(exist = false)
    private List<ProjectTestPaperTactics> testPaperTactics;

    /**
     * 人员详情列表
     */
    @TableField(exist = false)
    private List<ProjectPersonList> projectPersonLists;

    /**
     * 课程详情列表
     */
    @TableField(exist = false)
    private List<ProjectCourseList> courseLists;

    /**
     * 预约考试状态  0-默认 1-已经预约
     */
    @TableField(exist = false)
    private Integer subscribeStatus;

    /**
     * 考试形式  1 - （考试）线上
     */
    @TableField(exist = false)
    private Integer testType;

    /**
     * 是否可以参加考试 0-否  1-是
     */
    @TableField(exist = false)
    private Integer isAttendStatus;

    /**
     * 考试用时 (分钟)
     */
    @TableField(exist = false)
    private Integer examinationTime;

    /**
     * 答题报告 ，用于移动端
     */
    @TableField(exist = false)
    private AnswerReport report;

    /**
     * 是否参加考试  1-是  0-否   移动端用于反参
     */
    @TableField(exist = false)
    private Integer isAttend;

    /**
     * 考试分数 （PC端已参加考试列表使用）
     */
    @TableField(exist = false)
    private BigDecimal score;

    @TableField(exist = false)
    /** 1-考试详情 2-预约考试(20220517丢弃) 3-进入考试*/
    private Integer examCondition;

    /**
     * 答卷时间
     */
    @TableField(exist = false)
    private String submitTime;

    /**
     * 准考证号
     */
    @TableField(exist = false)
    private String candidateNo;

    /* 是否禁用 （考试移动端 20220517）*/
    @TableField(exist = false)
    private Integer disable = 0;

    /**
     * 培训计划id
     */
    private Long trainPlanId;

    /**
     * 培训计划签到人数
     */
    @TableField(exist = false)
    private Integer signCount;

    /**
     * 是否可以补考
     */
    @TableField(exist = false)
    private Boolean makeUpExam;
}
