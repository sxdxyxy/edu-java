package com.joyfishs.dawa.plan.controller;

import com.joyfishs.dawa.plan.dto.PlanDashboardDTO;
import com.joyfishs.dawa.plan.service.PlanDashboardService;
import com.joyfishs.utils.AjaxResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/{planId}/dashboard/refresh")
    public AjaxResult<Void> refreshCache(@PathVariable Long planId) {
        planDashboardService.refreshCache(planId);
        return AjaxResult.success();
    }
}
