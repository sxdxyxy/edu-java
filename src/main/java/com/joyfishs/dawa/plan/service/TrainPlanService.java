package com.joyfishs.dawa.plan.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.joyfishs.dawa.plan.domain.TrainPlanCreateEvent;
import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.plan.mapper.TrainPlanMapper;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 项目关联的数据表  1-项目类型
 */
@Slf4j
@Service
public class TrainPlanService extends ServiceImpl<TrainPlanMapper, TrainPlan> {

    @Autowired
    ProjectService projectService;

    /**
     * 新增培训计划
     *
     * @param trainPlan 计划实体
     * @return
     */
    @Transactional
    public boolean add(TrainPlan trainPlan) {
        log.info("XmTrainPlanService - add trainPlan:{}", trainPlan);
        List<Long> projectIds = trainPlan.getProjectIds();
        if (projectIds == null || projectIds.size() == 0) throw new CustomException("项目数据为空，请核对！");

        Date date = new Date();
        Long createUserId = SecurityUtil.getUserId();
//		Long orgId = SecurityUtil.getOrgId();
//		log.info("XmTrainPlanService - add userId:{},orgId:{}",createUserId,orgId);
//		if(orgId == null){
//			if(createUserId != 1){
//				throw new CustomException("未找到单位信息");
//			}
//		}

        //保存培训计划主体
        trainPlan.setId(null);
        trainPlan.setCreateBy(createUserId);
        trainPlan.setCreateTime(date);
        trainPlan.setIsDelete(YesOrNoState.NO.getState());
        save(trainPlan);
        Long trainPlanId = trainPlan.getId();
        List<Project>  projects = Lists.newArrayList();
        for (Long projectId : projectIds) {
            Project project = projectService.get(projectId);
            project.setId(null);
            project.setTrainPlanId(trainPlanId);
//			xmProject.setOrgId(trainPlan.getType() == 2 ? 0L : trainPlan.getOrgId()); //年度计划 为0，单位计划为各个单位部门
            project.setOrgId(trainPlan.getOrgId()); //年度计划 为0，单位计划为各个单位部门
            project.setProjectType(trainPlan.getType());
            project.setProjectStatus(2); //2-年度培训计划

            //设定开始时间为年度的01-01 00:00:00
            Integer current = trainPlan.getYear();
            DateTime currentDate = DateUtil.parse(current + "-01-01 00:00:00", DatePattern.NORM_DATETIME_PATTERN);

            project.setTrainSdate(currentDate);
            project.setPracticeSdate(currentDate);
            project.setExamSdate(currentDate);

//			Integer next = current+1;
            DateTime nextDate = DateUtil.parse(current + "-12-31 23:59:59", DatePattern.NORM_DATETIME_PATTERN);
            project.setTrainEdate(nextDate);
            project.setPracticeEdate(nextDate);
            project.setExamEdate(nextDate);

            project.setExamNumber(10); //考试次数
            project.setMockExam("1"); //允许模拟考试
            projectService.saveOrUpdateProject(project);
            projects.add(project);
        }
        trainPlan.setProjects(projects);
        TrainPlanCreateEvent event = new TrainPlanCreateEvent(trainPlan);
        SpringUtil.getApplicationContext().publishEvent(event);
        return true;
    }

    /**
     * 查询列表
     */
    public List<TrainPlan> list(Integer type, Long orgId, String name, Integer year, Long projectId) {
        return baseMapper.list(type, orgId, name, year, projectId);
    }

    /**
     * 查询计划详情
     */
    public TrainPlan get(Long id) {
        TrainPlan plan = baseMapper.selectById(id);
        List<Project> projects = baseMapper.getProjectListByPlanId(id);
        List<Long> idList = projects.stream().map(Project::getId).collect(Collectors.toList());
        plan.setProjectIds(idList);
        plan.setProjects(projects);
        return plan;
    }

    /**
     * 删除计划
     */
    @Transactional
    public void del(Long id) {
        TrainPlan plan = baseMapper.selectById(id);
        plan.setIsDelete(YesOrNoState.YES.getState());
        plan.setDeleteBy(SecurityUtil.getUserId());
        plan.setDeleteTime(new Date());
        baseMapper.updateById(plan);
        projectService.delByTrainPlanId(id);
    }
}
