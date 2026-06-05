package com.joyfishs.dawa.person.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.person.entity.PersonDetectAuth;
import com.joyfishs.dawa.person.mapper.PersonDetectAuthMapper;

/**
 * @author ykfnb
 */
@Service
public class PersonDetectAuthService extends ServiceImpl<PersonDetectAuthMapper, PersonDetectAuth> {

    public PersonDetectAuth findByOrderNo(String orderNo){
        LambdaQueryWrapper<PersonDetectAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonDetectAuth::getOrderNo, orderNo);
        return getOne(queryWrapper);
    }
    public PersonDetectAuth findByBizSeqNo(String bizSeqNo){
        LambdaQueryWrapper<PersonDetectAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonDetectAuth::getBizSeqNo, bizSeqNo);
        return getOne(queryWrapper);
    }

    public PersonDetectAuth findByBizToken(String bizToken) {
        LambdaQueryWrapper<PersonDetectAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonDetectAuth::getBizToken, bizToken);
        return getOne(queryWrapper);
    }
}
