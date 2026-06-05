package com.joyfishs.dawa.student.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;

/**
 * @description: 课程（项目）详情
 */
@Data
public class CourseDetail {
	Long id;
	/** 1-选修  2-必修 */
	Integer trainType;

	/** 项目名 */
	String name;

	/** 主讲老师 */
	String lecturer;

	/** 开课周期开始时间 */
	@JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN,timezone="GMT+8")
	Date trainSdate;

	/** 开课周期结束时间 */
	@JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN,timezone="GMT+8")
	Date trainEdate;

	/** 总课时 */
	Integer learnHours;

	/** 单课分钟数 */
	Integer courseDuration = 45;

	/** 课程概述（介绍） */
	String outline;

	/** 图片 */
	String courseImage;

	/** 创建人 */
	String createName;

	/** 状态 1-未开始  2-正在学  3-已学完 */
	Integer status = 1;
}
