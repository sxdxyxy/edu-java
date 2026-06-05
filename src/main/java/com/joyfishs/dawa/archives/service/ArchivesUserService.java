package com.joyfishs.dawa.archives.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.answer.service.AnswerReportService;
import com.joyfishs.dawa.archives.domain.*;
import com.joyfishs.dawa.archives.mapper.ArchivesUserMapper;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.service.ProjectTerminalTrainService;
import com.joyfishs.dawa.student.domain.MyCourseList;
import com.joyfishs.dawa.student.service.ProjectPersonStudyRecordService;
import com.joyfishs.dawa.student.service.StudentCourseListService;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ArchivesUserService extends ServiceImpl<ArchivesUserMapper, ArchivesUser> {

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private ProjectPersonStudyRecordService studyRecordService;

    @Autowired
    private StudentCourseListService studentCourseListService;

    @Autowired
    private AnswerReportService answerReportService;

    @Autowired
    private ProjectTerminalTrainService terminalTrainService;

    public List<ArchivesUser> listPage(Long orgId, Long projectId, String name){
        List<ArchivesUser> list = baseMapper.queryList(orgId, projectId, name);
        for (ArchivesUser user : list) {
            // 查询单位部门信息
            String[] orgArr = sysOrgService.getOrgUnitDept(user.getOrgId());
            user.setDwOrgName(orgArr[0]);
            user.setBmOrgName(orgArr[1]);

            // 查询学时
            BigDecimal yearClassHour = studyRecordService.getBaseMapper().getYearSumStudyHours(user.getId());
            user.setYearClassHour(yearClassHour);
            BigDecimal sumClassHour = studyRecordService.getBaseMapper().getSumStudyHours(user.getId());
            user.setAddUpClassHour(sumClassHour);
            BigDecimal projectClassHour = studentCourseListService.getTotalLearnHours(user.getId(), null, LocalDate.now().getYear());
            user.setProjectClassHour(projectClassHour);
        }
        return list;
    }

    public List<ArchivesUserAutoTrain> autoTrainPageList(Long personId, String projectName, Integer finishState){
        List<ArchivesUserAutoTrain> list = baseMapper.queryListAutoTrain(personId, projectName, finishState);
        for (ArchivesUserAutoTrain autoTrain : list) {
            // 查询练习相关
            Integer questionCount = answerReportService.getBaseMapper().getQuestionCountByProjectId(autoTrain.getId());
            autoTrain.setAddUpExercises(questionCount);
            Map<String, Object> practiceMap = answerReportService.getBaseMapper().findPracticeResult(personId, autoTrain.getId(), null);
            if (null != practiceMap) {
                autoTrain.setAlreadyExercises(Integer.valueOf(practiceMap.get("answered_questions").toString()));
                autoTrain.setYesTopic(Integer.valueOf(practiceMap.get("right_questions").toString()));
                autoTrain.setYesRate(new BigDecimal(autoTrain.getYesTopic()).divide(new BigDecimal(autoTrain.getAlreadyExercises()),4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100)).doubleValue() + "%");
            }
            // 查询考试相关
            Map<String, Object> examMap = answerReportService.getBaseMapper().findExamResult(personId, autoTrain.getId());
            if (null != examMap) {
                autoTrain.setTestResult(new BigDecimal(examMap.get("score").toString()));
                autoTrain.setTestTime(Integer.valueOf(examMap.get("answer_time").toString()));
                autoTrain.setTestState(Integer.valueOf(examMap.get("qualified").toString()));
            }

        }
        return list;
    }

    public List<ArchivesUserAutoTrainDetail> autoTrainDetailPageList(Long personId, Long projectId, String courseName){
        List<ArchivesUserAutoTrainDetail> list = baseMapper.queryListAutoTrainDetail(personId, projectId, courseName);
        for (ArchivesUserAutoTrainDetail autoTrainDetail : list) {
            // 查询练习相关
            Integer questionCount = answerReportService.getBaseMapper().getQuestionCountByCourseId(autoTrainDetail.getId());
            autoTrainDetail.setAddUpExercises(questionCount);
            Map<String, Object> practiceMap = answerReportService.getBaseMapper().findPracticeResult(personId, null, autoTrainDetail.getId());
            if (null != practiceMap && null != practiceMap.get("answered_questions") && Integer.valueOf(practiceMap.get("answered_questions").toString()) > 0) {
                autoTrainDetail.setAlreadyExercises(Integer.valueOf(practiceMap.get("answered_questions").toString()));
                autoTrainDetail.setYesExercises(Integer.valueOf(practiceMap.get("right_questions").toString()));
                autoTrainDetail.setYesRate(new BigDecimal(autoTrainDetail.getYesExercises()).divide(new BigDecimal(autoTrainDetail.getAlreadyExercises()), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100)).doubleValue() + "%");
            } else {
                autoTrainDetail.setYesRate("0%");
            }
            autoTrainDetail.setFinishState(autoTrainDetail.getMustClassHour() == autoTrainDetail.getAlreadyClassHour() ? 1 : 0);
        }

        return list;
    }

    public List<ArchivesUserAutoTrainAnswer> autoTrainAnswerPageList(Long personId, Long projectId){
        List<ArchivesUserAutoTrainAnswer> list = baseMapper.queryListAutoTrainAnswer(personId, projectId);
        return list;
    }

    public List<ArchivesUserTerminalTrain> terminalTrainPageList(Long personId, String projectName){
        List<ArchivesUserTerminalTrain> list = baseMapper.queryListTerminalTrain(personId, projectName);
        return list;
    }

    public ArchivesUserTerminalTrainDetail terminalTrainDetail(Long personId, Long projectId){
        ProjectTerminalTrain terminalTrain = terminalTrainService.getById(projectId);
        return new ArchivesUserTerminalTrainDetail().setId(terminalTrain.getId())
                .setName(terminalTrain.getTrainName())
                .setTime(DateUtil.format(terminalTrain.getTrainSdate(), DatePattern.NORM_DATE_PATTERN) + " 至 " + DateUtil.format(terminalTrain.getTrainEdate(), DatePattern.NORM_DATE_PATTERN))
                .setHost(terminalTrain.getHost())
                .setPersonSize(terminalTrain.getPersonNum())
                .setStudyContent(terminalTrain.getLearningContent())
                .setContact(terminalTrain.getContact() + " " + terminalTrain.getPhone())
                .setRemark(terminalTrain.getRemark());
    }

    public List<ArchivesUserSelfStudy> selfStudyPageList(Long personId, String courseName){
        // 获取该人员所有已完成的自主/必修课程作为自学记录
        List<MyCourseList> courseList = studentCourseListService.getMyCourseList(personId, courseName, null, null, 3, null);
        List<ArchivesUserSelfStudy> result = new ArrayList<>();
        if (courseList != null) {
            for (MyCourseList item : courseList) {
                ArchivesUserSelfStudy study = new ArchivesUserSelfStudy()
                    .setId(item.getId())
                    .setCourseName(item.getName())
                    .setClassHour(item.getLearnHours() != null ? item.getLearnHours().intValue() : 0)
                    .setAlreadyClassHour(item.getLearnHours() != null ? item.getLearnHours().toString() : "0");
                result.add(study);
            }
        }
        return result;
    }
}
