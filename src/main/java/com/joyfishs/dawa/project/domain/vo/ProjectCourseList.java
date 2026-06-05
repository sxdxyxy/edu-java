package com.joyfishs.dawa.project.domain.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 项目课程详情
 */
@Data
public class ProjectCourseList implements Serializable {
	private Long id;

	private Long projectRelateId;

	@ApiModelProperty("课程编号")
	private String courseCode;

	@ApiModelProperty("课程名字")
	private String courseName;

	@ApiModelProperty("课时")
	private String courseHours;

	@ApiModelProperty("学时要求")
	private String learnHours;

	@ApiModelProperty("题目数量")
	private Integer topicNumber;

	@ApiModelProperty("排序字段")
	private Integer sort;
}
