package com.joyfishs.dawa.statistics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.access.service.AccessRecordService;
import com.joyfishs.dawa.assessment.service.PracticalAssessmentService;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.qualification.entity.Qualification;
import com.joyfishs.dawa.qualification.service.QualificationService;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.dawa.statistics.domain.*;
import com.joyfishs.dawa.statistics.mapper.DataStatisticsMapper;
import com.joyfishs.dawa.violation.service.ViolationRecordService;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataStatisticsService extends ServiceImpl<DataStatisticsMapper, Person> {

    private final SysOrgService sysOrgService;
    private final CourseService courseService;
    
    @Autowired
    private SafetyCodeService safetyCodeService;
    
    @Autowired
    private QualificationService qualificationService;
    
    @Autowired
    private ViolationRecordService violationRecordService;
    
    @Autowired
    private AccessRecordService accessRecordService;
    
    @Autowired
    private PracticalAssessmentService practicalAssessmentService;

    /**
     * 部门统计
     * @param orgId
     * @return
     */
    public List<DataStatisticsOrg> listDataStatisticsOrg(Long orgId) {
        // 查询组织列表
        SysOrg sysOrg = new SysOrg();
        sysOrg.setOrgType(1);
        sysOrg.setPid(orgId);
        List<SysOrg> sysOrgList = sysOrgService.queryList(sysOrg);

        List<DataStatisticsOrg> dataStatisticsOrgList = new ArrayList<>();
        for (SysOrg org : sysOrgList) {
            DataStatisticsOrg data = new DataStatisticsOrg();
            Map<String, Object> result = baseMapper.getDataCountByOrgId(org.getId());
            data.setOrgName(org.getName());
            if (null != result) {
                Integer personCount = Integer.parseInt(result.get("person_count").toString());
                Integer trainCount = Integer.valueOf(result.get("train_count").toString());
                BigDecimal yearClassHour = new BigDecimal(result.get("year_class_hour").toString());
                BigDecimal sumClassHour = new BigDecimal(result.get("sum_class_hour").toString());

                if (personCount > 0) {
                    BigDecimal trainRate = BigDecimal.valueOf(new BigDecimal(trainCount).divide(new BigDecimal(personCount),4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue());
                    BigDecimal avgClassHour = sumClassHour.divide(new BigDecimal(personCount), 2, RoundingMode.HALF_UP);
                    data.setTrainRate(trainRate);
                    data.setAvgClassHour(avgClassHour);
                }

                data.setPersonCount(personCount);
                data.setTrainCount(trainCount);
                data.setYearClassHour(yearClassHour);
                data.setSumClassHour(sumClassHour);
            }
            dataStatisticsOrgList.add(data);
        }
        return dataStatisticsOrgList;
    }

    // 个人统计
    public List<DataStatisticsPersonal> listDataStatisticsPersonal(Long orgId, String personName, Integer asc, Integer year) {
        if (ObjectUtil.isNull(year)) {
            year = LocalDate.now().getYear();
        }
        List<DataStatisticsPersonal> dataStatisticsPersonalList = baseMapper.listDataStatisticsPersonal(orgId, personName, asc, year);
        return dataStatisticsPersonalList;
    }

    /**
     * 个人统计公司学时前十统计
     * @param orgId
     * @param type 1:公司累计学时学时前十统计 2:公司累计学时学时前十统计
     */
    public List<DataStatisticsPersonal> listPersonalClassHourTop(Long orgId, Integer type) {
        List<DataStatisticsPersonal> dataStatisticsPersonalList = baseMapper.listPersonalClassHourTop(orgId, type);
        return dataStatisticsPersonalList;
    }
    public HomeStatistics getHomeStatistics(Long orgId){
        HomeStatistics result = new HomeStatistics();
        result.setNumberOfRegistrants(baseMapper.getNumberOfRegistrants(orgId));
        result.setNumberOfActive(baseMapper.getNumberOfActive(orgId, LocalDateTimeUtil.beginOfDay(LocalDateTime.now().plusDays(-15))));
        LambdaQueryWrapper<Course> courseWrapper = Wrappers.lambdaQuery();
        courseWrapper.eq(Course::getIsDelete, Boolean.FALSE);
        result.setNumberOfCourses( courseService.count(courseWrapper));
        Map<String, Object> classHourResult = baseMapper.getDataCountByOrgId(orgId);
        Object personCountObj = (classHourResult != null) ? classHourResult.get("person_count") : null;
        result.setAverageClassHours(personCountObj != null ? new BigDecimal(personCountObj.toString()) : BigDecimal.ZERO);

        // 添加 dawa 安全模块统计数据
        // 总人数 = 有安全码的人数 + 有准入记录的人数（去重）
        result.setTotalUsers(getTotalUsersInDawa(orgId));
        // 在场人数 = 当前在场人数
        result.setOnSiteUsers((int) accessRecordService.getCurrentOnsiteCount(orgId));
        return result;
    }

    /**
     * 获取 Dawa 安全模块中的总人数
     */
    private Integer getTotalUsersInDawa(Long orgId) {
        // 统计所有关联了安全码的用户数量
        return (int) safetyCodeService.count();
    }

    public OverviewStatistics getOverviewStatistics(Long orgId) {
        OverviewStatistics result = new OverviewStatistics();
        result.setNumberOfPersonnel(baseMapper.getNumberOfRegistrants(orgId));
        result.setNumberOfCourses(baseMapper.getNumberOfCourses(orgId));
        result.setFinishClassHours(baseMapper.getFinishClassHours(orgId));
        Map<String, Object> classHourResult = baseMapper.getDataCountByOrgId(orgId);
        Object personCountObj2 = (classHourResult != null) ? classHourResult.get("person_count") : null;
        result.setAverageClassHours(personCountObj2 != null ? new BigDecimal(personCountObj2.toString()) : BigDecimal.ZERO);
        List<StatisticsByOrg> totalOfCourses = baseMapper.getTotalOfCoursesByOrgStatistics(orgId);
        List<StatisticsByOrg> totalOfClassHours = baseMapper.getByTotalOfClassHoursByOrgStatistics(orgId);
        result.setTotalOfCourses(totalOfCourses);
        result.setTotalOfClassHours(totalOfClassHours);
        List<DataStatisticsPersonal> totalOfCoursesForPersonal = baseMapper.getTotalOfCoursesForPersonal(orgId);
        List<DataStatisticsPersonal> yearClassHourForPersonal = baseMapper.getYearClassHourForPersonal(orgId);
        result.setTotalOfCoursesForPersonal(totalOfCoursesForPersonal);
        result.setYearClassHourForPersonal(yearClassHourForPersonal);
        result.setActiveRecord(getHomeActiveRecord(orgId, 15));

        // 添加 dawa 模块统计数据
        result.setSafetyCodeRate(getSafetyCodeRate(orgId));
        result.setQualificationRate(getQualificationRate(orgId));
        result.setViolationCount(violationRecordService.countAll());
        result.setPendingViolations(violationRecordService.countByStatus("pending"));
        result.setAssessmentCount(practicalAssessmentService.countAll());
        result.setAssessmentPassRate(getAssessmentPassRate(orgId));
        result.setViolationTrend(getViolationTrend(orgId));
        result.setAssessmentDistribution(getAssessmentDistribution(orgId));
        result.setViolationTypeDistribution(getViolationTypeDistribution(orgId));
        result.setQualificationDistribution(getQualificationDistribution(orgId));
        return result;
    }

    public List<EventsList> getHomeEventsList(Long orgId) {
        List<EventsList> result = baseMapper.getLatelyEventsList(orgId);
        return result;
    }

    public List<ActiveRecord> getHomeActiveRecord(Long orgId,int days) {
        List<ActiveRecord> result = baseMapper.getLatelyActiveRecord(orgId, LocalDateTimeUtil.beginOfDay(LocalDateTime.now().plusDays(-days)));
        Map<LocalDate, ActiveRecord> mapRes = result.stream().collect(Collectors.toMap(ActiveRecord::getDate, obj -> obj));
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < days; i++) {
            LocalDate date = currentDate.minusDays(i);
            if (ObjectUtil.isNull(mapRes.get(date))) {
                mapRes.put(date, new ActiveRecord(date, 0));
            }
        }
        return mapRes.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(Map.Entry::getValue).collect(Collectors.toList());
    }
    
    /**
     * T20: 安全码统计 (三段式: 绿/黄/红, 三色数字互斥)
     */
    public Map<String, Object> getSafetyCodeStatistics(Long orgId) {
        Map<String, Object> result = new HashMap<>();

        // 今日新增
        long todayNewCount = safetyCodeService.countTodayNewCodes(orgId);

        // 原始计数 (注意: countValidCodes 包含全部 active 码, 包括 0-90 天到期的 yellow 范围)
        long totalActiveCount = safetyCodeService.countValidCodes(orgId);
        long yellowCount     = safetyCodeService.countExpiringCodes(orgId);  // 0 ≤ 剩余天数 < 90
        long redCount        = safetyCodeService.countExpiredCodes(orgId);

        // 绿码 = 全部 active - 即将到期 (yellow) (互斥, 三色数字加起来 = total)
        long greenCount = totalActiveCount - yellowCount;

        result.put("todayNew", todayNewCount);
        result.put("greenCount", greenCount);
        result.put("yellowCount", yellowCount);
        result.put("redCount", redCount);

        return result;
    }

    /**
     * T21: 资质证件到期预警
     */
    public List<Map<String, Object>> getQualificationExpiryWarnings(Long orgId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取即将过期的资质证件（例如30天内过期）
        List<Qualification> expiringQualifications = qualificationService.getExpiringQualifications(orgId, 30);
        
        for (Qualification qual : expiringQualifications) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", qual.getId());
            item.put("qualificationName", qual.getQualificationName());
            item.put("holderName", qual.getHolderName());
            item.put("expiryDate", qual.getExpiryDate());
            item.put("daysLeft", qual.getDaysToExpiry());
            item.put("orgId", qual.getOrgId());
            result.add(item);
        }
        
        return result;
    }

    /**
     * T22: 违章记分排行榜top10
     */
    public List<Map<String, Object>> getViolationRankTop10(Long orgId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取违章记分排行前10名
        List<Map<String, Object>> top10Violators = violationRecordService.getTop10ViolatorsByScore(orgId);
        
        for (Map<String, Object> violator : top10Violators) {
            Map<String, Object> item = new HashMap<>();
            item.put("userId", violator.get("userId"));
            item.put("userName", violator.get("userName"));
            item.put("totalScore", violator.get("totalScore"));
            item.put("violationCount", violator.get("violationCount"));
            result.add(item);
        }
        
        return result;
    }

    /**
     * T23: 准入记录今日统计
     */
    public Map<String, Object> getAccessRecordTodayStatistics(Long orgId) {
        Map<String, Object> result = new HashMap<>();
        
        // 今日入场人数
        long todayEntryCount = accessRecordService.countTodayEntries(orgId);
        
        // 今日出场人数
        long todayExitCount = accessRecordService.countTodayExits(orgId);
        
        // 当前在场人数
        long currentOnsiteCount = accessRecordService.getCurrentOnsiteCount(orgId);
        
        result.put("todayEntryCount", todayEntryCount);
        result.put("todayExitCount", todayExitCount);
        result.put("currentOnsiteCount", currentOnsiteCount);
        
        return result;
    }

    /**
     * T24: 实操考核通过率统计
     */
    public Map<String, Object> getAssessmentPassRateStatistics(Long orgId) {
        Map<String, Object> result = new HashMap<>();

        // 总考核次数
        long totalAssessments = practicalAssessmentService.countTotalAssessments(orgId);

        // 通过考核次数
        long passedAssessments = practicalAssessmentService.countPassedAssessments(orgId);

        // 优秀考核次数
        long excellentAssessments = practicalAssessmentService.countExcellentAssessments(orgId);

        // 通过率
        double passRate = totalAssessments > 0 ? (double) passedAssessments / totalAssessments * 100 : 0.0;
        double excellentRate = totalAssessments > 0 ? (double) excellentAssessments / totalAssessments * 100 : 0.0;

        result.put("totalAssessments", totalAssessments);
        result.put("passedAssessments", passedAssessments);
        result.put("excellentAssessments", excellentAssessments);
        result.put("passRate", Math.round(passRate * 100.0) / 100.0);
        result.put("excellentRate", Math.round(excellentRate * 100.0) / 100.0);

        return result;
    }

    /**
     * 计算安全码有效率
     */
    private Double getSafetyCodeRate(Long orgId) {
        long validCount = safetyCodeService.countValidCodes(orgId);
        long totalCount = safetyCodeService.count();
        return totalCount > 0 ? (double) validCount / totalCount * 100 : 0.0;
    }

    /**
     * 计算资质合规率
     */
    private Double getQualificationRate(Long orgId) {
        long validCount = qualificationService.countValidQualifications(orgId);
        long totalCount = qualificationService.count();
        return totalCount > 0 ? (double) validCount / totalCount * 100 : 0.0;
    }

    /**
     * 计算考核通过率
     */
    private Double getAssessmentPassRate(Long orgId) {
        long totalAssessments = practicalAssessmentService.countTotalAssessments(orgId);
        long passedAssessments = practicalAssessmentService.countPassedAssessments(orgId);
        return totalAssessments > 0 ? (double) passedAssessments / totalAssessments * 100 : 0.0;
    }

    /**
     * 获取近 7 天违章趋势
     */
    private List<Map<String, Object>> getViolationTrend(Long orgId) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            long count = violationRecordService.countByDate(date, orgId);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            trend.add(item);
        }
        return trend;
    }

    /**
     * 获取考核结果分布
     */
    private List<Map<String, Object>> getAssessmentDistribution(Long orgId) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        long excellent = practicalAssessmentService.countExcellentAssessments(orgId);
        long passed = practicalAssessmentService.countPassedAssessments(orgId) - excellent;
        long failed = practicalAssessmentService.countFailed();

        Map<String, Object> excellentMap = new HashMap<>();
        excellentMap.put("name", "优秀");
        excellentMap.put("count", excellent);
        distribution.add(excellentMap);

        Map<String, Object> passedMap = new HashMap<>();
        passedMap.put("name", "通过");
        passedMap.put("count", passed);
        distribution.add(passedMap);

        Map<String, Object> failedMap = new HashMap<>();
        failedMap.put("name", "未通过");
        failedMap.put("count", failed);
        distribution.add(failedMap);

        return distribution;
    }

    /**
     * 获取违章类型分布
     */
    private List<Map<String, Object>> getViolationTypeDistribution(Long orgId) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        String[] types = {"行为违章", "管理违章", "装置违章"};
        for (String type : types) {
            long count = violationRecordService.countByType(type, orgId);
            Map<String, Object> item = new HashMap<>();
            item.put("name", type);
            item.put("count", count);
            distribution.add(item);
        }
        return distribution;
    }

    /**
     * 获取资质类型分布
     */
    private List<Map<String, Object>> getQualificationDistribution(Long orgId) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        String[] types = {"电工证", "焊工证", "高空作业证", "特种作业操作证"};
        for (String type : types) {
            long count = qualificationService.countByCertType(type);
            Map<String, Object> item = new HashMap<>();
            item.put("name", type);
            item.put("count", count);
            distribution.add(item);
        }
        return distribution;
    }
}

