package com.joyfishs.dawa.plan.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OverduePersonDTO implements Serializable {
    private Long personId;
    private String personName;
    private String projectName;
    private Integer overdueDays;
    private BigDecimal progress;
}
