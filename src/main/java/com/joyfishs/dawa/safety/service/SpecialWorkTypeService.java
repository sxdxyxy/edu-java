package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.entity.SpecialWorkType;
import com.joyfishs.dawa.safety.mapper.SpecialWorkTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 特种作业类型服务类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Service
public class SpecialWorkTypeService extends ServiceImpl<SpecialWorkTypeMapper, SpecialWorkType> {

    /**
     * 获取所有启用的特种作业类型
     */
    public List<SpecialWorkType> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    /**
     * 根据作业类型编码获取
     */
    public SpecialWorkType getByWorkTypeCode(String workTypeCode) {
        return baseMapper.selectByWorkTypeCode(workTypeCode);
    }

    /**
     * 根据危险等级获取启用的特种作业类型
     */
    public List<SpecialWorkType> getByDangerLevel(String dangerLevel) {
        QueryWrapper<SpecialWorkType> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "enabled")
               .eq("danger_level", dangerLevel)
               .orderByAsc("id");
        return list(wrapper);
    }
}
