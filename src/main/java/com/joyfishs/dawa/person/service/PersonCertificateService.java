package com.joyfishs.dawa.person.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.entity.PersonCertificate;
import com.joyfishs.dawa.person.mapper.PersonCertificateMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.util.ObjectUtil;

@Service
public class PersonCertificateService extends ServiceImpl<PersonCertificateMapper, PersonCertificate> {

    public boolean executeOrUpdate(PersonCertificate personCertificate) {
        if (ObjectUtil.isEmpty(personCertificate.getId())) {
            personCertificate.setCreateBy(SecurityUtil.getUserId());
            personCertificate.setCreateTime(new Date());
            personCertificate.setState(this.getState(personCertificate.getValidityEndDate()));
            personCertificate.setIsDelete(YesOrNoState.NO.getState());
        } else {
            personCertificate.setUpdateBy(SecurityUtil.getUserId());
            personCertificate.setUpdateTime(new Date());
            personCertificate.setState(this.getState(personCertificate.getValidityEndDate()));
        }
        return saveOrUpdate(personCertificate);
    }

    private int getState(Date validityEndDate) {
        if (validityEndDate.before(new Date())) {
            return 2;
        }
        return 1;
    }

    public boolean del(Long id) {
        PersonCertificate personCertificate = getById(id);
        if (ObjectUtil.isEmpty(personCertificate)) {
            throw new CustomException("删除记录不存在");
        }
        personCertificate.setIsDelete(YesOrNoState.YES.getState());
        personCertificate.setDeleteBy(SecurityUtil.getUserId());
        personCertificate.setDeleteTime(new Date());
        return updateById(personCertificate);
    }

    public List<PersonCertificate> listByPersonId(Long personId) {
        LambdaQueryWrapper<PersonCertificate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonCertificate::getPersonId, personId);
        queryWrapper.eq(PersonCertificate::getIsDelete, YesOrNoState.NO.getState());
        return list(queryWrapper);
    }
}
