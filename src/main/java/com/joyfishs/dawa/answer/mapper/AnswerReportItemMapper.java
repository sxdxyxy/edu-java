package com.joyfishs.dawa.answer.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.answer.entity.AnswerReportItem;
import com.joyfishs.dawa.answer.vo.QuestionVo;

@Mapper
public interface AnswerReportItemMapper extends BaseMapper<AnswerReportItem> {

    /**
     * 根据ReportId 查询详情列表
     * @param reportId
     * @return
     */
    List<AnswerReportItem> listByReportId(@Param("reportId") Long reportId);

    /**
     * 根据
     * @param reportId
     * @param answerType
     * @return
     */
    List<QuestionVo> listResolutionByReportId(@Param("reportId") Long reportId, @Param("answerType") Integer answerType);

    /**
     * 查询正确率
     *
     * @param questionId
     * @return
     */
   BigDecimal getAccuracyRate(@Param("questionId") Long questionId);

    /** 查询易错项
     *
     * @param questionId
     * @return
     */
    String getWrongItem(@Param("questionId") Long questionId);

    /**
     * 练习-收藏、错题集 根据题目id查询答题时间
     *
     * @param personId
     * @param questionId
     * @return
     */
    int getAnswerTimeByPersonIdAndQuestionId(@Param("personId") Long personId, @Param("questionId") Long questionId);

    /*// 查询未答题题目数量
    @Select("select count(1) from xm_answer_report_item where report_id = #{reportId} and answer_type = 0 ")
    int getCountUnanswered(@Param("reportId") Long reportId);*/

    /**
     * 根据报告id删除答题卡明细
     *
     * @param reportId
     */
    void deleteByReportId(@Param("reportId") Long reportId);

}
