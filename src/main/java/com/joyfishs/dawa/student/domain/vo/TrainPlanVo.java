package com.joyfishs.dawa.student.domain.vo;

import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.utils.SpringUtil;

import lombok.Data;

/**
 * @program: dawa-java
 * @description: 培训计划列表对象
 * @author: Yjhon
 * @create: 2022-03-15 11:10
 */
@Data
public class TrainPlanVo {
	/** 计划id */
	Long id;
	/** 计划名称 */
	String name;
	/** 发布单位id */
	Long orgId;
	/** 发布单位名称 */
	String orgName;
	/** 年度 */
	Integer year;

	/** 1-进行中 2-过期 */
	Integer state;

	/** 单位名称 */
	public String getOrgName() {
		if(this.orgId == 0l) return "系统管理员";
		String name = SpringUtil.getBean(SysOrgService.class).getById(this.orgId).getName();
		return name.isEmpty() ? "" : name;
	}
}
