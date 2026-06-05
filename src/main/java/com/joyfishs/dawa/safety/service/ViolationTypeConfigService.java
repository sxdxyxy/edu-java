package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.entity.ViolationTypeConfig;
import com.joyfishs.dawa.safety.mapper.ViolationTypeConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 违章类型配置服务类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Service
public class ViolationTypeConfigService extends ServiceImpl<ViolationTypeConfigMapper, ViolationTypeConfig> {

    /**
     * 获取所有启用的违章类型配置
     */
    public List<ViolationTypeConfig> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    /**
     * 根据违章代码获取配置
     */
    public ViolationTypeConfig getByViolationCode(String violationCode) {
        return baseMapper.selectByViolationCode(violationCode);
    }

    /**
     * 根据违章级别获取配置列表
     */
    public List<ViolationTypeConfig> getByLevel(String level) {
        return baseMapper.selectByLevel(level);
    }

    /**
     * 获取所有会触发培训的违章类型
     */
    public List<ViolationTypeConfig> getTrainingRequired() {
        QueryWrapper<ViolationTypeConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "enabled")
               .eq("trigger_training", 1)
               .orderByAsc("sort_order");
        return list(wrapper);
    }

    /**
     * 根据违章代码获取扣分分值
     */
    public Integer getDeductScore(String violationCode) {
        ViolationTypeConfig config = getByViolationCode(violationCode);
        return config != null ? config.getDeductScore() : 0;
    }
}
