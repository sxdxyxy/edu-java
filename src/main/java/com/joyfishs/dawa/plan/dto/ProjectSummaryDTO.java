package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProjectSummaryDTO implements Serializable {
    private Long projectId;
    private String projectName;
    private Integer status;
    private Integer studentCount;
    private Integer completedCount;
    private BigDecimal completionRate;
    private Integer overdueCount;
}
