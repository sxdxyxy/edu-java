package com.joyfishs.dawa.student.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: dawa-java
 * @description: 我的作业列表对象
 * @author: Yjhon
 * @create: 2022-02-18 17:32
 */
@Data
@Accessors(chain = true)
public class MyClassworkListVo {
	Long id;

	/** 作业名 */
	String name="我的作业";

	/** 提交时间 */
	@JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
	Date submitTime;

	/** 出题范围 */
	String RangeOfQuestions = "当前项目";

	/** 1-已提交 2-未提交 */
	Integer status = 1;
}
