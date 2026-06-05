package com.joyfishs.dawa.practice.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.practice.entity.PracticeWrong;

@Mapper
public interface XmPracticeWrongMapper extends BaseMapper<PracticeWrong> {


    List<QuestionVo> findWrongList(@Param("personId") Long personId, @Param("courseType") String courseType);

    /*@Select("select b.id as question_id,b.question_name,b.question_type,b.answerA,b.answerB,b.answerC,b.answerD,b.answerE,b.answerF, " +
            "b.answer_parse,b.right_answers,a.my_answers, " +
            "c.course_type,d.name as course_type_name " +
            "from xm_practice_wrong a " +
            "left join xm_course_question b on a.question_id = b.id " +
            "left join xm_course c on b.course_id = c.id " +
            "left join sys_data_dictionary_item d on dictionary_code = '0006' and d.value = c.course_type and d.is_delete = 0 " +
            "where a.person_id = #{personId} " +
            "and b.is_delete = 0 " +
            "and c.is_delete = 0 " +
            "order by a.create_time desc ")
    List<QuestionVo> findWrongClassifyList(@Param("personId") Long personId);*/

    List<Map<String, Object>> findWrongClassifyList(@Param("personId") Long personId);

    @Select("select * from xm_practice_wrong where person_id = #{personId} and question_id = #{questionId} ")
    PracticeWrong getByPersonIdAndQuestionId(@Param("personId") Long personId, @Param("questionId") Long questionId);

    @Delete("delete from xm_practice_wrong where person_id = #{personId} and question_id = #{questionId}")
    void deleteByPersonIdAndQuestionId(@Param("personId") Long personId, @Param("questionId") Long questionId);

    QuestionVo getWrongByQuestionId(@Param("personId") Long personId, @Param("questionId") Long questionId);
}
