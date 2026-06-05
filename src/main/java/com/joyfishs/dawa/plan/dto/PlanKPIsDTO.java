package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class PlanKPIsDTO implements Serializable {
    private Long totalProjects;
    private Long totalStudents;
    private Double completionRate;
    private Double overdueRate;
}
