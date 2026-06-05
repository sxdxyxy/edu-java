package com.joyfishs.dawa.person.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.domain.param.PersonOrgAddParam;
import com.joyfishs.dawa.person.entity.PersonOrg;
import com.joyfishs.dawa.person.mapper.PersonOrgMapper;

/**
 * @author ykfnb
 */
@Service
public class PersonOrgService extends ServiceImpl<PersonOrgMapper, PersonOrg> {

    /**
     * 根据项目查询用户
     */
    public List<Long> listByOrgId(Long orgId) {
        LambdaQueryWrapper<PersonOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonOrg::getOrgId, orgId);

        List<PersonOrg> personOrgList = baseMapper.selectList(queryWrapper);
        List<Long> personIds = personOrgList.stream().map(PersonOrg::getPersonId).collect(Collectors.toList());
        return personIds;
    }

    /**
     * 新增人员项目关联
     */
    public Boolean addPersonOrg(PersonOrgAddParam param) {
        // 先删除绑定过的
        LambdaQueryWrapper<PersonOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonOrg::getOrgId, param.getOrgId());
        baseMapper.delete(queryWrapper);

        // 再插入新增的
        List<PersonOrg> list = new ArrayList<>();
        for (Long personId : param.getPersonIds()) {
            PersonOrg personOrg = new PersonOrg();
            personOrg.setOrgId(param.getOrgId());
            personOrg.setPersonId(personId);
            list.add(personOrg);
        }
        return this.saveBatch(list);
    }

    /**
     * 移除人员项目关联
     */
    public Boolean removePersonOrg(Long orgId, Long personId) {
        LambdaQueryWrapper<PersonOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonOrg::getOrgId, orgId);
        queryWrapper.eq(PersonOrg::getPersonId, personId);
        baseMapper.delete(queryWrapper);

        return true;
    }
}
