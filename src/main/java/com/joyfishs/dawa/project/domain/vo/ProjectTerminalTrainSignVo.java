package com.joyfishs.dawa.project.domain.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 签到和参会人员VO
 * @create: 2021-11-03 20:36
 */
@Data
@Accessors(chain = true)
public class ProjectTerminalTrainSignVo implements Serializable {
	Long id;
	String trainName; // 培训名称
	Long personId; //人员id
	String name; //人员名称
	String department; //部门
	String position; //职位
	@JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
	Date trainSdate;// 培训开始时间
	Date trainEdate;// 培训结束时间
	Date signTime; //签到时间
	Integer signStatus; //签到状态 0-无状态 1-正常  2-迟到  3-未签到
	Integer enrollStatus; //报名状态 0-未报名 1-已报名 2-取消报名
	boolean disabled; // 禁止操作项
}
