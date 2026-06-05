package com.joyfishs.dawa.exam.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.exam.entity.StudentSubscribeExam;
import com.joyfishs.dawa.exam.mapper.StudentSubscribeExamMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;
import com.joyfishs.dawa.student.entity.PersonCourseRelate;
import com.joyfishs.dawa.student.service.PersonCourseRelateService;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.SpringUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * 预约考试业务
 */

@Slf4j
@Service
public class StudentSubscribeExamService extends ServiceImpl<StudentSubscribeExamMapper, StudentSubscribeExam> {

    @Autowired
    private ExamService examService;
    @Autowired
    private StudentSubscribeExamService studentSubscribeExamService;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonCourseRelateService personCourseRelateService;



    /**
     * 考试预约新增
     * @param personId 人员ID
     * @param projectId 项目ID
     */

    public StudentSubscribeExam saveSubscribeExam(Long personId, Long projectId){
       if (StringUtils.isNull(personId)) {
           throw new CustomException("人员ID不能够为空！");
       }
        if (StringUtils.isNull(projectId)) {
            throw new CustomException("项目ID不能够为空！");
        }

        StudentSubscribeExam studentSubscribeExam = new StudentSubscribeExam();
        studentSubscribeExam.setPersonId(personId);
        studentSubscribeExam.setProjectId(projectId);
        studentSubscribeExam.setStatus(YesOrNoState.YES.getState());

        studentSubscribeExam.setCreateTime(new Date());
        studentSubscribeExam.setCreateBy(SecurityUtil.getUserId());
        studentSubscribeExam.setIsDelete(YesOrNoState.NO.getState());
        save(studentSubscribeExam);

        return studentSubscribeExam;
    }


    /**
     * 预约考试
     * @param projectId 项目ID
     * @return
     */
    public StudentSubscribeExam subscribeExam(Long projectId) {
        Person person = personService.getByUserId(SecurityUtil.getUserId());

        //通过项目ID去查询考试状态
        Project project = SpringUtil.getBean(ProjectService.class).getById(projectId);
       if (StringUtils.isNull(project.getId())) {
            throw new CustomException("项目未找到!");
        }

        //判断预约考试时间
        if (new Date().after(project.getExamEdate())) {
            throw new CustomException("考试已经结束不能在进行预约!");
        }



        //判断学员是否已经学时达标
        PersonCourseRelate personCourseRelate = personCourseRelateService.getRecord(projectId,person.getId());
        if (personCourseRelate == null || StringUtils.isNull(personCourseRelate.getId())) {
            throw new CustomException("学习记录未找到!");
        }
        if (personCourseRelate.getIsSign() != 3) {
            throw new CustomException("您还没有学习完成，请学习完成后再来预约考试!");
        }

        //通过学生ID和项目ID查询 考试人员状态
        StudentSubscribeExam studentSubscribeExam = examService.getBaseMapper().findPersonSubscriptExamStatus(person.getId(),projectId);

        //如果是空说明 没有预约过， 假设现在预约考试条件都已经足够了 预约考试
        if (studentSubscribeExam == null) {
            return studentSubscribeExamService. saveSubscribeExam(person.getId(), projectId);
        }

        return studentSubscribeExam;
    }


    /**
     * 验证考试是否可以预约  通过考试 开始时间、结束时间和 系统当前时间进行对比
     * @param beginTime 开始时间
     * @param endTime 结束时间
     */
    public boolean verifyExamStatus(Date beginTime,Date endTime,Integer isExam){
        // 是否考试 -----
        if (isExam == 1) {
            if (new Date().before(beginTime)) {
                throw new CustomException("还未到考试时间，请到时间后再来进行考试!");
            }
        }
        if (isExam == 1) {
            if (new Date().after(endTime)) {
                throw new CustomException("考试已经结束了!");
            }
        }
        return true;
    }

}
