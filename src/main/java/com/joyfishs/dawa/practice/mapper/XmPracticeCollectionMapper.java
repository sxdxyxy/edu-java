package com.joyfishs.dawa.practice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.practice.entity.PracticeCollection;

@Mapper
public interface XmPracticeCollectionMapper extends BaseMapper<PracticeCollection> {

    List<QuestionVo> findCollectionList(@Param("personId") Long personId);

    @Select("select * from xm_practice_collection where person_id = #{personId} and question_id = #{questionId} ")
    PracticeCollection getByPersonIdAndQuestionId(@Param("personId") Long personId, @Param("questionId") Long questionId);

    @Delete("delete from xm_practice_collection where person_id = #{personId} and question_id = #{questionId}")
    void deleteByPersonIdAndQuestionId(@Param("personId") Long personId, @Param("questionId") Long questionId);

    QuestionVo getCollectionByQuestionId(@Param("personId") Long personId, @Param("questionId") Long questionId);
}
