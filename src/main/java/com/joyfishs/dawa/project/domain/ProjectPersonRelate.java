package com.joyfishs.dawa.project.domain;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ykfnb
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ProjectPersonRelate", description = "项目人员关系")
public class ProjectPersonRelate {
	Long id;

	/** 状态 */
	Integer status;

	/** 项目id */
	Long projectId;

	/** 当前课程id */
	Long currentCourseId;

	/** 当前课件id */
	Long currentCoursewareId;

	/** 人员id */
	Long personId;

	/** 当前项目已获得的学分 */
	BigDecimal credit;
}
