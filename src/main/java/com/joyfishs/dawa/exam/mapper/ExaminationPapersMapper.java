package com.joyfishs.dawa.exam.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.entity.AnswerReportItem;
import com.joyfishs.dawa.exam.domain.ExamQuestionVO;
import com.joyfishs.dawa.exam.entity.ExaminationPapers;
import com.joyfishs.dawa.exam.entity.StudentSubscribeExam;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.student.domain.vo.MyClassworkListVo;
import com.joyfishs.dawa.student.entity.ProjectPersonStudyRecord;

/**
 * @Author xiaodai
 * @create 2022/1/8 16:33
 */

@Mapper
public interface ExaminationPapersMapper extends BaseMapper<ExaminationPapers> {

    /**
     * 通过学生ID和项目ID查询 学生学习记录
     * @param personId 人员ID
     * @param projectId 项目ID
     * @return
     */
    ProjectPersonStudyRecord findStudentRecord(@Param("personId") Long personId, @Param("projectId") Long projectId);

    /**
     * 查询 人员预约状态
     * @param personId 人员ID
     * @param projectId 项目ID
     * @return
     */
    StudentSubscribeExam findPersonSubscriptExamStatus(@Param("personId") Long personId, @Param("projectId") Long projectId);

    List<Project> findExamAll(@Param("orgId") Long orgId,
                              @Param("examClassify") Integer examClassify,
                              @Param("personId") Long personId,
                              @Param("status") Integer status);

    /**
     * 通过人员ID和项目ID获取考试详情列表
     * @param personId 人员ID
     * @param projectId 项目ID
     * @return
     */
    List<AnswerReport> findExamQuestion(@Param("personId") Long personId, @Param("projectId") Long projectId, @Param("examType") Integer examType);

    List<AnswerReportItem> findByReporId(@Param("reportId") Long reportRd);


    /**
     * 通过题目Ids 获取 题目类别，此题的分数，此题的正确答案
     * @param projectId
     * @param questionIds
     * @return
     */
    List<ExamQuestionVO> findExamQuestionDetail(@Param("projectId") Long projectId, @Param("questionIds") List<Long> questionIds);

    List<Project> findAttendExamAll(@Param("orgId") Long orgId, @Param("examClassify")Integer examClassify, @Param("personId") Long personId, @Param("type") Integer type);

    List<AnswerReport> findReport(@Param("personId") Long personId, @Param("projectId") Long projectId);

    BigDecimal getBeat(@Param("projectId") Long projectId, @Param("score") BigDecimal score);

    BigDecimal getAvg(Long projectId);

    List<Project> findMobileExamList(@Param("examClassify") Integer examClassify, @Param("status") Integer status, @Param("personId") Long personId);

    /** 查询作业列表 */
    List<MyClassworkListVo> getClassworkList(@Param("personId") Long personId,
                                             @Param("projectId") Long projectId);
}
