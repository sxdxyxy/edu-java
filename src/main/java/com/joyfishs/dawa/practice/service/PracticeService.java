package com.joyfishs.dawa.practice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.entity.AnswerReportItem;
import com.joyfishs.dawa.answer.service.AnswerReportItemService;
import com.joyfishs.dawa.answer.service.AnswerReportService;
import com.joyfishs.dawa.answer.vo.*;
import com.joyfishs.dawa.course.service.CourseQuestionService;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.exam.service.ExamService;
import com.joyfishs.dawa.practice.entity.PracticeCollection;
import com.joyfishs.dawa.practice.entity.PracticeRecord;
import com.joyfishs.dawa.practice.entity.PracticeWrong;
import com.joyfishs.dawa.practice.vo.PracticeVo;
import com.joyfishs.system.config.redis.RedisCache;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PracticeService {

    @Autowired
    private CourseQuestionService courseQuestionService;

    @Autowired
    private XmPracticeRecordService xmPracticeRecordService;

    @Autowired
    private AnswerReportService answerReportService;

    @Autowired
    private AnswerReportItemService answerReportItemService;

    @Autowired
    private XmPracticeWrongService xmPracticeWrongService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private XmPracticeCollectionService xmPracticeCollectionService;

    @Autowired
    private ExamService examService;

    @Autowired
    private RedisCache redisCache;

    private static final String CACHE_KEY_PRACTICE_LIST = "PracticeListKey";

    /**
     * 我的练习列表
     * @param type
     * @param name
     * @param classify
     * @return
     */
    public Map<String, List<PracticeVo>> findPracticeList(String type, String name, Integer classify) {
        Long personId = SecurityUtil.getPersonId();
        Map<String, List<PracticeVo>> practiceMap = redisCache.getCacheObject(CACHE_KEY_PRACTICE_LIST);
        if (CollUtil.isNotEmpty(practiceMap)) {
            return practiceMap;
        }
//        if(classify == null || classify == 1){
//            //我的练习列表
//            practiceList = xmProjectRelateService.getBaseMapper().findPracticeListByPersonId(personId, type, name);
//        } else {
//            //todo 我的作业列表,预留
//            practiceList = xmProjectRelateService.getBaseMapper().findPracticeListByPersonId(personId, type, name);
//        }
        // 2022-03-09 修改为 我的练习显示平台所有课程的习题 不关联项目
        List<PracticeVo> practiceList = xmPracticeRecordService.getBaseMapper().findPracticeList(type, name);

        // 查看练习完成情况
        for (PracticeVo practice : practiceList) {
            // 题数
            int questionCount = courseQuestionService.getQuestionCount(practice.getCourseId());

            PracticeRecord practiceRecord = xmPracticeRecordService.getBaseMapper().getByPersonAndCourseId(personId, practice.getCourseId());
            int count = 0; // 未答题数
            // 练习类型 1:未练习 2:未完成 3:已完成
            if (null == practiceRecord) {
                practice.setType(1);
            } else {
                // 查询答题卡完成情况
                AnswerReport report = answerReportService.getById(practiceRecord.getReportId());
                count = report.getTotalQuestions() - report.getAnsweredQuestions();
                practice.setType(count > 0 ? 2 : 3);
                practice.setAnsweredCount(report.getAnsweredQuestions());
            }
            practice.setQuestionCount(questionCount);
        }

        // 根据课程分类 分类
        practiceMap = practiceList.stream()
                .filter(practiceVo -> practiceVo.getPracticeTypeName() != null)
                .collect(Collectors.groupingBy(practiceVo -> practiceVo.getPracticeTypeName()));
        redisCache.setCacheObject(CACHE_KEY_PRACTICE_LIST, practiceMap, 30, TimeUnit.MINUTES);
        return practiceMap;
    }

    /**
     * 练习-开始答题-查询题目
     * @param courseId
     * @param type 1:新答题 2:继续答题
     * @return
     */
    public AnswerVo getPracticeDetail(Long courseId, Integer type) {
        Long personId = SecurityUtil.getPersonId();

        // 查询课程对应练习题目
//        List<QuestionVo> questionList = xmCourseQuestionService.getBaseMapper().findPracticeQuestion(courseId);
        List<QuestionVo> questionList = new ArrayList<>();

        // 查询当前学员是否练习过
        PracticeRecord practiceRecord = xmPracticeRecordService.getBaseMapper().getByPersonAndCourseId(personId, courseId);
        if (null != practiceRecord && 2 == type) {
            // 如果已经练习过,且选择继续答题,则返回已答题过的选项
            List<AnswerReportItem> reportItemList = answerReportItemService.getBaseMapper().listByReportId(practiceRecord.getReportId());
            List<Long> oldQuestionIdList = reportItemList.stream().map(i -> i.getQuestionId()).collect(Collectors.toList());
            questionList = courseQuestionService.getBaseMapper().findByQuestionByIdIN(oldQuestionIdList);
            for (QuestionVo question : questionList) {
                for (AnswerReportItem reportItem : reportItemList) {
                    if (reportItem.getQuestionId() == question.getQuestionId()) {
                        question.setMyAnswers(reportItem.getMyAnswers());
                        break;
                    }
                }
            }
        } else {
            // 2022-05-12 修改为随机抽选15题
            List<Long> idList = examService.findRandomSelectionQuestionId(null, 15, CollectionUtil.newArrayList(String.valueOf(courseId)));
            questionList = courseQuestionService.getBaseMapper().findByQuestionByIdIN(idList);
        }

        // 根据答题类型分类
        Map<String, List<QuestionVo>> questionMap = questionList.stream().collect(
                Collectors.groupingBy(
                        questionVo -> questionVo.getQuestionType()
                ));

        List<AnswerQuestionVo> answerQuestionVoList = new ArrayList<>();
        for (Map.Entry<String, List<QuestionVo>> entry : questionMap.entrySet()) {
            String questionType = entry.getKey();
            List<QuestionVo> voList = entry.getValue();
            int i = 0;
            for (QuestionVo questionVo : voList) {
                questionVo.setOrderNumber(++i);
                // 查询该题目是否已收藏
                PracticeCollection collection = xmPracticeCollectionService.getBaseMapper().getByPersonIdAndQuestionId(personId, questionVo.getQuestionId());
                questionVo.setIsCollection(collection != null ? 1 : 0);
            }
            AnswerQuestionVo answerQuestionVo = new AnswerQuestionVo();
            answerQuestionVo.setQuestionType(questionType);
            answerQuestionVo.setQuestionList(voList);
            answerQuestionVoList.add(answerQuestionVo);
        }


        AnswerVo answerVo = new AnswerVo();
        answerVo.setCourseName(courseService.getById(courseId).getCourseName());
        answerVo.setQuestion(answerQuestionVoList);
        return answerVo;
    }

    @Transactional
    public Long executeSubmit(SubmitPaperParams paperParams) {
        Long personId = SecurityUtil.getPersonId();
        Long projectId = paperParams.getProjectId();
        Long courseId = paperParams.getCourseId();

        PracticeRecord practiceRecord = null;
        AnswerReport answerReport;
        int type = paperParams.getType();
        if (type == 2) { //2:继续答题
            practiceRecord = xmPracticeRecordService.getBaseMapper().getByPersonAndCourseId(personId, courseId);
            if (null == practiceRecord) throw new CustomException("未查询到练习记录");
            answerReport = answerReportService.getById(practiceRecord.getReportId());
            if (null == answerReport) throw new CustomException("未查询到答题报告");
            // 如果继续答题,则删除答题卡明细,重新生成新的
            answerReportItemService.getBaseMapper().deleteByReportId(answerReport.getId());
        } else {
            if(type == 1){ //1:新答题
                practiceRecord = new PracticeRecord();
                practiceRecord.setPersonId(personId);
                practiceRecord.setProjectId(projectId);
                practiceRecord.setCourseId(courseId);
                practiceRecord.setCreateTime(new Date());
            }

            answerReport = new AnswerReport();
            answerReport.setPersonId(personId);
            answerReport.setProjectId(projectId);
            answerReport.setCourseId(courseId);
            answerReport.setType(type == 4?type:3); // 答题报告 类型为练习
            answerReport.setCreateTime(new Date());
            answerReport.setCreateBy(SecurityUtil.getUserId());
        }

        answerReport.setAnswerTime(paperParams.getAnswerTime());
        answerReport.setSubmitTime(new Date());
        answerReportService.saveOrUpdate(answerReport);

        if(practiceRecord != null){
            practiceRecord.setPracticeTime(new Date());
            practiceRecord.setReportId(answerReport.getId());
            xmPracticeRecordService.saveOrUpdate(practiceRecord);
        }

        // 答题卡明细集合
        List<AnswerReportItem> reportItemList = new ArrayList<>();
        // 计算答对题数
//        int totalQuestions = xmCourseQuestionService.getQuestionCount(paperParams.getCourseId());
        int answeredQuestions = 0;
        int rightQuestions = 0;
        for (SubmitPaperQuestionParams questionParams : paperParams.getQuestionList()) {
            // 生成答题卡明细
            AnswerReportItem reportItem = new AnswerReportItem();
            reportItem.setReportId(answerReport.getId());
            reportItem.setQuestionId(questionParams.getQuestionId());
            reportItem.setMyAnswers(JSONUtil.toJsonStr(questionParams.getMyAnswerList())); // 把答案集合转成json字符串 存进表中
            reportItem.setAnswerTime(questionParams.getAnswerTime());

            // 如果我的答案为空,则未答题,反之判断是否正确
            if (questionParams.getMyAnswerList() == null || questionParams.getMyAnswerList().isEmpty() ) {
                reportItem.setAnswerType(0);
            } else {
                answeredQuestions++;
                //对比答案 true正确  false错误
                boolean correctness = courseQuestionService.compareAnswer(questionParams.getQuestionId(), questionParams.getMyAnswerList());
                if (correctness) {
                    reportItem.setAnswerType(1);
                    rightQuestions++;
                } else {
                    reportItem.setAnswerType(2);

                    // 写入错题集
                    this.executeWrong(personId, questionParams.getQuestionId(), JSONUtil.toJsonStr(questionParams.getMyAnswerList()));
                }
            }
            reportItemList.add(reportItem);
        }

        answerReport.setTotalQuestions(paperParams.getQuestionList().size());
        answerReport.setRightQuestions(rightQuestions);
        answerReport.setAnsweredQuestions(answeredQuestions);

        // 保存答题报告及答题卡明细
        answerReportService.updateById(answerReport);

        answerReportItemService.saveBatch(reportItemList);

        return answerReport.getId();
    }

    @Transactional
    public void executeWrong(Long personId, Long questionId, String myAnswers) {
        // 查询该题目是否在错题集中
        PracticeWrong wrong = xmPracticeWrongService.getBaseMapper().getByPersonIdAndQuestionId(personId, questionId);
        if (wrong == null || wrong.getId() == null) {
            wrong = new PracticeWrong();
            wrong.setPersonId(personId);
            wrong.setQuestionId(questionId);
            wrong.setMyAnswers(myAnswers);
            wrong.setCreateTime(new Date());
            xmPracticeWrongService.save(wrong);
        }
    }


}
