package com.joyfishs.dawa.student.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**

 * @description: 学员端我的课程
 */
@Data
@Accessors(chain = true)
public class MyCourseList {

	private Long id;

	/** 课程名 */
	private String name;

	/** 课程类别 1-必修课  2-选修课  3-终端培训*/
	private Integer trainType;

	/**
	 * 培训方式 1=培训  2=考试  3=培训+练习  4=培训+练习+考试
	 */
	private Integer trainWay;

	/** 课程开始时间 */
	@JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
	private Date startDate;

	/** 课程结束时间 */
	@JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN)
	private Date endDate;

	/** 状态 1-未开始  2-进行中 3-已完成  4-课程结束 */
	private Integer status;

	/** 进度 0 - 100之间 */
	private Integer schedule = 0;

	/** 学时 */
	private Double learnHours = 0.0D;

	/** 总课时 */
	private BigDecimal totalCourseHours;

	/** 培训种类名 培训计划的标签 */
	private String trainClassName;

	/** 是否添加进我的课程 */
	private boolean ifAdd;

	/** 得分 */
	private BigDecimal score;

	/** 是否合格 1是 0否*/
	private Integer isQualified = 0;
}
