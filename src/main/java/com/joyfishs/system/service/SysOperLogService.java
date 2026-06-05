package com.joyfishs.system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.entity.SysOperLog;
import com.joyfishs.system.mapper.SysOperLogMapper;
import com.joyfishs.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysOperLogService extends ServiceImpl<SysOperLogMapper, SysOperLog> {
    public List<SysOperLog> queryList(SysOperLog sysOperLog) {
        log.info("SysOperLogService - queryList sysOperLog:{}", sysOperLog);
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(sysOperLog.getTitle())) queryWrapper.like(SysOperLog::getTitle, sysOperLog.getTitle());

        return list(queryWrapper);
    }
}
