package com.joyfishs.dawa.archives.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.archives.domain.*;
import com.joyfishs.dawa.archives.mapper.ArchivesProjectMapper;
import com.joyfishs.dawa.org.service.SysOrgService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ArchivesProjectService extends ServiceImpl<ArchivesProjectMapper, ArchivesProject> {

    @Autowired
    private SysOrgService sysOrgService;

    public List<ArchivesProject> listPage(Integer trainClass, Integer trainWay, String projectName, Long projectId){
        List<ArchivesProject> list = baseMapper.findArchiveProjectList(trainClass,trainWay,projectName,projectId);
        return list;
    }

    /**
     * 人员记录
     * @param projectId
     * @param state
     * @param userName
     * @return
     */
    public List<ArchivesProjectUser> userListPage(Long projectId, Integer state, String userName){
        List<ArchivesProjectUser> list = baseMapper.findUserListPage(projectId,state,userName);
        for (ArchivesProjectUser projectUser : list) {
            //计算正确率
            DecimalFormat df=new DecimalFormat("0.00");
            projectUser.setYesRate(projectUser.getYesTopic() == 0 ? "0" : df.format((float)projectUser.getYesTopic() / projectUser.getAlreadyExercises()));
            projectUser.setMendTestResult("-");

            // 如果是没有参考直接返回 未参加
            if (projectUser.getTestState() != 3) {
                //得到考试状态,考试成绩 大于 合格分数 为合格
                if (projectUser.getTestResult() >= projectUser.getPassScore()) projectUser.setTestState(1);
                //考试成绩 小于 考试合格分数 = 不合格
                if (projectUser.getTestResult() < projectUser.getPassScore()) projectUser.setTestState(2);
            }

            // 查询单位部门信息
            String[] orgArr = sysOrgService.getOrgUnitDept(projectUser.getOrgId());
            projectUser.setDwOrgName(orgArr[0]);
            projectUser.setBmOrgName(orgArr[1]);

        }

        return list;
    }


    /**
     * 培训详情
     * @param projectId 项目ID
     * @param userId 人员ID
     * @param courseName 课程名称
     * @return
     */
    public List<ArchivesProjectUserCourse> userCourseListPage(Integer projectId, Integer userId, String courseName){
        log.info("ArchivesProjectService - userCourseListPage projectId:{}", projectId);
        log.info("ArchivesProjectService - userCourseListPage userId:{}", userId);
        log.info("ArchivesProjectService - userCourseListPage courseName:{}", courseName);

        List<ArchivesProjectUserCourse> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new ArchivesProjectUserCourse()
                    .setId(Long.parseLong(String.valueOf(i)))
                    .setCourseName("安全生产法律体系基本框架")
                    .setMustClassHour("22分0秒")
                    .setAlreadyClassHour("22分0秒")
                    .setAddUpExercises(500)
                    .setAlreadyExercises(200)
                    .setYesExercises(100)
                    .setYesTopic("50%")
                    .setFinishState(i%2)
            );
        }
        log.info("ArchivesProjectService - userCourseListPage list:{}", list);

        return list;
    }

    public List<ArchivesProjectUserAnswer> userAnswerListPage(Integer projectId, Integer userId){
        log.info("ArchivesProjectService - userAnswerListPage projectId:{}", projectId);
        log.info("ArchivesProjectService - userAnswerListPage userId:{}", userId);

        List<ArchivesProjectUserAnswer> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new ArchivesProjectUserAnswer()
                    .setId(Long.parseLong(String.valueOf(i)))
                    .setHandDate(new Date())
                    .setTestTime("3分18秒")
                    .setTestResult(20)
            );
        }
        log.info("ArchivesProjectService - userAnswerListPage list:{}", list);

        return list;
    }


}
