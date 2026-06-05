package com.joyfishs.dawa.answer.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.entity.AnswerReportItem;
import com.joyfishs.dawa.answer.mapper.AnswerReportMapper;
import com.joyfishs.dawa.answer.vo.AnswerQuestionVo;
import com.joyfishs.dawa.answer.vo.AnswerVo;
import com.joyfishs.dawa.answer.vo.QuestionVo;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.exam.service.ExamService;
import com.joyfishs.dawa.practice.entity.PracticeCollection;
import com.joyfishs.dawa.practice.service.XmPracticeCollectionService;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.utils.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnswerReportService extends ServiceImpl<AnswerReportMapper, AnswerReport> {

    @Autowired
    private AnswerReportItemService reportItemService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ExamService examService;

    @Autowired
    private XmPracticeCollectionService xmPracticeCollectionService;

    // 查看答题报告
    public AnswerReport viewReport(Long reportId) {
        AnswerReport report = this.getById(reportId);
        if (null == report) throw new CustomException("答题报告不存在");

        // 查询答题卡明细
        List<AnswerReportItem> reportItems = reportItemService.getBaseMapper().listByReportId(reportId);
        report.setReportItems(reportItems);

        // 1:课程 2:考试
        if (report.getType() == 1 || report.getType() == 3) {
            //获取课程名称
            Course course = courseService.getById(report.getCourseId());
            report.setCourseName(course != null ? course.getCourseName() : "");

            // 查询击败比例
            BigDecimal beat = baseMapper.getBeat(report.getScore(), report.getCourseId());
            report.setBeat(beat);

            // 查询全站平均得分
            BigDecimal avgScore = baseMapper.getAvgScore(report.getCourseId());
            report.setAvgScore(avgScore);
        } else if (report.getType() == 2) {
            //获取项目（考试）名称
            report.setCourseName(projectService.getById(report.getProjectId()).getProjectName());

            // 查询击败比例
            report.setBeat(examService.getBaseMapper().getBeat(report.getProjectId(), report.getScore()));

            // 查询全站平均得分
            report.setAvgScore(examService.getBaseMapper().getAvg(report.getProjectId()));
        }
        return report;
    }

    /**
     * 查看答题解析
     *
     * @param reportId
     * @param type     1: 全部解析  2:错题解析
     * @return
     */
    public AnswerVo viewResolution(Long reportId, Integer type) {
        AnswerReport report = this.getById(reportId);
        if (null == report) throw new CustomException("答题报告不存在");

        // 根据报告查询答题解析
        List<QuestionVo> reportItems = new ArrayList<>();
        if (type == null || type == 1) {
            reportItems = reportItemService.getBaseMapper().listResolutionByReportId(reportId, null);
        } else if (type == 2) {
            reportItems = reportItemService.getBaseMapper().listResolutionByReportId(reportId, 2);
        }

        // 根据答题类型分类
        Map<String, List<QuestionVo>> questionMap = reportItems.stream().collect(
                Collectors.groupingBy(
                        questionVo -> questionVo.getQuestionType()
                ));

        List<AnswerQuestionVo> answerQuestionVoList = new ArrayList<>();
        for (Map.Entry<String, List<QuestionVo>> entry : questionMap.entrySet()) {
            String questionType = entry.getKey();
            List<QuestionVo> voList = entry.getValue();
            int i = 0;
            int answeredCount = 0;
            for (QuestionVo questionVo : voList) {
                questionVo.setOrderNumber(++i);
                BigDecimal accuracyRate = reportItemService.getBaseMapper().getAccuracyRate(questionVo.getQuestionId());
                String wrongItem = reportItemService.getBaseMapper().getWrongItem(questionVo.getQuestionId());
                questionVo.setAccuracyRate(accuracyRate);
                if (StrUtil.isNotBlank(wrongItem)) {
                    questionVo.setWrongItem(wrongItem);
                }
                // 查询该题目是否已收藏
                PracticeCollection collection = xmPracticeCollectionService.getBaseMapper().getByPersonIdAndQuestionId(report.getPersonId(), questionVo.getQuestionId());
                questionVo.setIsCollection(collection != null ? 1 : 0);

                if (questionVo.getAnswerType() > 0) {
                    answeredCount++;
                }
            }
            AnswerQuestionVo answerQuestionVo = new AnswerQuestionVo();
            answerQuestionVo.setQuestionType(questionType);
            answerQuestionVo.setQuestionCount(voList.size());
            answerQuestionVo.setAnsweredCount(answeredCount);
            answerQuestionVo.setQuestionList(voList);
            answerQuestionVoList.add(answerQuestionVo);
        }


        AnswerVo answerVo = new AnswerVo();
        Course course = courseService.getById(report.getCourseId());
        answerVo.setCourseName(course != null ? course.getCourseName() : "");
        answerVo.setQuestion(answerQuestionVoList);
        return answerVo;
    }

    /**
     * 结业凭证查询最后一次考试分数
     */
    public AnswerReport getMaxScore(Long personId, Long projectId) {
        return baseMapper.getMaxScore(personId, projectId);
    }
}
