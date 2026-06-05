package com.joyfishs.dawa.answer.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.answer.entity.AnswerReport;

@Mapper
public interface AnswerReportMapper extends BaseMapper<AnswerReport> {

    /**
     * 根据当前得分，查击败率
     */
    BigDecimal getBeat(@Param("score") BigDecimal score, @Param("courseId") Long courseId);

    /**
     * 查平均得分
     *
     * @param courseId
     * @return
     */
    BigDecimal getAvgScore(@Param("courseId") Long courseId);

    /**
     * 查项目下人员的模拟考试
     *
     * @param projectId
     * @param personId
     * @return
     */
    List<AnswerReport> findMockTestList(@Param("projectId") Long projectId, @Param("personId") Long personId);

    /**
     * 根据人员id和项目id查询 考试成绩、考试耗时、考试状态
     *
     * @param personId
     * @param projectId
     * @return
     */
    Map<String, Object> findExamResult(@Param("personId") Long personId, @Param("projectId") Long projectId);

    /**
     * 根据人员id和项目id/课程id查询练习结果
     *
     * @param personId
     * @param projectId
     * @param courseId
     * @return
     */
    Map<String, Object> findPracticeResult(@Param("personId") Long personId, @Param("projectId") Long projectId, @Param("courseId") Long courseId);

    /**
     *
     * @param projectId
     * @return
     */
    Integer getQuestionCountByProjectId(@Param("projectId") Long projectId);


    Integer getQuestionCountByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询最后一次考试
     * @param personId
     * @param projectId
     * @return
     */
    AnswerReport getMaxScore(@Param("personId") Long personId, @Param("projectId") Long projectId);
}
