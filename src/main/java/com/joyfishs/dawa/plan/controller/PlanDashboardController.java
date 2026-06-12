package com.joyfishs.dawa.plan.controller;

import com.joyfishs.dawa.plan.dto.PlanDashboardDTO;
import com.joyfishs.dawa.plan.service.PlanDashboardService;
import com.joyfishs.utils.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanDashboardController {

    private final PlanDashboardService planDashboardService;

    @GetMapping("/{planId}/dashboard")
    public AjaxResult<PlanDashboardDTO> getDashboard(@PathVariable Long planId) {
        try {
            PlanDashboardDTO dashboard = planDashboardService.getDashboard(planId);
            return AjaxResult.success(dashboard);
        } catch (Exception e) {
            // D3-B: 原代码只 catch RuntimeException, Redis 故障 / 反射等会逃逸成"系统繁忙"。
            //      改 catch Exception, 落到业务错误, 前端能看到具体原因
            log.error("plan dashboard failed, planId={}", planId, e);
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/{planId}/dashboard/refresh")
    public AjaxResult<Void> refreshCache(@PathVariable Long planId) {
        planDashboardService.refreshCache(planId);
        return AjaxResult.success();
    }
}
