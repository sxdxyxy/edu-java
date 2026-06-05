package com.joyfishs.dawa.statistics.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.statistics.domain.ActiveRecord;
import com.joyfishs.dawa.statistics.domain.DataStatisticsPersonal;
import com.joyfishs.dawa.statistics.domain.EventsList;
import com.joyfishs.dawa.statistics.domain.StatisticsByOrg;

@Mapper
public interface DataStatisticsMapper extends BaseMapper<Person> {

    List<DataStatisticsPersonal> listDataStatisticsPersonal(@Param("orgId") Long orgId, @Param("personName") String personName, @Param("asc") Integer asc, @Param("year") Integer year);

    List<DataStatisticsPersonal> listPersonalClassHourTop(@Param("orgId") Long orgId, @Param("asc") Integer asc);

    Map<String, Object> getDataCountByOrgId(@Param("orgId") Long orgId);

    int getNumberOfRegistrants(@Param("orgId") Long orgId);

    int getNumberOfActive(@Param("orgId") Long orgId, @Param("loginTime") LocalDateTime loginTime);

    int getNumberOfCourses(@Param("orgId") Long orgId );

    BigDecimal getFinishClassHours(@Param("orgId")Long orgId);

    List<EventsList> getLatelyEventsList(@Param("orgId") Long orgId);

    List<ActiveRecord> getLatelyActiveRecord(@Param("orgId") Long orgId, @Param("loginTime") LocalDateTime loginTime);

    List<StatisticsByOrg> getTotalOfCoursesByOrgStatistics(@Param("orgId") Long orgId);

    List<StatisticsByOrg> getByTotalOfClassHoursByOrgStatistics(@Param("orgId") Long orgId);

    List<DataStatisticsPersonal> getTotalOfCoursesForPersonal(@Param("orgId") Long orgId);

    List<DataStatisticsPersonal> getYearClassHourForPersonal(@Param("orgId") Long orgId);
}
