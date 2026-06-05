package com.joyfishs.dawa.plan.mapper;

import com.joyfishs.dawa.plan.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PlanDashboardMapper {
    List<ProjectSummaryDTO> selectProjectSummaries(@Param("planId") Long planId);
    Long selectTotalStudents(@Param("planId") Long planId);
    CompletionStatsDTO selectCompletionStats(@Param("planId") Long planId);
    List<OverduePersonDTO> selectOverduePersons(@Param("planId") Long planId);
    List<StartingSoonDTO> selectStartingSoon(@Param("planId") Long planId, @Param("daysAhead") int days);
    List<ProgressLaggingDTO> selectProgressLagging(@Param("planId") Long planId);
}
