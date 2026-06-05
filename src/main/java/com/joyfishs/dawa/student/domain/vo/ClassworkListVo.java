package com.joyfishs.dawa.student.domain.vo;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 课程作业列表视图
 */
@Data
@Accessors(chain = true)
public class ClassworkListVo {
	Long id;

	/** 作业名称 */
	String name;

	/** 提交时间 */
	Date submitTime;

	/** 状态 1-未开始 2-继续 3-已提交 */
	Integer status;

	/** 出题范围 */
	String questionScope;
}
