package com.joyfishs.dawa.student.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.student.entity.ProjectPersonStudyRecord;

@Mapper
public interface ProjectPersonStudyRecordMapper extends BaseMapper<ProjectPersonStudyRecord> {
	/**
	 * 查询已获得的总课时
	 * @param projectId
	 * @param personId
	 * @return
	 */
	BigDecimal getObtainHours(@Param("projectId") Long projectId,
							  @Param("personId") Long personId);
	/**
	 * 获取项目下得分记录数量
	 * @param personId
	 * @param projectId
	 * @param courseId
	 * @param coursewareId
	 * @return
	 */
	Integer getRecordCount(@Param("personId") Long personId,
	                       @Param("projectId") Long projectId,
	                       @Param("courseId") Long courseId,
	                       @Param("coursewareId") Long coursewareId);

	/**
	 * 累计学时
	 * @param personId
	 * @return
	 */
	BigDecimal getSumStudyHours(@Param("personId") Long personId);

	/**
	 * 年度学时
	 * @param personId
	 * @return
	 */
	BigDecimal getYearSumStudyHours(@Param("personId") Long personId);

	// 获取阅读公告的总学时
	BigDecimal getNoticeStudyHours(@Param("personId") Long personId);
}
