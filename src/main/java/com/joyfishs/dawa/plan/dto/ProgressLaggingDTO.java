package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ProgressLaggingDTO implements Serializable {
    private Long personId;
    private String personName;
    private String projectName;
    private LocalDate deadline;
}
