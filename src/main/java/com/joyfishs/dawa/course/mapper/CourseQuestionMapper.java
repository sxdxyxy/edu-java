package com.joyfishs.dawa.course.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.course.domain.vo.QuestionCountVo;
import com.joyfishs.dawa.course.entity.CourseQuestion;
import com.joyfishs.dawa.project.entity.ProjectTestPaperTactics;

/**
 * <p>
 * 考试题目表 Mapper 接口
 * </p>
 *
 * @author xiaodai
 * @since 2021-08-17
 */
@Mapper
public interface CourseQuestionMapper extends BaseMapper<CourseQuestion> {

    /**
     * 查询题目列表
     * @param t
     * @return
     */
    List<CourseQuestion> questionList(@Param("t") CourseQuestion t);

    /**
     * 通过课程ID获取题目ID
     * @param id 课程ID
     * @return 题目ID
     */
    @Select("SELECT id FROM xm_course_question WHERE is_delete=0 and course_id=#{courseId}")
    List<Long> selectIdList(@Param("courseId") Long id);

    /** 查询题目数量 */
    @Select("SELECT count(1) FROM xm_course_question WHERE is_delete=0 and course_id=#{courseId}")
	Integer getQuestionCount(@Param("courseId") Long courseId);

    /** 查询课程各类别的题目数量 */
	List<QuestionCountVo> getQuestionNumByCourseIds(@Param("list") List<Long> ids);

    /**
     * 通过课程ID获取课程下的所有题目
     * @param courseId 课程ID
     * @return
     */
    @Select("SELECT * FROM xm_course_question WHERE is_delete = 0 and course_id=#{courseId}")
    List<CourseQuestion> findQuestionByCourseId(@Param("courseId") Long courseId);

    @Select("SELECT * FROM xm_course_question WHERE id in(${questionId}) and is_delete = 0")
    List<CourseQuestion> getByIdIN(@Param("questionId") String questionId);

    @Select("SELECT * FROM xm_course_question WHERE course_id in(${courseIds}) and is_delete = 0")
    List<CourseQuestion> findByCourseIdIN(@Param("courseIds") String courseIds);

    List<ProjectTestPaperTactics> findGroupCount(@Param("projectId") Long projectId);

    List<Long> findByQuestionType(@Param("questionType") Integer questionType, @Param("courseIds") List<String> courseIds);

    // 根据课程id查询练习题目
    List<QuestionVo> findPracticeQuestion(@Param("courseId") Long courseId);

    List<QuestionVo> findByQuestionByIdIN(@Param("questionAll") List<Long> questionAll);

    /** 题目id查询题目(小丽提供的实体) */
    QuestionVo getQuestionVo(@Param("id") Long id);
}
