package com.joyfishs.dawa.syscourse.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.mapper.ProjectMapper;
import com.joyfishs.dawa.syscourse.domain.SysRecommendCourse;
import com.joyfishs.utils.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiaodai
 * @create 2022/1/5 11:30
 */

@Slf4j
@Service
public class SysCourseService extends ServiceImpl<ProjectMapper, Project> {

    /**
     * 系统推荐课程详情
     * @return 系统推荐课程
     */
    public SysRecommendCourse findSysRecommend(Long projectId) {
        List<SysRecommendCourse> course = baseMapper.findSysRecommendList(projectId,null, SecurityUtil.getPersonId());
        if (course.size() == 0) return new SysRecommendCourse();

        return course.get(0);
    }

    /**
     * 系统推荐课程列表
     * @return 系统推荐课程列表
     */
    public List<SysRecommendCourse> findSysRecommendList() {
        List<SysRecommendCourse> course = baseMapper.findSysRecommendList(null,2,SecurityUtil.getPersonId());
        for (SysRecommendCourse project : course) {
            project.setTotalCourseHours(this.hourCount(project.getId()));
        }
        return course;
    }

    /**
     *  获取项目下的总课时
     * @param projectId 项目ID
     * @return
     */
    public int hourCount(Long projectId){
        //获取课件下的总课时
        return baseMapper.getHourCount(projectId);
    }

}
