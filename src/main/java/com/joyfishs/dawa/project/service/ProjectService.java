package com.joyfishs.dawa.project.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.course.service.CourseService;
import com.joyfishs.dawa.message.entity.Message;
import com.joyfishs.dawa.message.service.MessageService;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.plan.entity.TrainPlan;
import com.joyfishs.dawa.plan.service.TrainPlanService;
import com.joyfishs.dawa.project.domain.vo.ProjectCourseList;
import com.joyfishs.dawa.project.domain.vo.ProjectList;
import com.joyfishs.dawa.project.domain.vo.ProjectPersonList;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.entity.ProjectDict;
import com.joyfishs.dawa.project.entity.ProjectRelate;
import com.joyfishs.dawa.project.entity.ProjectTestPaperTactics;
import com.joyfishs.dawa.project.mapper.ProjectMapper;
import com.joyfishs.dawa.student.entity.PersonCourseRelate;
import com.joyfishs.dawa.student.service.PersonCourseRelateService;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目管理服务
 *
 * @author ykfnb
 */
@Slf4j
@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> {

    @Autowired
    ProjectRelateService projectRelateService;

    @Autowired
    ProjectTestPaperTacticsService testPaperTacticsService;

    @Autowired
    CourseService courseService;

    @Autowired
    ProjectDictService dictService;

    @Autowired
    SysOrgService sysOrgService;

    @Autowired
    PersonCourseRelateService personCourseRelateService;

    @Autowired
    MessageService messageService;

    @Autowired
    TrainPlanService trainPlanService;


    /**
     * 项目管理保存 or 更新
     *
     * @param project
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateProject(Project project) {
        Long id = project.getId();

        //单位ID列表
//		List<String> unitIdList = project.getUnitIds();
//		//拼接成字符串便于保存
//		String unitIds = null;
//		if (unitIdList != null && unitIdList.size() > 0) {
//			unitIds = unitIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
//		}

        //人员ID列表
        List<String> personIdList = project.getPersonIds();
        //拼接成字符串便于保存
        String personIds = null;
        // todo 解除单培训不考试，不保存人的限制
        if (personIdList != null && personIdList.size() > 0) {
            personIds = personIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
        }

        //课程ID列表
        List<ProjectRelate> lessonIds = project.getLessonIds();

        project.setCreateBy(SecurityUtil.getUserId());
        project.setCreateTime(new Date());
        project.setIsDelete(YesOrNoState.NO.getState());
        //如果没有就是没发布
        if (project.getProjectStatus() == null) {
            project.setProjectStatus(1);
        }
        // 自动设置org_id：如果项目没有设置org_id，则从培训计划或当前用户获取
        if (project.getOrgId() == null) {
            if (project.getTrainPlanId() != null) {
                // 如果有关联培训计划，从计划获取org_id
                TrainPlan trainPlan = trainPlanService.getById(project.getTrainPlanId());
                if (trainPlan != null) {
                    project.setOrgId(trainPlan.getOrgId());
                }
            } else {
                // 否则从当前用户获取org_id
                project.setOrgId(SecurityUtil.getOrgIdOrNull());
            }
        }
        //保存项目主体
        this.saveOrUpdate(project);
        //项目id
        Long projectId = project.getId();

        if (id != null) {
            projectRelateService.removeByProjectId(id);
            testPaperTacticsService.removeByProjectId(id);
            personCourseRelateService.removeByProjectId(id);
        }

        //保存人员关联表，前台课程学习报到用
        if (personIdList != null && personIdList.size() > 0) {
            List<PersonCourseRelate> personCourseRelateList = new ArrayList<>(personIdList.size());
            for (String s : personIdList) {
                PersonCourseRelate pcr = new PersonCourseRelate();
                pcr.setProjectId(projectId);
                pcr.setPersonId(Long.parseLong(s));
                //未签到
                pcr.setIsSign(1);
                pcr.setCreateBy(SecurityUtil.getUserId());
                pcr.setCreateTime(new Date());
                pcr.setIsDelete(YesOrNoState.NO.getState());

                personCourseRelateList.add(pcr);

                // 是否生成准考证号
                if (Integer.valueOf(2).equals(project.getTrainWay()) || Integer.valueOf(4).equals(project.getTrainWay())) {
                    pcr.setCandidateNo(this.getCandidateNo());
                }
            }
            personCourseRelateService.saveBatch(personCourseRelateList);
        }


        //项目关联数据一起保存
        List<ProjectRelate> list = new ArrayList<>();
        /** 类型 1-受训单位 2-课程列表  3-人员 */
//		if (unitIds != null) {
//			XmProjectRelate unit = new XmProjectRelate(null, 1, projectId, unitIds, null);
//			unit.setCreateBy(SecurityUtil.getUserId());
//			unit.setCreateTime(DateUtil.date());
//			list.add(unit);
//		}

        if (personIds != null) {
            ProjectRelate person = new ProjectRelate(projectId, personIds);
            person.setCreateBy(SecurityUtil.getUserId());
            person.setCreateTime(DateUtil.date());
            list.add(person);
        }


        if (lessonIds != null) {
            List<ProjectRelate> collect = lessonIds.stream().map(r -> {
                r.setType(2);
                r.setId(null);
                r.setProjectId(projectId);
                r.setRelateIds(r.getIds());
                r.setCreateBy(SecurityUtil.getUserId());
                r.setCreateTime(new Date());
                return r;
            }).collect(Collectors.toList());
            list.addAll(collect);
        }
        projectRelateService.saveBatch(list);  //保存关联数据


        //单培训  没有考试
        // todo 解除单培训不考试，不保存组卷策略的限制
//		if(!"1".equals(project.getTrainWay())){
        //组卷详情列表
        List<ProjectTestPaperTactics> testPaperTactics = project.getTestPaperTactics();
        if (testPaperTactics != null) {
            //组卷详情明细数据
            List<ProjectTestPaperTactics> testPaperTacticsList = testPaperTactics.stream()
                    .filter(t1 -> t1.getTopicNum() != null || t1.getTopicScore() != null)
                    .peek(t -> {
                        t.setId(null);
                        t.setProjectId(projectId);
                        t.setCreateBy(SecurityUtil.getUserId());
                        t.setCreateTime(new Date());
                        if (t.getTopicNum() == null) t.setTopicNum(0);
                        if (t.getTopicScore() == null) t.setTopicScore(0);
                    }).collect(Collectors.toList());
            testPaperTacticsService.saveBatch(testPaperTacticsList);
        }

        // 2022-02-26 新增发送消息通知，如果
        if (project.getProjectStatus() != null && project.getProjectStatus() == 2) {
            // 根据项目id获取人员id
            List<Long> personIdArr = personCourseRelateService.getBaseMapper().listPersonIdByProjectId(projectId);
            this.sendMessage(projectId, project.getProjectName(), personIdArr);
        }
        return true;
    }


    private String getCandidateNo() {
        String str = null;

        Random random = new Random();
        //随机生成数字，并添加到字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        str = String.valueOf(random.nextInt(8)) + (sdf.format(new Date()));

        return str;
    }

    /**
     * 项目删除
     *
     * @param idList
     * @param deleteReason
     * @return
     */
    public boolean del(List<Long> idList, String deleteReason) {
        Long userId = SecurityUtil.getUserId();
        baseMapper.remove(idList, userId, deleteReason);
        return true;
    }

    /**
     * 查询项目列表
     *
     * @return
     */
    public List<ProjectList> findList(String projectName,
                                      Integer status,
                                      String startDate,
                                      String endDate,
                                      Integer trainWay,
                                      String type,
                                      Long orgId) {
        //系统推荐Type= 0
        //项目类型 0-系统项目  1-自主项目  2-年度培训计划  3-单位培训计划
        Integer t = 0;
        if ("self".equals(type)) t = 1;  //自主项目
        else if ("year".equals(type)) t = 2;  //年度培训计划
        else if ("unit".equals(type)) t = 3;  //单位培训计划
        Long userId = SecurityUtil.getUserId();
        List<Long> orgIds = SecurityUtil.getManagedOrgIds();
        return baseMapper.queryList(projectName, status, startDate, endDate, trainWay, t, userId, orgIds, orgId);
    }


    /**
     * 查询项目详情
     *
     * @param id
     * @return
     */
    public Project get(Long id) {

        //主项目数据
        Project project = baseMapper.selectById(id);

        if (project == null) {
            throw new CustomException("项目不存在或已删除");
        }

        //添加培训种类
        ProjectDict trainClass = dictService.getById(project.getTrainClass());
        if (trainClass != null) project.setTrainClassName(trainClass.getName());

        List<ProjectRelate> list = projectRelateService.getByProjectId(id);

        /** 类型 1-受训单位 2-课程列表  3-人员 */
        for (ProjectRelate projectRelate : list) {
            if (projectRelate.getType() == 1) {  //受训单位列表
                String[] unitIdArray = projectRelate.getRelateIds().split(",");
                ArrayList<String> unitIds = new ArrayList<>(Arrays.asList(unitIdArray));
                project.setUnitIds(unitIds);
            }

            if (projectRelate.getType() == 3) {  //3-人员
                String ids = projectRelate.getRelateIds();
                project.setPersonIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
                List<ProjectPersonList> personList = null;
                if (ids != null && ids.length() > 0) {
                    List<String> idList = Arrays.asList(ids.split(","));
                    personList = baseMapper.getPersonList(idList);
                    for (ProjectPersonList person : personList) {
                        SysOrg sysOrg = sysOrgService.getById(person.getOrgId());
                        if (sysOrg.getType() == 1) {
                            // 如果组织为单位，则直接返回单位名称
                            person.setCompany(sysOrg.getName());
                        } else {
                            // 先查询出部门信息，再查询所属单位
                            SysOrg parentOrg = sysOrgService.findUnitByOrgId(sysOrg.getPid());
                            person.setCompany(parentOrg.getName());
                            person.setDepartment(sysOrg.getName());
                        }
                    }
                }
                if (personList == null) personList = new ArrayList<>();
                project.setProjectPersonLists(personList);
            }
        }

        //课程列表
        List<ProjectRelate> courseList = projectRelateService.getCourseByProjectId(id);
        project.setLessonIds(courseList);
        List<String> courseIdList = courseList.stream().map(ProjectRelate::getRelateIds).collect(Collectors.toList());
        List<ProjectCourseList> projectCourseLists = courseService.getListByIds(courseIdList, null);
        project.setCourseLists(projectCourseLists);

        List<ProjectTestPaperTactics> testPaperTacticsList = testPaperTacticsService.getByProjectId(id);
        project.setTestPaperTactics(testPaperTacticsList);

        return project;
    }

    /**
     * 项目发布
     */
    public Boolean release(Long id) {
        //主项目数据
        Project project = baseMapper.selectById(id);
        if (project == null) {
            throw new CustomException("项目不存在或已删除");
        }

        Integer i = baseMapper.release(id);
        if (i > 0) {
            // 根据项目id获取人员id
            List<Long> personIds = personCourseRelateService.getBaseMapper().listPersonIdByProjectId(id);
            this.sendMessage(id, project.getProjectName(), personIds);
            return true;
        }
        return false;
    }

    /**
     * 项目中止
     */
    public Boolean stop(Long id) {
        Project project = baseMapper.selectById(id);
        if (project == null) {
            throw new CustomException("项目不存在或已删除");
        }
        if (project.getProjectStatus() == null || project.getProjectStatus() != 3) {
            throw new CustomException("只能中止进行中的项目");
        }
        return baseMapper.stop(id) > 0;
    }

    private void sendMessage(Long projectId, String projectName, List<Long> personIds) {
        List<Message> messageList = new ArrayList<>();
        for (Long personId : personIds) {
            Message message = new Message();
            message.setPersonId(personId);
            message.setTitle("您接收到新的培训项目：" + projectName);
            message.setType(1);
            message.setDataId(projectId);

            // 设置默认参数
            message.setCreateBy(SecurityUtil.getUserId());
            message.setCreateTime(new Date());
            message.setIsDelete(YesOrNoState.NO.getState());
            message.setIsRead(0);

            messageList.add(message);
        }

        messageService.saveBatch(messageList);
    }


    /**
     * 获取移动端学习提醒内容
     *
     * @param personId
     * @return
     */
    public List<Project> getReminDList(Long personId) {
        List<Project> remindList = baseMapper.getReminDList(personId);
        return remindList;
    }

    /**
     * 删除计划关联的项目
     *
     * @param id
     */
    public void delByTrainPlanId(Long id) {
        baseMapper.delByTrainPlanId(id, SecurityUtil.getUserId());
    }

    /**
     * 刷新状态
     */
    public void refreshProjectStatus() {
        baseMapper.refreshProjectStatus();
    }

    public int countByTrainClass(Long trainClass) {
        return baseMapper.countByTrainClass(trainClass);
    }
}
