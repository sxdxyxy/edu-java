package com.joyfishs.dawa.student.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.student.domain.vo.StudyRecordVo;
import com.joyfishs.dawa.student.entity.PersonCourseRelate;

@Mapper
public interface PersonCourseRelateMapper extends BaseMapper<PersonCourseRelate> {

	/**
	 * 查询人员课程关联记录
	 * @param projectId
	 * @param personId
	 * @return
	 */
	PersonCourseRelate getRecord(@Param("projectId") Long projectId,
								 @Param("personId") Long personId);

	/** 查询学习记录 */
	List<StudyRecordVo> getStudyRecord(Long personId);

	/** 查询学习完成度 0 - 100 */
	int getStudyStatus(@Param("personId") Long personId,@Param("projectId") Long projectId);

	/** 删除项目下所有人员关联信息 */
	@Delete("delete from xm_person_course_relate where project_id = #{id}")
	void removeByProjectId(Long id);

	/** 删除项目下所有人员学习记录信息 */
	@Delete("delete from xm_project_person_study_record where project_id = #{id}")
	void removeRecordByProjectId(Long id);

	/** 获取项目下所有人员Id */
	@Select("select person_id from xm_person_course_relate where project_id = #{id} and is_delete = 0 ")
	List<Long> listPersonIdByProjectId(Long id);

	Date getStudyDate(@Param("tag") String tag,@Param("personId") Long personId,@Param("projectId")  Long projectId);

	/**
	 * 结业凭证列表
	 * @param personId
	 * @return
	 */
	List<Map<String, String>> getGraduationList(Long personId);
}
