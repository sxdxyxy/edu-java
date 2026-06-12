package com.joyfishs.dawa.plan.service;

import com.joyfishs.dawa.plan.dto.*;
import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.plan.mapper.PlanDashboardMapper;
import com.joyfishs.dawa.plan.mapper.TrainPlanMapper;
import com.joyfishs.system.config.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanDashboardService {

    private static final String DASHBOARD_CACHE_KEY = "plan:dashboard:";
    private static final int CACHE_MINUTES = 5;

    private final PlanDashboardMapper planDashboardMapper;
    private final TrainPlanMapper trainPlanMapper;
    private final RedisCache redisCache;

    public PlanDashboardDTO getDashboard(Long planId) {
        String cacheKey = DASHBOARD_CACHE_KEY + planId;

        // D3-B: Redis 故障不应阻塞驾驶舱 — 失败时跳过缓存, 直接聚合
        try {
            PlanDashboardDTO cached = redisCache.getCacheObject(cacheKey);
            if (cached != null) {
                log.debug("Dashboard cache hit for planId: {}", planId);
                return cached;
            }
        } catch (Exception e) {
            log.warn("Dashboard redis read failed for planId={}, fallback to DB", planId, e);
        }

        PlanDashboardDTO dto = aggregateDashboard(planId);
        try {
            redisCache.setCacheObject(cacheKey, dto, CACHE_MINUTES * 60, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Dashboard redis write failed for planId={}", planId, e);
        }
        return dto;
    }

    private PlanDashboardDTO aggregateDashboard(Long planId) {
        TrainPlan plan = trainPlanMapper.selectById(planId);
        if (plan == null) {
            throw new RuntimeException("培训计划不存在: " + planId);
        }

        List<ProjectSummaryDTO> projects = planDashboardMapper.selectProjectSummaries(planId);
        Long totalStudents = planDashboardMapper.selectTotalStudents(planId);
        CompletionStatsDTO stats = planDashboardMapper.selectCompletionStats(planId);

        double completionRate = stats.getTotal() > 0
            ? Math.round(stats.getCompleted() * 10000.0 / stats.getTotal()) / 100.0
            : 0.0;

        List<OverduePersonDTO> overduePersons = planDashboardMapper.selectOverduePersons(planId);
        double overdueRate = totalStudents > 0
            ? Math.round(overduePersons.size() * 10000.0 / totalStudents) / 100.0
            : 0.0;

        PlanKPIsDTO kpis = new PlanKPIsDTO();
        kpis.setTotalProjects((long) projects.size());
        kpis.setTotalStudents(totalStudents);
        kpis.setCompletionRate(completionRate);
        kpis.setOverdueRate(overdueRate);

        WeeklyTodoDTO weeklyTodo = buildWeeklyTodo(planId);

        PlanDashboardDTO dto = new PlanDashboardDTO();
        dto.setPlanId(planId);
        dto.setPlanName(plan.getName());
        dto.setKpis(kpis);
        dto.setWeeklyTodo(weeklyTodo);
        dto.setProjects(projects);
        dto.setOverduePersons(overduePersons);

        return dto;
    }

    private WeeklyTodoDTO buildWeeklyTodo(Long planId) {
        WeeklyTodoDTO todo = new WeeklyTodoDTO();

        List<StartingSoonDTO> startingSoon = planDashboardMapper.selectStartingSoon(planId, 7);
        List<ProgressLaggingDTO> lagging = planDashboardMapper.selectProgressLagging(planId);
        List<OverduePersonDTO> overdue = planDashboardMapper.selectOverduePersons(planId);

        todo.setTotal(startingSoon.size() + lagging.size() + overdue.size());
        todo.setStartingSoon(startingSoon);
        todo.setProgressLagging(lagging);
        todo.setOverdueNotCompleted(overdue);

        return todo;
    }

    public void refreshCache(Long planId) {
        String cacheKey = DASHBOARD_CACHE_KEY + planId;
        PlanDashboardDTO dto = aggregateDashboard(planId);
        redisCache.setCacheObject(cacheKey, dto, CACHE_MINUTES * 60, java.util.concurrent.TimeUnit.SECONDS);
        log.info("Dashboard cache refreshed for planId: {}", planId);
    }
}
