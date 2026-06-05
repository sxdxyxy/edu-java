package com.joyfishs.dawa.student.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.student.domain.CourseDetail;
import com.joyfishs.dawa.student.domain.MyCourseList;
import com.joyfishs.dawa.student.domain.vo.TrainPlanVo;
import com.joyfishs.dawa.student.mapper.StudentCourseListMapper;

/**
 * @description: 我的课程service
 */
@Service
public class StudentCourseListService extends ServiceImpl<StudentCourseListMapper, MyCourseList> {
	/** 学生端   首页-我的课程，课程列表查询列表使用*/
	public List<MyCourseList> getMyCourseList(Long studentId, String name, Integer projectType, Integer trainType, Integer status, Date startDate){
		List<MyCourseList> myCourseList = baseMapper.getMyCourseList(studentId, name, projectType, trainType, status, startDate);
		return  myCourseList;
	}

	/** 学生端   我的课程-课程详情*/
	public CourseDetail getMyCourseDetail(Long id) {
		CourseDetail courseDetail = baseMapper.getCourseDetail(id);
		if(courseDetail == null) {
            courseDetail = new CourseDetail();
        }
		return  courseDetail;
	}

	/** 学生端 查询培训计划项目列表 */
	public List<MyCourseList> getTrainPlan(Long trainPlanId, Long personId, Integer projectType) {
		List<MyCourseList> myCourseList = baseMapper.getTrainPlanList(trainPlanId,personId, projectType);
		return  myCourseList;
	}

	/**
	 * 查询累计学习课程数
	 * @param personId 人员id
	 * @param type 1-累计课程数  2-学完的课程数 3-结业课程数
	 * @return
	 */
	public Integer getTotalCourseCount(Long personId,Integer type){
		return baseMapper.getTotalCourseCount(personId,type);
	}

	/**
	 *  查询学时数
	 * @param personId 人员id
	 * @param trainType 1-必修 2-选修 null-所有
	 * @return
	 */
	public BigDecimal getTotalLearnHours(Long personId,Integer trainType, int year){
		BigDecimal totalLearnHours = baseMapper.getTotalLearnHours(personId, trainType, year);
		if(totalLearnHours == null) {
            totalLearnHours = BigDecimal.ZERO;
        }
		return totalLearnHours;
	}

	/**
	 * 查询培训计划列表
	 * @param personId 人员
	 * @param type   2-年度培训计划  3-单位培训计划
	 * @return
	 */
	public List<TrainPlanVo> queryTrainPlanList(Long personId, Integer type) {
		return baseMapper.queryTrainPlanList(personId,type);
	}
}
