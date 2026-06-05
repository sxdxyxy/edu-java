package com.joyfishs.dawa.person.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.joyfishs.dawa.person.domain.po.ModifyCreditEvent;
import com.joyfishs.dawa.person.domain.vo.PersonCreditTopVo;
import com.joyfishs.dawa.person.domain.vo.PersonCreditVo;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.entity.PersonCredit;
import com.joyfishs.dawa.person.entity.PersonCreditDetail;
import com.joyfishs.dawa.person.enums.CreditType;
import com.joyfishs.dawa.person.mapper.PersonCreditDetailMapper;
import com.joyfishs.dawa.person.mapper.PersonCreditMapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@Service
public class PersonCreditService extends ServiceImpl<PersonCreditMapper, PersonCredit> {
    @Resource
    private PersonCreditDetailMapper creditDetailMapper;
    @Autowired
    private PersonService personService;

    public PersonCreditVo findByPersonId(Long personId) {
        PersonCredit userCredit = getByPersonId(personId);
        PersonCreditVo result = new PersonCreditVo();
        BeanUtil.copyProperties(userCredit, result);
        Person person = personService.get(userCredit.getPersonId());
        result.setOrgName(person.getOrgName());
        result.setName(person.getName());
        return result;
    }
    public PersonCredit getByPersonId(Long personId) {
        LambdaQueryWrapper<PersonCredit> lqw = Wrappers.lambdaQuery();
        lqw.eq(PersonCredit::getPersonId, personId);
        PersonCredit userCredit = getOne(lqw);
        if (ObjectUtil.isNull(userCredit)) {
            userCredit = new PersonCredit();
            userCredit.setPersonId(personId);
            userCredit.setCredit(0);
            save(userCredit);
        }
        return userCredit;
    }

    /**
     * 1、修改积分信息
     * 2、新增明细
     *
     * @param event 新增积分参数
     * @return 添加成功 返回最新积分
     */
    @Async
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void addCredit(ModifyCreditEvent event) {
        log.info("收到积分变动事件{},{}", event.getType(), event.getBusinessId());
        PersonCredit userCredit = getByPersonId(event.getPersonId());
        //此业务记录是否已存在
        PersonCreditDetail detail = findCreditDetailByBusinessId(event);
        if (ObjectUtil.isNotNull(detail)) {
            return;
        }
        int points = CreditType.value(event.getType()).getPoints();
        userCredit.setCredit(userCredit.getCredit() +points );
        //更新用户积分
        updateById(userCredit);
        PersonCreditDetail creditDetail = new PersonCreditDetail();
        BeanUtil.copyProperties(event, creditDetail);
        creditDetail.setPoints(points);
        creditDetail.setCreateTime(LocalDateTime.now());
        creditDetailMapper.insert(creditDetail);
    }

    public List<PersonCreditDetail> findCreditDetailListByPersonId(Long personId) {
        LambdaQueryWrapper<PersonCreditDetail> lqw = Wrappers.lambdaQuery();
        lqw.eq(PersonCreditDetail::getPersonId, personId);
        List<PersonCreditDetail> creditDetailList = creditDetailMapper.selectList(lqw);
        return creditDetailList;
    }

    public PersonCreditDetail findCreditDetailByBusinessId(ModifyCreditEvent event) {
        LambdaQueryWrapper<PersonCreditDetail> lqw = Wrappers.lambdaQuery();
        lqw.eq(PersonCreditDetail::getPersonId, event.getPersonId());
        lqw.eq(PersonCreditDetail::getType, event.getType());
        lqw.eq(PersonCreditDetail::getBusinessId, event.getBusinessId());
        return creditDetailMapper.selectOne(lqw);
    }

    public List<PersonCreditTopVo> getTopTen() {
        LambdaQueryWrapper<PersonCredit> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(PersonCredit::getCredit).last(" limit 10");
        List<PersonCredit> list = this.list(lqw);
        List<Long> personIds = list.stream().map(PersonCredit::getPersonId).distinct().collect(Collectors.toList());
        Map<Long, Person> personMap = personService.listByIds(personIds).stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        List<PersonCreditTopVo> result = Lists.newArrayList();
        int index = 1;
        for (PersonCredit item : list) {
            PersonCreditTopVo vo = new PersonCreditTopVo();
            vo.setIndex(index);
            index++;
            vo.setCredit(item.getCredit());
            vo.setNickname(personMap.get(item.getPersonId()).getName());
            result.add(vo);
        }
        return result;
    }

    public List<PersonCreditVo> listByPage(String name, Long orgId) {
        List<PersonCreditVo> creditList = baseMapper.selectByPage(name,orgId);
        return creditList;
    }
}
