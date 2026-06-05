package com.joyfishs.dawa.studyData.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.joyfishs.dawa.studyData.domain.StudyData;
import com.joyfishs.dawa.studyData.domain.StudyDataCourse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudyDataService {

    public StudyData home(){
        log.info("StudyDataService - home");

        StudyData studyData = new StudyData();
        studyData.setTotalClassHour(300);
        studyData.setTotalCourse(30);
        studyData.setCompleteCourse(8);
        studyData.setExamSize(8);
        studyData.setYearClassHourSituation(0.6);
        studyData.setYearTotalClassHour(30);
        studyData.setYearClassHourRequi(90);
        studyData.setYearClassHour1(33);
        studyData.setYearClassHour2(22);
        studyData.setYearClassHour3(33);

        List<StudyDataCourse> courseList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            courseList.add(new StudyDataCourse()
                    .setCourseId(Long.parseLong(String.valueOf(i+1)))
                    .setCourseName("建筑施工安全培训第一期课程学习（6.8-7.8）")
                    .setCourseType(i==3 ? 1 : 0)
                    .setStartTime(new Date())
                    .setStatus(i==0 ? 0: 1)
                    .setStudyTime(i==0 ? "1/30" : "30/30")
                    .setTestResult(i==0 ? null : 60));
        }
        studyData.setCourseList(courseList);
        log.info("StudyDataService - home studyData:{}", studyData);

        return studyData;
    }



}
