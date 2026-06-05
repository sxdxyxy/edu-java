package com.joyfishs.dawa.student.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.student.domain.CourseDetail;
import com.joyfishs.dawa.student.domain.MyCourseList;
import com.joyfishs.dawa.student.domain.vo.TrainPlanVo;

@Mapper
public interface StudentCourseListMapper extends BaseMapper<MyCourseList> {
    /**
     * 学生端首页-我的课程，课程列表
     * @param studentId
     * @param name
     * @param projectType
     * @param trainType
     * @param status
     * @param startDate
     * @return
     */
    List<MyCourseList> getMyCourseList(@Param("studentId") Long studentId,
                                       @Param("name") String name,
                                       @Param("projectType") Integer projectType,
                                       @Param("trainType") Integer trainType,
                                       @Param("status") Integer status,
                                       @Param("startDate") Date startDate);

    /**
     * 查询(项目)课程详情
     * @param id
     * @return
     */
    CourseDetail getCourseDetail(@Param("id") Long id);

    /**
     * 查询培训计划项目列表
     */
    List<MyCourseList> getTrainPlanList(@Param("trainPlanId") Long trainPlanId, @Param("personId") Long personId, @Param("projectType") Integer projectType);

    /**
     * 查询学习课程数
     */
    Integer getTotalCourseCount(@Param("personId") Long personId, @Param("type") Integer type);

    /**
     * 查询总学时
     */
    BigDecimal getTotalLearnHours(@Param("personId") Long personId, @Param("trainType") Integer trainType, @Param("year") Integer year);

    /**
     * 查询培训计划列表
     *
     * @param personId
     * @param type
     * @return
     */
    List<TrainPlanVo> queryTrainPlanList(@Param("personId") Long personId, @Param("type") Integer type);
}
