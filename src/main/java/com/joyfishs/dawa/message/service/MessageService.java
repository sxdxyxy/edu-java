package com.joyfishs.dawa.message.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.message.entity.Message;
import com.joyfishs.dawa.message.mapper.MessageMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.plan.domain.TrainPlanCreateEvent;
import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    @Autowired
    PersonService personService;

    /**
     * 新增消息
     */
    @Transactional
    public void addMessage(Long personId, String title, Integer type, Long dataId) {
        Message message = new Message();
        message.setPersonId(personId);
        message.setTitle(title);
        message.setType(type);
        message.setDataId(dataId);

        // 设置默认参数
        message.setCreateTime(new Date());
        message.setIsDelete(YesOrNoState.NO.getState());
        message.setIsRead(0);

        this.save(message);

    }

    /**
     * 监听培训计划创建事件创建消息
     *
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void listenerTrainPlanCreateEvent(TrainPlanCreateEvent event) {
        TrainPlan trainPlan = event.getTrainPlan();
        Integer year = trainPlan.getYear();
        if(year.equals(DateUtil.thisYear())){
            sendMsgByOrgId(trainPlan.getOrgId(),
                    DateUtil.date(),
                    trainPlan.getType() == 2?3:4,
                    trainPlan.getId(),
                    trainPlan.getName());
        } else if(year > DateUtil.thisYear()){
            sendMsgByOrgId(trainPlan.getOrgId(),
                    DateUtil.parse(year+"-01-01", DatePattern.NORM_DATE_PATTERN),
                    trainPlan.getType() == 2?3:4,
                    trainPlan.getId(),
                    trainPlan.getName());
        }
    }

    /**
     * 分页列表
     */
    public List<Message> queryList(){
        Long personId = SecurityUtil.getPersonId();
        List<Message> list = baseMapper.queryList(personId);
        return list;
    }

    /**
     * 消息接收，修改为已读
     */
    @Transactional
    public boolean executeReceive(Long id) {
        baseMapper.updateRead(id);
        return true;
    }

    /**
     * 给党组织下的人员发送消息
     * @param orgId
     * @param date
     */
    public void sendMsgByOrgId(Long orgId, DateTime date,Integer type,Long dataId,String name) {
        List<Person> xmPeople = personService.findListByOrgId(orgId);
        List<Message> messageList = new ArrayList<>();
        String title = "";
        switch (type){
            case 1:
                title = "您接收到新的培训项目:";
                break;
            case 2:
                title = "";
                break;
            case 3:
                title = "您收到新的年度培训计划:";
                break;
            case 4:
                title = "您收到新的单位培训计划:";
                break;
            case 5:
                title = "您收到新的终端培训项目:";
                break;
        }
        for (Person person : xmPeople) {
            Message message = new Message();
            message.setPersonId(person.getId());
            message.setTitle(title + name);
            message.setType(type); /** 通知类型 1: 项目提醒 2:学习提醒 3:单位培训计划 4:年度培训计划 5:终端培训 */
            message.setDataId(dataId);

            // 设置默认参数
            message.setCreateBy(SecurityUtil.getUserId());
            message.setCreateTime(date);
            message.setIsDelete(YesOrNoState.NO.getState());
            message.setIsRead(0);

            messageList.add(message);
        }

        this.saveBatch(messageList);
    }
}
