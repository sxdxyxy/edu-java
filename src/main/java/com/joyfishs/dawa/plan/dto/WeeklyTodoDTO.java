package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class WeeklyTodoDTO implements Serializable {
    private Integer total;
    private List<StartingSoonDTO> startingSoon;
    private List<ProgressLaggingDTO> progressLagging;
    private List<OverduePersonDTO> overdueNotCompleted;
}
