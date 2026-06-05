package com.joyfishs.dawa.student.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 学员端-我的课程-学习-目录
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "StudentCourseCatalogue", description = "课程目录")
public class StudentCourseCatalogue {
	Long id;
	@ApiModelProperty("名字")
	String name;

	@ApiModelProperty("类型 1-课程 2-课件 3-视频 ")
	Integer type;

	/** 1: 视频, 2: pdf */
	Integer courseWay = 1;

	@ApiModelProperty("状态 0-未学 1-已学 ")
	Integer status = 0;

	@ApiModelProperty("下级list")
	List<StudentCourseCatalogue> courseList;

	@ApiModelProperty("封面图路径")
	String coverPath;

	@ApiModelProperty("视频地址")
	String videoAddress;

	@ApiModelProperty("腾讯云vod文件id")
	private String fileId;

	@ApiModelProperty("是否合作方课件")
	private Boolean thirdParty;

	@ApiModelProperty("简介")
	String content;

	@ApiModelProperty("视频时长")
	String duration;
}

