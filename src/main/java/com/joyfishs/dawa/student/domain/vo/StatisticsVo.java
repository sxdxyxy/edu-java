package com.joyfishs.dawa.student.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.joyfishs.dawa.student.domain.StudentCourseCatalogue;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: dawa-java
 * @description: 课程统计
 * @author: Yjhon
 * @create: 2022-01-10 01:10
 */
@Data
@Accessors(chain = true)
public class StatisticsVo {
	Long projectId; //项目id
	String projectName; //项目名
	BigDecimal totalHours; //总学时
	BigDecimal learnedHours; //已得学时
	String status; //状态
	Date startDate; //学习开始时间
	Date endDate; //学习结束时间
	Integer trainWay; //培训方式
	List<StudentCourseCatalogue> details;
}
