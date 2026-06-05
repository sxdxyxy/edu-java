package com.joyfishs.dawa.project.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.domain.param.PersonListQueryRequest;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.mapper.PersonMapper;
import com.joyfishs.dawa.project.domain.vo.ProjectTerminalTrainSignVo;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrainSign;
import com.joyfishs.dawa.project.mapper.ProjectTerminalTrainMapper;
import com.joyfishs.dawa.project.mapper.ProjectTerminalTrainSignMapper;
import com.joyfishs.dawa.student.domain.MyCourseList;

/**
 * 终端培训签到
 *
 * @author ykfnb
 */
@Service
public class ProjectTerminalTrainSignService extends ServiceImpl<ProjectTerminalTrainSignMapper, ProjectTerminalTrainSign> {

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private ProjectTerminalTrainMapper projectTerminalTrainMapper;

    /**
     * 查询报名数据
     */
    public ProjectTerminalTrainSign getSign(Long trainId, Long personId) {
        return baseMapper.getSign(trainId, personId);
    }

    /**
     * 查询参会人员列表
     */
    public List<ProjectTerminalTrainSignVo> getEnrollList(Long trainId) {
        ProjectTerminalTrain train = projectTerminalTrainMapper.selectById(trainId);
        PersonListQueryRequest queryRequest = new PersonListQueryRequest();
        queryRequest.setOrgId(train.getOrgId());
        queryRequest.setIsAdmin(2); //不是管理员
        List<Person> personList = personMapper.queryList(queryRequest);
        List<ProjectTerminalTrainSignVo> result = Lists.newArrayList();
        Map<Long, ProjectTerminalTrainSignVo> signPersonMap = getRegisteredList(trainId).stream().collect(
                Collectors.toMap(ProjectTerminalTrainSignVo::getPersonId, x -> x));
        personList.forEach(person -> {
            if (signPersonMap.containsKey(person.getId())) {
                result.add(signPersonMap.get(person.getId()).setDisabled(!train.getLater()));
            } else {
                result.add(new ProjectTerminalTrainSignVo().setPersonId(person.getId()).setName(person.getName()).setEnrollStatus(0).setSignStatus(0).setDisabled(!train.getLater()));
            }
        });
        return result;
    }

    /**
     * 查询报名过的人员列表，含签到情况
     */
    public List<ProjectTerminalTrainSignVo> getRegisteredList(Long trainId) {
        List<ProjectTerminalTrainSignVo> list = baseMapper.getRegisteredList(trainId);
        return list;
    }

    /**
     * 查询报名过的培训项目，含签到情况
     */
    public List<ProjectTerminalTrainSignVo> getTerminalTrainByPersonId(Long personId) {
        return baseMapper.getTerminalTrainByPersonId(personId);
    }

    public List<MyCourseList> getMyCourseList(Long personId) {
        List<ProjectTerminalTrainSignVo> signList = getTerminalTrainByPersonId(personId);
        List<MyCourseList> result = Lists.newArrayList();
        signList.forEach(sign -> {
            result.add(new MyCourseList()
                    .setId(sign.getId())
                    .setTrainType(3)
                    .setName(sign.getTrainName())
                    .setTrainWay(1)
                    .setStartDate(sign.getTrainSdate())
                    .setEndDate(sign.getTrainEdate())
                    .setStatus(4)
                    .setIsQualified(1));
        });
        return result;
    }

    /**
     * 获取签到数量
     */
    public Long getSignNum(Long id, Integer type) {
        return baseMapper.getSignNum(id, type);
    }
}
