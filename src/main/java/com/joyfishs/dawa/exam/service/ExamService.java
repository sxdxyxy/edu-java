package com.joyfishs.dawa.exam.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.answer.entity.AnswerReport;
import com.joyfishs.dawa.answer.entity.AnswerReportItem;
import com.joyfishs.dawa.answer.service.AnswerReportItemService;
import com.joyfishs.dawa.answer.service.AnswerReportService;
import com.joyfishs.dawa.answer.vo.*;
import com.joyfishs.dawa.course.service.CourseQuestionService;
import com.joyfishs.dawa.exam.domain.ExamQuestionVO;
import com.joyfishs.dawa.exam.entity.ExaminationPapers;
import com.joyfishs.dawa.exam.entity.StudentSubscribeExam;
import com.joyfishs.dawa.exam.mapper.ExaminationPapersMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.practice.service.PracticeService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.entity.ProjectRelate;
import com.joyfishs.dawa.project.entity.ProjectTestPaperTactics;
import com.joyfishs.dawa.project.service.ProjectRelateService;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.dawa.student.domain.vo.MyClassworkListVo;
import com.joyfishs.dawa.student.entity.PersonCourseRelate;
import com.joyfishs.dawa.student.service.PersonCourseRelateService;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 考试相关的业务
 */

@Slf4j
@Service
public class ExamService extends ServiceImpl<ExaminationPapersMapper, ExaminationPapers> {


    @Autowired
    private CourseQuestionService courseQuestionService;
    @Autowired
    private ProjectRelateService projectRelateService;
    @Autowired
    private PersonService personService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AnswerReportService answerReportService;
    @Autowired
    private AnswerReportItemService answerReportItemService;
    @Autowired
    private StudentSubscribeExamService studentSubscribeExamService;
    @Autowired
    private PersonCourseRelateService personCourseRelateService;
    @Autowired
    PracticeService practiceService;

    /**
     * 获取作业题
     * @param projectId
     * @param personId
     * @return
     */
    public AnswerVo getClassworkQuestions(Long projectId, Long personId) {
        log.info("XmExamService - testPaper projectId:{}", projectId);
        if (personId == null) {
            throw new CustomException("参考人员ID不能为空！");
        }

        Project project = projectService.getById(projectId);
        //通过项目ID找到项目下绑定的课程
        List<ProjectRelate> projectRelates = projectRelateService.getCourseByProjectId(projectId);
        if (projectRelates == null || projectRelates.size() == 0) {
            throw new CustomException("该项目下没有课程！");
        }

        AnswerVo answerVo = new AnswerVo();
        answerVo.setCourseName(project.getProjectName()+"(作业题)");
        answerVo.setTotalScore(0);

        //获取所有的课程id列表
        List<String> courseIds = projectRelates.stream().map(ProjectRelate::getRelateIds).collect(Collectors.toList());
        List<Long> idList = this.findRandomSelectionQuestionId(null, 15, courseIds);
        List<QuestionVo> questionList = courseQuestionService.getBaseMapper().findByQuestionByIdIN(idList);

        AnswerQuestionVo vo = new AnswerQuestionVo();
        vo.setQuestionCount(questionList.size());
        vo.setQuestionList(questionList);
        List<AnswerQuestionVo> retList = new ArrayList<>(1);
        retList.add(vo);
        answerVo.setQuestion(retList);
        return answerVo;
    }

    /**
     * 作业提交
     * @param paperParams
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long submitClasswork(SubmitPaperParams paperParams) {
        Long personId = SecurityUtil.getPersonId();
        Long projectId = paperParams.getProjectId();

        AnswerReport report = new AnswerReport();
        report.setPersonId(personId)
                .setProjectId(projectId)
                .setSubmitTime(new Date())
                .setType(4)
                .setAnswerTime(paperParams.getAnswerTime());
        answerReportService.save(report);
        Long reportId = report.getId();

        // 答题卡明细集合
        List<AnswerReportItem> reportItemList = new ArrayList<>();
        // 计算答对题数
        int rightQuestions = 0;
        for (SubmitPaperQuestionParams questionParams : paperParams.getQuestionList()) {
            // 生成答题卡明细
            AnswerReportItem reportItem = new AnswerReportItem();
            reportItem.setReportId(reportId);
            reportItem.setQuestionId(questionParams.getQuestionId());
            // 把答案集合转成json字符串 存进表中
            reportItem.setMyAnswers(JSONUtil.toJsonStr(questionParams.getMyAnswerList()));
            reportItem.setAnswerTime(questionParams.getAnswerTime());

            // 如果我的答案为空,则未答题,反之判断是否正确
            if (CollUtil.isEmpty(questionParams.getMyAnswerList())) {
                reportItem.setAnswerType(0);
            } else {
                //对比答案 true正确  false错误
                boolean correctness = courseQuestionService.compareAnswer(questionParams.getQuestionId(), questionParams.getMyAnswerList());
                if (correctness) {
                    reportItem.setAnswerType(1);
                    rightQuestions ++;
                } else {
                    reportItem.setAnswerType(2);

                    // 写入错题集
                    practiceService.executeWrong(personId, questionParams.getQuestionId(), JSONUtil.toJsonStr(questionParams.getMyAnswerList()));
                }
            }
            reportItemList.add(reportItem);
        }

        report.setTotalQuestions(paperParams.getQuestionList().size());
        report.setRightQuestions(rightQuestions);
        // 保存答题报告及答题卡明细
        answerReportService.updateById(report);

        answerReportItemService.saveBatch(reportItemList);

        return reportId;

    }

    /** 作业列表 */
    public List<MyClassworkListVo> getClassworkList(Long personId, Long projectId) {
        return baseMapper.getClassworkList(personId,projectId);
    }

    /**
     * 自动组卷
     * xmStudentSubscribeExamService
     * @param projectId 项目ID
     * @param personId  学员ID
     * @param examType  考试类型 1-模拟考试  2-正常考试  （暂定传什么都可以，因为我也不知道有几种模式生成题目）
     */
    public AnswerVo testPaper(Long projectId, Long personId, Integer examType) {

        if (personId == null) {
            throw new CustomException("参考人员ID不能为空！");
        }
        if (examType == null) {
            throw new CustomException("考试组卷类型不能为空！");
        }

        Project project = projectService.getById(projectId);

        //通过项目ID找到项目下绑定的课程
        List<ProjectRelate> projectRelates = projectRelateService.getCourseByProjectId(projectId);

        AnswerVo answerVo = new AnswerVo();
        String title = "";
        if (examType == 1) {
            title = "模拟考试";
        } else {
            title = "正式考试";
        }
        answerVo.setCourseName(project.getProjectName() + " (" + title + ")");
        answerVo.setTotalScore(project.getTotalScore());

        if (projectRelates == null || projectRelates.size() == 0) {
            throw new CustomException("项目下的课程不能为空！");
        }

        //获取组卷规则 判断题多少题，选择题多少题，多选题多少题，单选题多少题
        List<ProjectTestPaperTactics> xmProjectRelates1 = courseQuestionService.getBaseMapper().findGroupCount(projectId);

        List<AnswerQuestionVo> retList = new ArrayList<>();
        int totalScore = 0; //总分

        //转换得到课程IDS
        List<String> courseIds = projectRelates.stream().map(ProjectRelate::getRelateIds).collect(Collectors.toList());
        //题目生成的试卷
        List<Long> questionAll = new ArrayList<>();
        //遍历获取所有题目类型、该题目类型数量、单题分值
        for (ProjectTestPaperTactics projectTestPaperTactics : xmProjectRelates1) {

            Integer topicType = projectTestPaperTactics.getTopicType(); //题目类型
            int radioNum = projectTestPaperTactics.getTopicNum(); // 抽题数量
            int radioScore = projectTestPaperTactics.getTopicScore(); // 单题分值
            int score = radioNum * radioScore; // 题目数量 * 单题分值 = 一种题目类型的总分值
            //查询该类型下题目id
            List<Long> idList = findRandomSelectionQuestionId(topicType, radioNum, courseIds);

            List<QuestionVo> questionList = courseQuestionService.getBaseMapper().findByQuestionByIdIN(idList);

            AnswerQuestionVo vo = new AnswerQuestionVo();
            vo.setQuestionCount(radioNum)
                    .setQuestionScore(radioScore)
                    .setTotalScore(score)
                    .setQuestionType(projectTestPaperTactics.getTopicType().toString())
                    .setQuestionList(questionList);
            retList.add(vo);

            //总分
            totalScore = totalScore + score;

            questionAll.addAll(idList);
        }
        answerVo.setQuestion(retList);

        //保存考试记录
        AnswerReport answerReport = new AnswerReport();
        answerReport.setPersonId(personId);
        answerReport.setProjectId(projectId);
        answerReport.setType(examType);
        answerReport.setTotalQuestions(questionAll.size());
        answerReport.setTotalScore(new BigDecimal(totalScore));

        answerReport.setIfAttend(YesOrNoState.NO.getState());
        answerReport.setCreateTime(new Date());
        answerReport.setCreateBy(SecurityUtil.getUserId());
        answerReport.setIsDelete(YesOrNoState.NO.getState());
        answerReportService.save(answerReport);

        //保存试卷题目
        List<AnswerReportItem> reportItemList = new ArrayList<>();
        for (Long questionId : questionAll) {
            AnswerReportItem reportItem = new AnswerReportItem();
            reportItem.setReportId(answerReport.getId());
            reportItem.setQuestionId(questionId);
            reportItemList.add(reportItem);
        }
        //写入答题卡明细
        answerReportItemService.saveBatch(reportItemList);


        // 查询到生成的题目
//        List<QuestionVo> questionList = ;
//        log.info("XmExamService - testPaper questionList:{}", questionList);

        // 根据答题类型分类
//        Map<String, List<QuestionVo>> questionMap = questionList.stream().collect(Collectors.groupingBy(QuestionVo::getQuestionType));


        return answerVo;
    }

    /**
     * 自动抽题目
     * @param questionType 题目类型  如果不需要题目类型传null
     * @param NodeQuestionSize 需要的题目数量
     * @return 题目ID集合
     */
    public List<Long> findRandomSelectionQuestionId(Integer questionType, Integer NodeQuestionSize ,List<String> courseIds){
        //通过题目类型、和题目数量 查出组卷题目
        List<Long> questionIdList = courseQuestionService.getBaseMapper().findByQuestionType(questionType,courseIds);

        List<Long> list = new ArrayList<>();
        //如果需要的题目大于等于查询出的题目数量直接返回所有题目
        if(NodeQuestionSize >= questionIdList.size()) {
            return questionIdList;
        }
        for (Integer integer = 0; integer < NodeQuestionSize; integer++) {
            int i = RandomUtil.randomInt(0, questionIdList.size());
            list.add(questionIdList.get(i));
            questionIdList.remove(i);
        }
        return list;
    }

    /**
     * 查询我的考试-（考试列表）
     * @param examClassify 考试类别
     * @param status 状态
     * @return
     */
    public List<Project> findExamAll(Integer examClassify, Integer status) {
        //通过人员找到所在组织
        Person person = personService.getByUserId(SecurityUtil.getUserId());
        if (person == null || StringUtils.isNull(person.getId())) {
            throw new CustomException("人员未找到");
        }

        List<Project> projectList = baseMapper.findExamAll(person.getOrgId(),examClassify,person.getId(),status);
        for (Project project : projectList) {
            // 设置预约状态，是否预约
            this.setsubscribeExamStatus(person,project);
            // 是否参加考试 -- 参加考试状态默认为0，因为这是预约考试列表
            project.setIsAttend(0);
            // 是否达到预约超时
            if (this.checkTime(project.getExamSdate())) {
                project.setIsAttendStatus(1);
            } else {
                project.setIsAttendStatus(0);
            }
            //设置考试状态
            this.setExamState(project);
            //设置答题报告
            this.setReport(project,person.getId());
            //设置考试按钮表示 、、 考试详情 - 预约考试 - 进入考试
            this.setExamCondition(project,person.getId());
            //设置考试类别  1 - 线上
            project.setTestType(1);
        }

        return projectList;
    }


    /**
     * 设置学生的考试预约状态
     * @param person 人员
     * @param project 考试
     */
    private void setsubscribeExamStatus(Person person, Project project) {
        //通过学生ID和项目ID查询学员预约情况
        StudentSubscribeExam studentSubscribeExam = baseMapper.findPersonSubscriptExamStatus(person.getId(),project.getId());

        // 如果 是空的，代表学员没有预约考试 反之则进行预约了
        if (studentSubscribeExam == null) {
            project.setSubscribeStatus(0);
        } else {
            project.setSubscribeStatus(1);
        }

        // 验证学员是否可以参加考试  -----第一：当前时间在考试开始时间前五分钟内则认为他可以进入考试，当前时间一旦超过考试开始时间则判断他不能进入考试



    }

    /**
     * 我的考试 - 开始答题 - 查询题目
     * @param isExam  是否参加考试 1-是 0-否  。。。。。考试时间过了后使用
     * @param projectId
     * @return
     */
    public AnswerVo getExamQuestionList(Long projectId,Long personId,Integer isExam) {
        Project project = projectService.getById(projectId);
        if (project == null || StringUtils.isNull(project.getId())) {
            throw new CustomException("项目未找到！");
        }

        // 达到条件方可进入考试
        studentSubscribeExamService.verifyExamStatus(project.getExamSdate(),project.getExamEdate(),isExam);

        StudentSubscribeExam studentSubscribeExam = baseMapper.findPersonSubscriptExamStatus(personId,projectId);

        // 如果没参加过考试必须要走下面的判断
        if (isExam == 1) {
            if (studentSubscribeExam == null || StringUtils.isNull(studentSubscribeExam.getId())) {
                throw new CustomException("您还未预约考试!");
            }
        }


        //生成考试题目
        AnswerVo vo = this.testPaper(projectId, personId, 2);
        vo.setExamTime(project.getExamTime());
        return vo;
    }

    /**
     * 我的考试 - 考试提交
     * @param paperParams
     */
    @Transactional
    public Long submitExam(SubmitPaperParams paperParams) {
        Long personId = SecurityUtil.getPersonId();

        Long projectId = paperParams.getProjectId();
        if (StringUtils.isNull(projectId)) {
            throw new CustomException("项目ID不能为空!");
        }

//        //查找考试预约相关信息
//        XmStudentSubscribeExam subscribeExam = baseMapper.findPersonSubscriptExamStatus(personId,projectId);
//        log.info("XmExamService - submitExam subscribeExam:{}",subscribeExam);
//        if (subscribeExam == null || StringUtils.isNull(subscribeExam.getId())) throw new CustomException("请先预约考试后再进行考试!");

        //通过项目ID和人员ID获取 答题报告
        List<AnswerReport> reports = baseMapper.findExamQuestion(personId,projectId,paperParams.getExamType());
        if (reports == null || reports.size() == 0) {
            throw new CustomException("考试组卷相关信息未找到！");
        }

        AnswerReport report = reports.get(0);
        report.setAnswerTime(paperParams.getAnswerTime());
        report.setSubmitTime(new Date());

        // 获取答题明细
        List<AnswerReportItem> reportItemList = baseMapper.findByReporId(report.getId());
        if (reportItemList == null || reportItemList.size() == 0) {
            throw new CustomException("答题明细未找到！");
        }

        //获取提交上来的题目ID集合
        List<Long> questionIds = paperParams.getQuestionList().stream().map(SubmitPaperQuestionParams::getQuestionId).collect(Collectors.toList());

        //获取单题分值
        List<ExamQuestionVO> questionVOList = baseMapper.findExamQuestionDetail(projectId, questionIds);

        //设置题目的单题分值；
        this.setQuestionScore(questionVOList,reportItemList);

        // 计算答对题数
        int rightQuestions = 0;
        int totalQuestions = 0;
        BigDecimal totalScore = new BigDecimal("0");
        for (SubmitPaperQuestionParams questionParams : paperParams.getQuestionList()) {
            //获取答题明细
            AnswerReportItem reportItem = this.getReportItemByReportId(report.getId(),reportItemList,questionParams.getQuestionId());

            reportItem.setMyAnswers(JSONUtil.toJsonStr(questionParams.getMyAnswerList())); // 把答案集合转成json字符串 存进表中
            reportItem.setAnswerTime(questionParams.getAnswerTime());

            totalQuestions++;
            // 如果我的答案为空,则未答题,反之判断是否正确
            if (questionParams.getMyAnswerList() == null || questionParams.getMyAnswerList().isEmpty() ) {
                reportItem.setAnswerType(0);
            } else {
                //对比答案 true正确  false错误
                boolean correctness = courseQuestionService.compareAnswer(questionParams.getQuestionId(), questionParams.getMyAnswerList());
                if (correctness) {
                    reportItem.setAnswerType(1);
                    rightQuestions ++;
                    totalScore = totalScore.add(reportItem.getQuestionScore());
                } else {
                    reportItem.setAnswerType(2);
                }
            }
        }

        report.setIfAttend(YesOrNoState.YES.getState());
        report.setRightQuestions(rightQuestions);
        report.setTotalQuestions(totalQuestions); // 总题数
        report.setAnsweredQuestions(totalQuestions); // 考试默认总题数也是已答题数
        report.setScore(totalScore);
        // 保存答题报告及答题卡明细
        answerReportService.updateById(report);
        answerReportItemService.updateBatchById(reportItemList);

        //预约考试
//        subscribeExam.setStatus(2);
//        subscribeExam.setUpdateTime(new Date());
//        subscribeExam.setUpdateBy(SecurityUtil.getUserId());
//        xmStudentSubscribeExamService.updateById(subscribeExam);

        //把考试状态修改成考试完成
        PersonCourseRelate courseRelate = personCourseRelateService.getRecord(projectId,personId);

        courseRelate.setIfJoinExam(YesOrNoState.YES.getState());
        courseRelate.setUpdateTime(new Date());
        courseRelate.setUpdateBy(SecurityUtil.getUserId());
        personCourseRelateService.updateById(courseRelate);

        return report.getId();
    }

    /**
     * 设置每道题目的分值
     * @param questionVOList
     * @param reportItemList
     */
    private void setQuestionScore(List<ExamQuestionVO> questionVOList, List<AnswerReportItem> reportItemList) {
        for (AnswerReportItem reportItem : reportItemList) {
            for (ExamQuestionVO examQuestionVO : questionVOList) {
                if (reportItem.getQuestionId().equals(examQuestionVO.getQuestionId())) {
                    reportItem.setQuestionScore(examQuestionVO.getTopicScore());
                }
                    continue;
            }
        }
    }

    /**
     * 获取用户答明细
     * @param reportId 答题报告ID
     * @param reportItemList 答题明细List
     * @param questionId 题目ID
     * @return 答题明细
     */
    public AnswerReportItem getReportItemByReportId(Long reportId, List<AnswerReportItem> reportItemList, Long questionId){

        AnswerReportItem item = null;
        for (AnswerReportItem reportItem : reportItemList) {
            if (reportItem.getReportId().equals(reportId) &&
                    questionId.equals(reportItem.getQuestionId())) {
                return item = reportItem;
            }
        }
       return item;
    }

    //获取已参加的考试
    public List<Project> findAttendExamAll(Integer examClassify, Integer type) {
        //找到人员
        Person person = personService.getByUserId(SecurityUtil.getUserId());
        if (person == null || StringUtils.isNull(person.getId())) {
            throw new CustomException("人员未找到");
        }

        List<Project> projectList = baseMapper.findAttendExamAll(person.getOrgId(),examClassify,person.getId(),type);

        for(Project project : projectList) {
            this.setReport(project,person.getId());
        }

        return projectList;
    }

    /**
     * 考试-查询答题报告
     * @param projectId
     * @return
     */
    public AnswerReport viewReport(Long projectId) {
        Project project = projectService.getById(projectId);
        if (project == null || StringUtils.isNull(project.getId())) {
            throw new CustomException("项目未找到！");
        }

        //找到人员
        Long personId = SecurityUtil.getPersonId();

         //先找到答题报告
         List<AnswerReport> reports = baseMapper.findReport(personId,projectId);
        if (reports.size() == 0 || reports == null) {
            return new AnswerReport();
        }

        AnswerReport report = reports.get(0);

        if (report != null && StringUtils.isNotNull(report.getId())) {
            //计算击败比例
            report.setBeat(baseMapper.getBeat(projectId,report.getScore()));
            report.setAvgScore(baseMapper.getAvg(projectId));
            // 查询答题卡明细
            List<AnswerReportItem> reportItems = answerReportItemService.getBaseMapper().listByReportId(report.getId());
            report.setReportItems(reportItems);
        }
        return report;
    }

    /**
     * 考试-查看答题解析
     * @param projectId
     * @return
     */
    public AnswerVo viewResolution(Long projectId) {
        Project project = projectService.getById(projectId);
        if (project == null || StringUtils.isNull(project.getId())) {
            throw new CustomException("项目未找到！");
        }

        //找到人员
        Long personId = SecurityUtil.getPersonId();
        //先找到答题报告
        List<AnswerReport> reports = baseMapper.findReport(personId,projectId);
        if (reports.size() == 0 || reports == null) {
            throw new CustomException("答题报告不存在！");
        }

        AnswerReport report = reports.get(0);
        AnswerVo answerVo = answerReportService.viewResolution(report.getId(), null);
        return answerVo;
    }

    /**
     * 检测是否达到考试的时间（考前前五分钟）
     * @param examStartTime 考试开始时间
     * @return true - 达到要求  false - 不满足要求
     */
    private  boolean checkTime(Date examStartTime){

        Calendar beforeTime = Calendar.getInstance();
        beforeTime.setTime(examStartTime);

        // 获取考试开始时间前五分钟
        beforeTime.add(Calendar.MINUTE, -5);
        Date currentV = beforeTime.getTime();

        Date current = new Date();

        //当前时间在考试开始时间前五分钟之后,并且在考试开始时间之前，并且在考试开始时间之前
        return current.after(currentV) && current.after(examStartTime);
    }


    /**
     * 获取移动端 考试列表
     * @param examClassify 项目类型--培训种类id
     * @param status 状态  1-未开始 2-已结束
     * @return
     */
    public List<Project> findMobileExamList(Integer examClassify, Integer status, Person person) {
        //查询所有考试
        List<Project> projectList = baseMapper.findMobileExamList(examClassify,status,person.getId());

        for(Project project : projectList) {
            // 设置 report 答题报告
            this.setReport(project,person.getId());
            // 设置预约考试状态
//            this.setsubscribeExamStatus(person,project);
            //设置考试状态
            this.setExamState(project);
            // 设置是否能参加考试状态
//            if (this.checkTime(project.getExamSdate())) project.setIsAttendStatus(1);
//            else project.setIsAttendStatus(0);

            //设置预约考试、考试详情、进入考试按钮
            this.setExamCondition(project,person.getId());
        }
        return projectList;
    }


    /**
     * 给各个考试设置答题报告
     * @param project 项目
     * @param personId 人员ID
     */
    private void setReport(Project project, Long personId) {
        List<AnswerReport> reporList = baseMapper.findReport(personId,project.getId());
        // 设置是否参加考试标识、、、 处理学员没有去考试的答题卡 给他新生一套答题卡
        if (reporList == null || reporList.size() == 0) {
            project.setIsAttend(0);

            //生成题目
            this.getExamQuestionList(project.getId(),personId,0);
            reporList = baseMapper.findReport(personId,project.getId());
            project.setReport(reporList.get(0));
        }

        AnswerReport r = null;
        for (AnswerReport report : reporList) {
            if(report.getSubmitTime() != null) {
                r = report;
                break;
            }
        }

        //遍历答题报告，把提交时间不为空的设置为答题报告
        if(r == null){
            if(project.getReport() == null) {
                project.setReport(reporList.get(0));
            }
            // 没有答题参考时间
            project.setSubmitTime("-");
            project.setIsAttend(YesOrNoState.NO.getState());
        } else {
            project.setIsAttend(YesOrNoState.YES.getState());
            project.setReport(r);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            project.setSubmitTime(sdf.format(r.getSubmitTime()));
        }

        // 获取答题报告ID
//        if (reporList.size() != 0) {
//            // 设置答题报告ID
//            project.setReport(reporList.get(0));
//            if (StringUtils.isNotNull(reporList.get(0).getSubmitTime())) {
//                project.setIsAttend(YesOrNoState.YES.getState());
//
//                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                project.setSubmitTime(sdf.format(reporList.get(0).getSubmitTime()));
//            } else {
//                // 没有答题参考时间
//                project.setSubmitTime("-");
//                project.setIsAttend(YesOrNoState.NO.getState());
//            }
//        }

        //项目基础数据---
//        project.setTestType(1);
    }


    /**
     * 考试状态更变
     * @param project 考试
     */
    private void setExamState(Project project){
        //当前时间在考试开始时间之前 为未开始
        if (new Date().before(project.getExamSdate())) {
            project.setExamState(1);
        }
        //当前时间在考试开始时间之后 为进行中
        if (new Date().after(project.getExamSdate())) {
            project.setExamState(2);
        }
        //当前时间在考试结束时间之后 为已结束
        if (new Date().after(project.getExamEdate())) {
            project.setExamState(3);
        }
    }

    /**
     * 获取学员考试次数
     * @return 学员参考的次数
     */
    public Integer getAttendExamCount(){
        return findAttendExamAll(null,2).size();
    }

    /**
     *  设置考试查看详情、预约考试、进入考试
     *  1-考试详情  3-进入考试
     */
    public void setExamCondition(Project project, Long personId){
        //当前时间在考试结束时间之前 || 当前时间在考试开始时间之后(考试期间之内)
        if(new Date().before(project.getExamEdate()) || new Date().after(project.getExamSdate())) {
            //是否参考，参考了则是查看详情，未参加则还是显示进入考试
            if (project.getIsAttend() == YesOrNoState.YES.getState()) {
                //此处是已经参加过考试的
                //如果可以补考就状态就显示补考，不能补考就显示查看详情
                if (project.getMakeUpExam()) {
                    project.setExamCondition(4);
                } else {
                    project.setExamCondition(1);
                }
            } else {
                //此处是没有参加过考试的人
                project.setExamCondition(3);
            }

            //判断学时是否达到，学时未达到则灰掉按钮
            PersonCourseRelate courseRelate = personCourseRelateService.getRecord(project.getId(),personId);
            //判断学员学时是否达到要求
            BigDecimal bigDecimal = new BigDecimal(project.getExamOpenCondition());
            int i = bigDecimal.compareTo(courseRelate.getLearnHours());
            if( i > 0) {
                project.setDisable(YesOrNoState.YES.getState());
            }
        }

        //当前时间在考试开始时间之前
        if (new Date().before(project.getExamSdate())) {
            project.setExamCondition(3);
            project.setDisable(YesOrNoState.YES.getState());
        }

        //考试结束时间在当前时间之前，显示字样应为查看详情
        if (new Date().after(project.getExamEdate())) {
            project.setExamCondition(1);
        }
    }

    /**
     * 项目下模拟考试列表
     * @param projectId
     * @param personId
     * @return
     */
	public List<AnswerReport> findMockTestList(Long projectId, Long personId) {
        return answerReportService.getBaseMapper().findMockTestList(projectId,personId);
	}


}
