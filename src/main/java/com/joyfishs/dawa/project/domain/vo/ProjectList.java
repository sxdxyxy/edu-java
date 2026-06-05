package com.joyfishs.dawa.project.domain.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: dawa-java
 * @description: 项目列表对象
 * @author: Yjhon
 * @create: 2021-08-24 10:39
 */
@Data
@Accessors(chain = true)
public class ProjectList implements Serializable {
	String id;
	String projectName;
	@JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
	Date startDate;
	@JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
	Date endDate;
	Integer personNum;
	Integer signNum;
	String projectType;
	String trainType; //培训类型 1-必修课，2-选修课
	String trainWay;  //培训方式
	String status;  ///** 项目状态 1-未发布 2-未开始 3-进行中  4-已完成  5-已中止 */
	Boolean later;
	String createBy;
	@JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
	Date createDate;
}
