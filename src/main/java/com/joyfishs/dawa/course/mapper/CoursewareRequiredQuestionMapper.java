package com.joyfishs.dawa.course.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.course.entity.CoursewareRequiredQuestion;

/**
 * @Author xiaodai
 * @create 2021/12/17 17:27
 */

@Mapper
public interface CoursewareRequiredQuestionMapper extends BaseMapper<CoursewareRequiredQuestion> {

    /**
     * 通过题目路ID和课件ID获取 课件下的必选题
     * @param questionId 题目ID
     * @param coursewareId 课件ID
     * @return
     */
    @Select("SELECT * FROM xm_courseware_required_question WHERE question_id = #{questionId} and courseware_id = #{coursewareId} and is_delete=0")
    CoursewareRequiredQuestion findRequiredQuestion(@Param("questionId") Long questionId, @Param("coursewareId") Long coursewareId);

    /**
     * 通过课件ID获取所有未删除的 必选题题目ID
     * @param coursewareId 课件ID
     * @return 必选题题目ID
     */
    @Select("SELECT question_id FROM xm_courseware_required_question WHERE courseware_id = #{coursewareId} and is_delete = 0")
    List<Long> findQuestionIdAllByCoursewareId(@Param("coursewareId") Long coursewareId);



    /**
     * 通过课件ID获取所有未删除的,排除部分 必选题题目ID
     * @param coursewareId 课件ID
     * @return 必选题题目ID
     */
    @Select(" <script> " +
            " SELECT a.question_id  " +
            " FROM xm_courseware_required_question a  " +
            "         left join xm_course_question b on a.question_id = b.id  " +
            " WHERE a.courseware_id = #{coursewareId}  " +
            "  and a.is_delete = 0  " +
            "  and b.is_delete = 0  " +
            "  and b.question_type not in (4, 5)  " +
            " <if test='questionIds != null and  questionIds.size() > 0'> " +
            "    and a.question_id not in " +
            "    <foreach item='item' collection='questionIds' separator=',' open='(' close=')' index='index'> " +
            "         #{item} " +
            "    </foreach> " +
            " </if>" +
            " </script>")
    List<Long> findAllQuestionIdByCoursewareId(@Param("coursewareId") Long coursewareId,@Param("questionIds") List<Long> questionIds);


    @Update("update xm_courseware_required_question set is_delete = 1 WHERE courseware_id = #{coursewareId} and question_id in(${different}) and is_delete = 0 ")
    void delCancelRequiredQuestion(@Param("coursewareId") Long coursewareId, @Param("different") String different);
}
