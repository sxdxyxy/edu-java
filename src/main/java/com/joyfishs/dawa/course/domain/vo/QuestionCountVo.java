package com.joyfishs.dawa.course.domain.vo;

import lombok.Data;

/**
 * @description: 项目管理组卷详情返回
 */
@Data
public class QuestionCountVo {
	Integer i;
	Integer topicType; //题目类型
	Integer totalNum; //数量
}
