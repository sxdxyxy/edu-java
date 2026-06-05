package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PlanDashboardDTO implements Serializable {
    private Long planId;
    private String planName;
    private PlanKPIsDTO kpis;
    private WeeklyTodoDTO weeklyTodo;
    private List<ProjectSummaryDTO> projects;
    private List<OverduePersonDTO> overduePersons;
}
