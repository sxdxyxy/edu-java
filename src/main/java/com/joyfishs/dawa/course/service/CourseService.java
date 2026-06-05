package com.joyfishs.dawa.course.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.joyfishs.dawa.course.controller.AliVodController;
import com.joyfishs.dawa.course.domain.vo.CourseTypeWordVo;
import com.joyfishs.dawa.course.domain.vo.CourseWordVo;
import com.joyfishs.dawa.course.domain.vo.CoursewareVo;
import com.joyfishs.dawa.course.domain.vo.QuestionWordVo;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.course.entity.CourseQuestion;
import com.joyfishs.dawa.course.entity.CourseTag;
import com.joyfishs.dawa.course.entity.Courseware;
import com.joyfishs.dawa.course.mapper.CourseMapper;
import com.joyfishs.dawa.project.domain.vo.ProjectCourseList;
import com.joyfishs.dawa.project.service.ProjectRelateService;
import com.joyfishs.dawa.utils.Dictionary;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.entity.SysDataDictionaryItem;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.service.SysDataDictionaryItemService;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;
import com.yaoan.liveapi.CourseApi;
import com.yaoan.liveapi.vo.CompanyCourseSectionVo;
import com.yaoan.liveapi.vo.CompanyCourseVo;
import com.yaoan.liveapi.vo.CompanyCoursewareVo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @create 2021/8/17 9:31
 */

@Slf4j
@Service
public class CourseService extends ServiceImpl<CourseMapper, Course> {
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;
    @Autowired
    private CourseQuestionService questionService;
    @Autowired
    private CourseCoursewareService courseCoursewareService;
    @Autowired
    private CourseRelationCoursewareService courseRelationCoursewareService;
    @Autowired
    private SysDataDictionaryItemService dictionaryItemService;
    @Autowired
    private CourseTagService courseTagService;
    @Autowired
    private SysOssService ossService;
    @Autowired
    private ProjectRelateService projectRelateService;

    /**
     * 新增 或 修改 课程
     *
     * @param t
     */
    @Transactional
    public void addOrUpdate(Course t) {
        if (ObjectUtil.isNull(t.getType())) {
            throw new CustomException("课程类型不能为空！");
        }

        Course course = getById(t.getId());
        if (StringUtils.isEmpty(t.getCourseName())) {
            throw new CustomException("课程名称不能为空！");
        }

        if (course == null || StringUtils.isNull(t.getId())) {
            //设置课程编号
            t.setCourseCode(genCode());
            t.setIsDelete(YesOrNoState.NO.getState());
            t.setCreateTime(new Date());
            t.setCreateBy(SecurityUtil.getUserId());
            save(t);
        } else {
            t.setUpdateTime(new Date());
            t.setUpdateBy(SecurityUtil.getUserId());
            updateById(t);
        }

        if (t.getTags() == null || t.getTags().size() == 0) {
            return;
        }

        // 删除课程关联标签数据
        courseTagService.delTagByCourseId(t.getId());
        //遍历得到标签
        for (String tags : t.getTags()) {
            //通过标签名称去词典查询是否存在
            SysDataDictionaryItem dictionaryItem = null;
            try {
                int tags_value = Integer.parseInt(tags);

                dictionaryItem = dictionaryItemService.getBaseMapper().findByValueAndCode(tags_value, "0008");
            } catch (Exception e) {
            }

            //如果标签没查到则新增一个
            if (dictionaryItem == null || StringUtils.isNull(dictionaryItem.getId())) {
                SysDataDictionaryItem item = new SysDataDictionaryItem();
                item.setDictionaryCode("0008");
                item.setName(tags);
                item.setValue(dictionaryItemService.findMaxValueByCode("0008"));
                item.setParentid(0);
                item.setSortid(item.getValue());
                item.setCreateBy(SecurityUtil.getUserId());
                item.setCreateTime(new Date());
                item.setIsDelete(YesOrNoState.NO.getState());
                dictionaryItemService.saveOrUpdate(item);
                //往关联表中插入
                courseTagService.saveTag(t.getId(), item.getValue());
            } else {
                courseTagService.saveTag(t.getId(), dictionaryItem.getValue());
            }
        }
    }

    /**
     * 生成课程编号
     *
     * @return
     */
    public String genCode() {
        return "XT" + new SimpleDateFormat("yyyyMMddSSS").format(new Date()) + (int) (Math.random() * 9 + 1) + (int) (Math.random() * 9 + 1);
    }

    /**
     * 删除课程
     *
     * @param ids          课程ID
     * @param deleteReason
     */
    @Transactional
    public void deleteCourse(String ids, String deleteReason) {
        if (StringUtils.isEmpty(ids)) {
            throw new CustomException("请选择要删除的课程！");
        }

        String[] courseId = ids.split(",");
        for (String id : courseId) {
            Course course = getById(id);

            if (course == null || StringUtils.isNull(course.getId())) {
                throw new CustomException("未找到所选课程！");
            }

            course.setDeleteTime(new Date());
            course.setDeleteReason(deleteReason);
            course.setDeleteBy(SecurityUtil.getUserId());
            course.setIsDelete(YesOrNoState.YES.getState());
            updateById(course);
            
            // 删除课程与项目的关联关系
            projectRelateService.removeByCourseId(Long.valueOf(id));
        }
    }


    /**
     * 通过课程ID获取课程详情-课件列表
     *
     * @param id 课程ID
     * @return 课件列表
     */
    public List<CoursewareVo> getDetailsById(Long id) {
        log.info("CourseService - getDetailsById id:{}", id);
        if (StringUtils.isNull(id)) {
            throw new CustomException("课程ID不能为空！");
        }

        Course course = getById(id);
        log.info("CourseService - getDetailsById:{}", course);
        if (course == null || StringUtils.isNull(course)) {
            throw new CustomException("未找到该课程！");
        }

        //通过课程ID获取课件
        List<CoursewareVo> coursewareList = courseCoursewareService.getCoursewareList(id);
        log.info("CourseService - coursewareList:{}", coursewareList);
        return coursewareList;
    }


    /**
     * 获取课程集合
     *
     * @return
     */
    public List<Course> getCourseList(Course t) {
        List<Course> courseList = baseMapper.getCourseList(t);
        for (Course course : courseList) {
            //通过课程ID获取题目数量
            course.setQuestionCount(questionService.getQuestionCount(course.getId())); //getQuestionIds(course.getId()).size()
            //获取 课程类别
            course.setCourseTypeName(dictionaryItemService.findDictonaryBydictonaryCode("0006", course.getCourseType()));
            //设置总课时
            course.setHour(String.format("%s", courseRelationCoursewareService.getTotalHoursForCourse(course.getId())));
            course.setTotalDuration(String.format("%s", courseRelationCoursewareService.getTotalDuration(course.getId())));
        }
        return courseList;
    }

    /**
     * 查询课程列表
     *
     * @param courseIdList
     * @return
     */
    public List<ProjectCourseList> getListByIds(List<String> courseIdList, String courseName) {
        return baseMapper.getListByIds(courseIdList, courseName);
    }

    /**
     * 根据项目id查询课程列表
     *
     * @param projectId
     * @return
     */
    public List<ProjectCourseList> getListByProjectId(Long projectId) {
        return baseMapper.getListByProjectId(projectId);
    }

    /**
     * 通过课程ID获取课程、下所有题目ID
     *
     * @param t 课程参数
     * @return 题目
     */
    public List<Long> getQuestionIdList(Course t) {
        if (StringUtils.isNull(t.getId())) {
            throw new CustomException("课程ID不能为空！");
        }

        Course course = getById(t.getId());
        if (course == null || StringUtils.isNull(course.getId())) {
            throw new CustomException("课程未找到！");
        }

        //通过课程ID找到课程下的所有题目ID
        List<Long> questionList = questionService.getQuestionIds(t.getId(), null, false);


        return questionList;
    }


    /**
     * 通过课程ID获取课程详情
     *
     * @param id id
     * @return
     */
    public Course getCourseById(Long id) {

        Course t = getById(id);
        if (t == null || StringUtils.isNull(t.getId())) {
            throw new CustomException("未找到课程信息");
        }

        //题目数量
        t.setQuestionCount(questionService.getQuestionIds(t.getId(), null, false).size());

        //通过课程ID获取字典的Value值
        List<CourseTag> tagValue = courseTagService.getBaseMapper().selectListByCourseId(id);

        //通过课程Id找到所有课件
        List<CoursewareVo> coursewareList = courseCoursewareService.getCoursewareList(t.getId());

        //总时长
        BigDecimal hour = new BigDecimal("0");
        //计算总时长
        for (CoursewareVo courseware : coursewareList) {
            hour = hour.add(courseware.getLearnHours());
        }

        //设置课件总时长
        t.setHour(String.format("%.1f", hour));
        t.setCourseTags(tagValue);
        return t;
    }

    /**
     * 分类查询
     */
    public List<Course> getCourseListByClass(Integer classType, String name, List<Integer> tags, Integer type) {
        return baseMapper.getCourseListByClass(classType, name, tags, type);
    }


    /**
     * 通过课程ID和课件ID 获取必选题列表的题目
     *
     * @param courseId 课程ID
     * @return 选题列表的题目列表
     */
    public List<CourseQuestion> findQuestionList(Long courseId) {
        //获取到课程下的所有题目
        List<CourseQuestion> questionList = questionService.findQuestionList(courseId);
        return questionList;
    }

    /**
     * 安培课程课件同步
     *
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void sync() {
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            int pageNum = 1;
            List<CompanyCourseVo> courseList;
            do {
                log.info("页码{}", pageNum);
                courseList = CourseApi.build(AliVodController.AppId).courseList(pageNum, 50, (String) null);
                for (CompanyCourseVo vo : courseList) {
                    Course c = new Course();
                    c.setCourseName(vo.getCourseName());
                    c.setType(3);
                    c.setCourseType(25);//类别字典值(三方课程)
                    c.setLearningContent(vo.getCourseDescs());
                    c.setCoverPath(vo.getFrontImage());
                    c.setHour(vo.getLearnTime().toString());
                    c.setThirdPartyId(vo.getCourseId());
                    saveOrUpdateThirdParty(c);
                    for (CompanyCourseSectionVo sec : vo.getSecList()) {
                        for (CompanyCoursewareVo wareVo : sec.getWareList()) {
                            Courseware courseware = buildCourseware(wareVo);
                            courseCoursewareService.saveOrUpdateThirdParty(courseware, c.getId());
                        }
                    }
                }
                pageNum++;
                if (pageNum % 10 == 0) {
                    platformTransactionManager.commit(transactionStatus);
                    transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
                }
            } while (pageNum < 20);
            log.info("已运行到{}页，结束。", pageNum);
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            log.error("同步安培在线课程失败：", e);
            platformTransactionManager.rollback(transactionStatus);
        }
    }

    public Courseware buildCourseware(CompanyCoursewareVo vo) {
        Courseware c = new Courseware();
        c.setName(vo.getWareName());
        c.setThirdPartyId(vo.getWareId());
        c.setDuration(vo.getDuration());
        c.setCoverPath(vo.getImgUrl());
        c.setM3u8(vo.getFilePath());
        c.setType(1);
        c.setFileId(vo.getVid());
        c.setTranscode(Boolean.TRUE);
        return c;
    }

    Course selectByThirdPartyId(Long thirdPartyId) {
        return baseMapper.selectByThirdPartyId(thirdPartyId);
    }

    /**
     * 保存或更新三方课程
     *
     * @param c
     * @return
     */
    public Course saveOrUpdateThirdParty(Course c) {
        Course course = selectByThirdPartyId(c.getThirdPartyId());
        if (ObjUtil.isNull(course)) {
            course = c;
            course.setCourseCode(genCode());
            course.setIsDelete(YesOrNoState.NO.getState());
            course.setCreateTime(new Date());
            course.setCreateBy(SecurityUtil.getUserId());
            save(course);
        } else {
            @SuppressWarnings("unchecked")
            CopyOptions options = CopyOptions.create().setIgnoreNullValue(true).setIgnoreProperties(Course::getCourseType);
            BeanUtil.copyProperties(c, course, options);
            course.setUpdateTime(new Date());
            course.setUpdateBy(SecurityUtil.getUserId());
            updateById(course);
        }
        return course;
    }

    /**
     * 题库导出word模板
     */
    public static final String QUESTION_TEMPLATE = "/template/questionTemplate.docx";

    /**
     * 创建题库word文档
     */
    public String buildQuestionWordFile(Course reqVo) throws IOException {
        reqVo = new Course();
        List<SysDataDictionaryItem> dictItem = dictionaryItemService.findList("0006");
        List<CourseTypeWordVo> courseTypeResult = Lists.newArrayList();
        for (SysDataDictionaryItem dict : dictItem) {
            reqVo.setCourseType(dict.getValue());
            List<Course> courseList = baseMapper.getCourseList(reqVo);
            List<CourseWordVo> courseResult = Lists.newArrayList();
            int i = 1;
            for (Course c : courseList) {
                List<CourseQuestion> questionList = questionService.getQuestionId(c.getId());
                CourseWordVo course = new CourseWordVo().setId(i).setCourseType(Dictionary.getDictionaryItem("0006", c.getCourseType().toString()).getName()).setCourseName(c.getCourseName());
                for (CourseQuestion q : questionList) {
                    course.getQuestionList().add(new QuestionWordVo()
                            .setId(course.getId())
                            .setQuestionName(q.getQuestionName())
                            .setQuestionType(q.getQuestionType().getDesc())
                            .setAnswerA(q.getAnswerA())
                            .setAnswerB(q.getAnswerB())
                            .setAnswerC(q.getAnswerC())
                            .setAnswerD(q.getAnswerD())
                            .setAnswerE(q.getAnswerE())
                            .setAnswerF(q.getAnswerF())
                            .setRightAnswers(q.getRightAnswersDesc())
                            .setAnswerParse(StrUtil.isEmpty(q.getAnswerParse()) ? "略" : q.getAnswerParse()));
                }
                courseResult.add(course);
            }
            courseTypeResult.add(new CourseTypeWordVo().setCourseType(dict.getName()).setCourseList(courseResult));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("courseTypeList", courseTypeResult);
        Configure config = Configure.builder()
                .useSpringEL()
                .build();

        File templateFile = ossService.download(QUESTION_TEMPLATE);
        XWPFTemplate template = XWPFTemplate.compile(templateFile, config);
        template.render(data);
        template.writeToFile("D:\\project\\questionTemplate-sample.docx");
        return "xxxxx";
    }
}
