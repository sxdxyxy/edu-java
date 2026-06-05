package com.joyfishs.dawa.student.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 我的课程页面传输对象
 */
@Data
@Accessors(chain = true)
public class MyCourseParamsTo {
	Long id;
	Long projectId;  //项目id
	Long courseId;  //课程id
	Long coursewareId;  //课件id
	Integer status;  //状态
	Long personId; //人员id
}
