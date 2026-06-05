package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class CompletionStatsDTO implements Serializable {
    private Long total;
    private Long completed;
}
