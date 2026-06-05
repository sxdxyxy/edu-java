package com.joyfishs.dawa.project.domain.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @program: dawa-java
 * @description: 项目管理人员列表
 * @author: Yjhon
 * @create: 2021-08-25 16:17
 */
@Data
@Accessors(chain = true)
public class ProjectPersonList implements Serializable {
	Integer id;
	String name;
	String userName;
	Long orgId;
	String company;  //公司
	String department; //部门
	String jobLevel;  //职务级别
	String role;  //受训角色  详情使用
}
