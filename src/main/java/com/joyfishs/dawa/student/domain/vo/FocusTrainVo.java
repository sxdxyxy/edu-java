package com.joyfishs.dawa.student.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 集中培训列表视图对象
 */
@Data
@Accessors(chain = true)
public class FocusTrainVo {
	Integer id;
	/** 名称 */
	String trainName;

	/** 开始时间 */
	@JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN,timezone = "GMT+8")
	Date trainSdate;

	/** 结束时间 */
	@JsonFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN,timezone = "GMT+8")
	Date trainEdate;

	/** 培训地址 */
	String trainAddress;

	/** 详情-学习内容 */
	String learningContent;

	/** 状态 2-报名中 3-进行中 4-已结束 */
	Integer status;

	/** 报名状态 1-未报名 2- 已报名 3-已签到 */
	Integer signStatus = 1;

	/** 培训人数 */
	Integer personNum;

	/** 已报名人数 */
	Integer signNum;

	/** 签到方式 1-扫码签到 */
	Integer signWay = 1;

	/** 联系人 */
	String contact;

	/** 联系电话 */
	String phone;
}
