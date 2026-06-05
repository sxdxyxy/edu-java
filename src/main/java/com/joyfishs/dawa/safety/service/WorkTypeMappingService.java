package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.joyfishs.dawa.person.enums.WorkType;
import com.joyfishs.dawa.safety.mapper.WorkTypeMappingMapper;
import com.joyfishs.dawa.safety.entity.WorkTypeMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工种与岗位类型映射服务
 * <p>
 * 提供安全积分账户岗位类型与培训系统工种（WorkType 1-19）的双向查询
 * </p>
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Slf4j
@Service
public class WorkTypeMappingService {

    public static final String WORK_TYPE_WORKER = "worker";
    public static final String WORK_TYPE_SPECIALIZED = "specialized";
    public static final String WORK_TYPE_SAFETY_ADMIN = "safety_admin";

    @Autowired
    private WorkTypeMappingMapper workTypeMappingMapper;

    /** 缓存: workType -> WorkTypeMapping */
    private final Map<Integer, WorkTypeMapping> cacheByWorkType = new ConcurrentHashMap<>();

    /** 缓存: accountWorkType -> list of WorkTypeMapping */
    private final Map<String, WorkTypeMapping[]> cacheByAccountWorkType = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        log.info("初始化工种映射缓存...");
        refreshCache();
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        cacheByWorkType.clear();
        cacheByAccountWorkType.clear();

        QueryWrapper<WorkTypeMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("enabled", true);
        var list = workTypeMappingMapper.selectList(wrapper);

        for (WorkTypeMapping m : list) {
            cacheByWorkType.put(m.getWorkType(), m);
        }

        // 按 accountWorkType 分组
        Map<String, java.util.List<WorkTypeMapping>> grouped = list.stream()
                .collect(java.util.stream.Collectors.groupingBy(WorkTypeMapping::getAccountWorkType));
        for (var entry : grouped.entrySet()) {
            cacheByAccountWorkType.put(entry.getKey(),
                    entry.getValue().toArray(new WorkTypeMapping[0]));
        }

        log.info("工种映射缓存加载完成，共 {} 条记录", list.size());
    }

    /**
     * 根据工种编码获取安全积分岗位类型
     */
    public String getAccountWorkTypeByWorkType(Integer workType) {
        if (workType == null) {
            return WORK_TYPE_WORKER;
        }
        WorkTypeMapping mapping = cacheByWorkType.get(workType);
        if (mapping != null) {
            return mapping.getAccountWorkType();
        }
        // 未配置映射的工种默认 worker
        log.warn("工种 {} 未配置映射，使用默认 worker", workType);
        return WORK_TYPE_WORKER;
    }

    /**
     * 根据工种编码获取工种名称
     */
    public String getWorkTypeName(Integer workType) {
        if (workType == null) {
            return "未知";
        }
        WorkTypeMapping mapping = cacheByWorkType.get(workType);
        if (mapping != null) {
            return mapping.getWorkTypeName();
        }
        // fallback 到枚举
        WorkType wt = WorkType.value(workType);
        return wt != null ? wt.getDesc() : "未知";
    }

    /**
     * 根据工种编码获取初始积分
     */
    public int getInitialScoreByWorkType(Integer workType) {
        if (workType == null) {
            return 12;
        }
        WorkTypeMapping mapping = cacheByWorkType.get(workType);
        return mapping != null ? mapping.getInitialScore() : 12;
    }

    /**
     * 根据工种编码获取绿码阈值
     */
    public int getGreenThresholdByWorkType(Integer workType) {
        if (workType == null) {
            return 12;
        }
        WorkTypeMapping mapping = cacheByWorkType.get(workType);
        return mapping != null ? mapping.getGreenThreshold() : 12;
    }

    /**
     * 根据工种编码获取黄码阈值
     */
    public int getYellowThresholdByWorkType(Integer workType) {
        if (workType == null) {
            return 6;
        }
        WorkTypeMapping mapping = cacheByWorkType.get(workType);
        return mapping != null ? mapping.getYellowThreshold() : 6;
    }

    /**
     * 根据安全积分岗位类型获取所有关联的工种
     */
    public WorkTypeMapping[] getWorkTypesByAccountWorkType(String accountWorkType) {
        if (accountWorkType == null) {
            return new WorkTypeMapping[0];
        }
        return cacheByAccountWorkType.getOrDefault(accountWorkType, new WorkTypeMapping[0]);
    }

    /**
     * 根据人员工种编码，获取对应的账户岗位类型（兼容 xm_person.workType）
     * 此方法在 SafetyScoreAccount 创建时使用
     */
    public String resolveAccountWorkType(Integer personWorkType, String fallbackWorkType) {
        String result = getAccountWorkTypeByWorkType(personWorkType);
        return (result != null && !result.isEmpty()) ? result : fallbackWorkType;
    }
}
