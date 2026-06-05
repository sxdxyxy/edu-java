package com.joyfishs.dawa.plan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.plan.mapper.TrainPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanCacheRefreshService {

    private final PlanDashboardService planDashboardService;
    private final TrainPlanMapper trainPlanMapper;

    @Scheduled(cron = "0 */5 * * * *")
    public void refreshAllPlanDashboards() {
        log.info("Starting scheduled dashboard cache refresh");
        QueryWrapper<TrainPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        List<TrainPlan> plans = trainPlanMapper.selectList(wrapper);

        int success = 0;
        int failed = 0;
        for (TrainPlan plan : plans) {
            try {
                planDashboardService.refreshCache(plan.getId());
                success++;
            } catch (Exception e) {
                log.error("Failed to refresh dashboard for planId: {}", plan.getId(), e);
                failed++;
            }
        }
        log.info("Dashboard cache refresh completed. Success: {}, Failed: {}", success, failed);
    }
}
