package com.joyfishs.dawa.course.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.course.entity.CourseRelationCourseware;
import com.joyfishs.dawa.course.mapper.CourseRelationCoursewareMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
@Service
public class CourseRelationCoursewareService extends ServiceImpl<CourseRelationCoursewareMapper, CourseRelationCourseware>  {

    /**
     * 新增课程和课件的关联关系
     * @param id 课件ID
     * @param courseId 课程ID
     */
    public void saveRelation(Long id,Long courseId) {
        CourseRelationCourseware relation = new CourseRelationCourseware();
        relation.setCoursewareId(id);
        relation.setCourseId(courseId);
        relation.setCreateBy(SecurityUtil.getUserId());
        relation.setCreateTime(new Date());
        relation.setIsDelete(YesOrNoState.NO.getState());
        save(relation);
    }

    /**
     * 通过课件ID 删除关联关系
     * @param id
     */
    public void deleteById(Long id) {
        baseMapper.deleteByCoursewareId(id);
    }

    /**
     *通过课程ID获取课件ID
     * @param id 课程ID
     * @return 课件关联关系表
     */
    public List<Long> getCoursewareRelationByCourseId(Long id) {
        return baseMapper.getCoursewareRelationByCourseId(id);
    }

    /** 课程下课件列表 */
    public List<Map<String, Object>> getCoursewareList(Long courseId, Long projectId, Long personId) {
        return baseMapper.getCoursewareList(courseId, projectId, personId);
    }

    /**
     * 课程总课时
     * @param courseId
     * @return
     */
    public BigDecimal getTotalHoursForCourse( Long courseId){
        return baseMapper.getTotalHoursForCourse(courseId);
    }

    public long getTotalDuration (Long courseId){
        return baseMapper.getTotalDuration(courseId);
    }

    /**
     * 项目总课时
     * @param projectId
     * @return
     */
    public BigDecimal getTotalHours( Long projectId){
        return baseMapper.getTotalHours(projectId);
    }
}
