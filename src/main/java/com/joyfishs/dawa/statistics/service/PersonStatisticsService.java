package com.joyfishs.dawa.statistics.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.CalendarUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.NumberingFormat;
import com.deepoove.poi.data.NumberingRenderData;
import com.deepoove.poi.data.Numberings;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.exam.service.ExamService;
import com.joyfishs.dawa.person.domain.result.DateItem;
import com.joyfishs.dawa.person.domain.result.PersonDetail;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.mapper.PersonMapper;
import com.joyfishs.dawa.project.domain.vo.ProjectCourseList;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.entity.ProjectRelate;
import com.joyfishs.dawa.project.service.ProjectRelateService;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.dawa.project.service.ProjectTerminalTrainSignService;
import com.joyfishs.dawa.signature.domain.vo.SignedDocumentVo;
import com.joyfishs.dawa.signature.entity.OnePersonOneArchives;
import com.joyfishs.dawa.signature.service.SignedDocumentService;
import com.joyfishs.dawa.statistics.domain.PersonStatisticsLearning;
import com.joyfishs.dawa.statistics.domain.StudyRecordExportVo;
import com.joyfishs.dawa.student.domain.MyCourseList;
import com.joyfishs.dawa.student.service.ProjectPersonStudyRecordService;
import com.joyfishs.dawa.student.service.StudentCourseListService;
import com.joyfishs.oss.domain.UploadResult;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.config.CosConfig;
import com.joyfishs.utils.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangkaifeng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonStatisticsService extends ServiceImpl<PersonMapper, Person> {

    private final StudentCourseListService studentCourseListService;
    private final ExamService examService;
    private final ProjectPersonStudyRecordService studyRecordService;
    private final ProjectRelateService projectRelateService;
    private final CourseService courseService;
    private final ProjectService projectService;
    private final SysOssService ossService;
    private final ProjectTerminalTrainSignService trainSignService;
    private final SignedDocumentService signedDocumentService;

    public PersonStatisticsLearning learningStatistics(Long personId, int year, Integer trainType) {
        Person person = getById(personId);
        if (ObjectUtil.isNull(person)) {
            throw new CustomException("人员不存在");
        }

        String name = person.getName();
        PersonStatisticsLearning learning = new PersonStatisticsLearning();
        learning.setName(name);
        BigDecimal sumClassHour = studyRecordService.getBaseMapper().getSumStudyHours(personId);
        //总课程数
        Integer sumCourse = studentCourseListService.getTotalCourseCount(personId, 1);
        //结业课程数
        Integer completionCourse = studentCourseListService.getTotalCourseCount(personId, 3);
        learning.setSumClassHour(sumClassHour);
        learning.setSumCourse(sumCourse);
        learning.setCompletionCourse(completionCourse);
        learning.setExamCount(examService.getAttendExamCount());

        BigDecimal bxClassHour = studentCourseListService.getTotalLearnHours(personId, 1, year);
        BigDecimal xxClassHour = studentCourseListService.getTotalLearnHours(personId, 2, year);
        BigDecimal ggClassHour = studyRecordService.getBaseMapper().getNoticeStudyHours(personId);

        List<Map<String, Object>> classHourList = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "必修课程");
        map1.put("classHour", bxClassHour);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "选修课程");
        map2.put("classHour", xxClassHour);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("name", "必读公告");
        map3.put("classHour", ggClassHour);
        classHourList.add(map1);
        classHourList.add(map2);
        classHourList.add(map3);
        learning.setClassHourList(classHourList);

        // 年度学时完成情况
        learning.setPlanClassHour(person.getPlanClassHour());
        learning.setYearClassHour(bxClassHour.add(xxClassHour).add(ggClassHour));

        Calendar startCalendar = CalendarUtil.calendar();
        startCalendar.set(Calendar.YEAR, year);
        Date startDate = startCalendar.getTime();
        // 本年度所学课程 ，trainType=3是查询终端培训
        List<MyCourseList> courseList = null;
        if (trainType != null && trainType == 3) {
            courseList = trainSignService.getMyCourseList(personId);
        } else {
            courseList = studentCourseListService.getMyCourseList(personId, null, null, trainType, 3, DateUtil.beginOfYear(startDate));
        }
        learning.setCourseList(courseList);
        learning.setYearCourse(courseList.size());

        return learning;
    }

    public StudyRecordExportVo buildStudyRecordVo(Long projectId, Person person) {
        StudyRecordExportVo result = new StudyRecordExportVo();
        Project project = projectService.get(projectId);
        result.setTitle(project.getProjectName());
        result.setPersonName(person.getName());
        result.setIdCardNo(person.getIdCardNo());
        result.setFacePhotoUrl(person.getFacePhotoUrl());
        SimpleDateFormat sdf = new SimpleDateFormat(DatePattern.NORM_DATE_PATTERN);
        result.setStartDate(sdf.format(project.getTrainSdate()));
        result.setEndDate(sdf.format(project.getTrainEdate()));
        result.setJobsName(person.getJobsName());
        result.setWorkTypeName(person.getWorkTypeName());
        result.setTotalLearnHours(project.getTotalLearnHours());
        //课程列表
        List<ProjectRelate> courseByProjectId = projectRelateService.getCourseByProjectId(projectId);
        List<String> courseIdList = courseByProjectId.stream().map(ProjectRelate::getRelateIds).collect(Collectors.toList());
        List<ProjectCourseList> courseList = courseService.getListByIds(courseIdList, null);
        result.setCourseList(courseList);
        result.setSex(person.getSexStr());
        result.setPhone(person.getPhone());
        result.setBirthday(person.getBirthday());
        result.setNumber(DateUtil.format(project.getTrainSdate(), DatePattern.PURE_DATE_PATTERN) + NumberUtil.decimalFormat("00000", person.getId()));
        return result;
    }


    /**
     * 构建学习记录文档
     *
     * @param projectId
     * @param person
     */
    public UploadResult buildLearningRecord(Long projectId, Person person) {
        StudyRecordExportVo sreVo = buildStudyRecordVo(projectId, person);
        NumberingFormat.Builder builder = NumberingFormat.DECIMAL_BUILDER;
        NumberingFormat numberingFormat = builder.build(0);
        Numberings.NumberingBuilder of = Numberings.of(numberingFormat);
        sreVo.getCourseList().forEach(item -> of.addItem(item.getCourseName()));
        NumberingRenderData numberingRenderData = of.create();
        sreVo.setCourseNameList(numberingRenderData);
        File templateFile = ossService.download(OnePersonOneArchives.LEARNING_RECORD);
        XWPFTemplate template = XWPFTemplate.compile(templateFile);
        template.render(sreVo);
        File outputDocx = FileUtil.createTempFile(OnePersonOneArchives.DOCX, true);
        try {
            template.writeAndClose(new FileOutputStream(outputDocx));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String signedFileKey = (CosConfig.TEMPORARY + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + IdUtil.objectId() + StrUtil.DOT + OnePersonOneArchives.DOCX);
        return ossService.upload(outputDocx, signedFileKey);
    }

    /**
     * 扫码人员详情
     */
    public PersonDetail summary(Person person) {
        PersonDetail result = BeanUtil.copyProperties(person, PersonDetail.class);
        List<DateItem> dataItems = new ArrayList<>();

        if (ObjectUtil.isNotNull(person.getWorkType())) {
            List<SignedDocumentVo> signedDocuments = signedDocumentService.queryList(person).getSignedDocument();
            signedDocuments.forEach(item -> {
                if (ObjectUtil.isNotNull(item.getCreateTime())) {
                    dataItems.add(new DateItem(item.getCreateTime().toLocalDate(), item.getDocumentName(), item.getId(), "已完成", 1).setStyle().setFileUrl(item.getFileUrl()));
                }
            });
        }

        List<MyCourseList> courseList = studentCourseListService.getMyCourseList(person.getId(), null, null, null, 3, null);
        courseList.forEach(item -> {
            dataItems.add(new DateItem(LocalDateTimeUtil.of(item.getEndDate()).toLocalDate(), item.getName(), item.getId(), String.valueOf(item.getScore().intValue()), 0).setStyle());

        });
        result.getCertificateList().forEach(item -> {
            item.setFileUrl("");
            dataItems.add(new DateItem(LocalDateTimeUtil.of(item.getCreateTime()).toLocalDate(), item.getName(), item.getId(), "已上传", 2).setStyle().setFileUrl(item.getFileUrl()));
        });
        result.setGroupedData(dataItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                )));
        return result;
    }
}
