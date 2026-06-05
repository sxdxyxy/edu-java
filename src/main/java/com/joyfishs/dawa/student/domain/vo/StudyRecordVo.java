package com.joyfishs.dawa.student.domain.vo;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: dawa-java
 * @description: 学生端学习记录
 * @author: Yjhon
 * @create: 2022-02-17 18:31
 */
@Data
@Accessors(chain = true)
public class StudyRecordVo {
	/** 项目id */
	Long id;

	/** 项目名 */
	String projectName;

	/** 学习数量 */
	Integer studyNum;

	/** 最后学习时间 */
	Date studyDate;

	/** 学习状态 1-未开始 2-进行中 3-已学完 */
	Integer studyStatus;
}
