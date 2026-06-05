package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class StartingSoonDTO implements Serializable {
    private Long projectId;
    private String projectName;
    private LocalDate startDate;
}
