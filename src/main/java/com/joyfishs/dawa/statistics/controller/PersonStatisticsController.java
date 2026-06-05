package com.joyfishs.dawa.statistics.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.person.domain.vo.PersonCreditVo;
import com.joyfishs.dawa.person.domain.vo.PersonDetailsVo;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonChangeLogService;
import com.joyfishs.dawa.person.service.PersonCreditService;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.statistics.domain.PersonStatisticsLearning;
import com.joyfishs.dawa.statistics.domain.StudyRecordExportVo;
import com.joyfishs.dawa.statistics.service.PersonStatisticsService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 * @description: 学员-数据统计
 */
@Slf4j
@RestController
@RequestMapping("/data/person")
@Api(tags = "个人学习统计")
@RequiredArgsConstructor
public class PersonStatisticsController extends BaseController {
    private final PersonService personService;
    private final PersonStatisticsService personStatisticsService;
    private final PersonCreditService personCreditService;
    private final PersonChangeLogService personChangeLogService;

    @GetMapping("/learningStatistics")
    @ApiOperation(value = "个人学习统计报告-移动端")
    public AjaxResult<?> learningStatistics() {
        PersonStatisticsLearning learning = personStatisticsService.learningStatistics(SecurityUtil.getPersonId(), LocalDate.now().getYear(),null);
        return AjaxResult.success(learning);
    }

    @GetMapping("/statistics")
    @ApiOperation(value = "个人学习统计报告-PC端")
    public R<PersonDetailsVo> homeStatistics(@RequestParam Long personId, @RequestParam int year, @RequestParam int trainType) {
        Person person = personService.getById(personId);
        PersonStatisticsLearning learning = personStatisticsService.learningStatistics(personId, year,trainType);
        PersonCreditVo credit = personCreditService.findByPersonId(personId);
        PersonDetailsVo result = new PersonDetailsVo();
        result.setCredit(credit.getCredit());
        result.setOrgName(credit.getOrgName());
        result.setName(learning.getName());
        result.setYearClassHour(learning.getYearClassHour());
        result.setPlanClassHour(learning.getPlanClassHour());
        result.setTotalClassHours(learning.getSumClassHour());
        result.setChangeLogs(personChangeLogService.listByUserIdAndOrgId(person.getUserId(), person.getOrgId()));
        result.setCourseList(learning.getCourseList());
        return R.ok(result);
    }
    @ApiOperation(value = "导出学习记录")
    @PostMapping("/exportStudyRecord")
    public R<StudyRecordExportVo> exportStudyRecord(@RequestParam Long personId,@RequestParam Long projectId) {
        return R.ok();
    }

    @ApiOperation(value = "结业凭证证书")
    @GetMapping("/passCertificate")
    public R<StudyRecordExportVo> passCertificate(@RequestParam Long personId,@RequestParam Long projectId) {
        Person person = personService.getById(personId);
        return R.ok(personStatisticsService.buildStudyRecordVo(projectId, person));
    }
}
