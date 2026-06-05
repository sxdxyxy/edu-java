package com.joyfishs.dawa.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrainSign;
import com.joyfishs.dawa.project.mapper.ProjectTerminalTrainMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiaodai
 * @create 2022/1/4 18:37
 */

@Slf4j
@Service
public class FocusedTrainingService extends ServiceImpl<ProjectTerminalTrainMapper, ProjectTerminalTrain> {

    @Autowired
    private PersonService personService;
    @Autowired
    private ProjectTerminalTrainSignService projectTerminalTrainSignService;

    /**
     * 通过用户的ID获取 集中培训
     * @return 集中培训
     */
    public List<ProjectTerminalTrain> findByPerson() {
        Person person = personService.getByUserId(SecurityUtil.getUserId());
        log.info("XmFocusedTrainingService - findByPerson person:{}",person);
        if (person == null || StringUtils.isNull(person.getId())) person = new Person().setId(0L); //throw new CustomException("人员信息有误！");
        //通过人员的组织Id 获取可以参加的培训
        List<ProjectTerminalTrain> terminalTrainList = baseMapper.findByOrgId(person.getId());
        log.info("XmFocusedTrainingService - findByPerson terminalTrainList:{}",terminalTrainList);

        return terminalTrainList;
    }


    /**
     * 获取项目培训详情
     * @param projectId 项目Id
     * @return 培训项目
     */
    public ProjectTerminalTrain findTrainDetail(Long projectId) {
        log.info("XmFocusedTrainingService - findTrainDetail projectId:{}",projectId);

        //查询人员
        Person person = personService.getByUserId(SecurityUtil.getUserId());
        log.info("XmFocusedTrainingService - findByPerson xmPerson:{}", person);
        if (person == null || StringUtils.isNull(person.getId())) throw new CustomException("人员未找到！");

        //查找终端培训
        ProjectTerminalTrain projectTerminalTrain = getById(projectId);
        log.info("XmFocusedTrainingService - findTrainDetail projectTerminalTrain:{}",projectTerminalTrain);
        if (projectTerminalTrain == null || StringUtils.isNull(projectTerminalTrain.getId())) throw new CustomException("人员未找到！");

        //根据 项目ID和 人员ID 查询终端培训报名信息
        ProjectTerminalTrainSign sign = projectTerminalTrainSignService.getSign(projectTerminalTrain.getId(), person.getId());
        log.info("XmFocusedTrainingService - findTrainDetail sign:{}",sign);
        if (sign == null) projectTerminalTrain.setEnrollStatus(2);


        return projectTerminalTrain;
    }

}
