package com.joyfishs.dawa.student.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyfishs.dawa.course.controller.AliVodController;
import com.joyfishs.dawa.course.service.CourseCoursewareService;
import com.joyfishs.dawa.course.service.CourseRelationCoursewareService;
import com.joyfishs.dawa.project.service.ProjectRelateService;
import com.joyfishs.dawa.student.domain.StudentCourseCatalogue;
import com.joyfishs.utils.StringUtils;
import com.yaoan.liveapi.CourseApi;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 我的课程-开始学习-目录
 */
@Slf4j
@Service
public class StudentCourseCatalogueService{

	@Autowired
    CourseCoursewareService courseCoursewareService;
	@Autowired
	ProjectRelateService projectRelateService;
	@Autowired
	private CourseRelationCoursewareService courseRelationCoursewareService;

	public List<StudentCourseCatalogue> getCourseTree(Long projectId, Long studentId) {
		//课程列表
		List<StudentCourseCatalogue> list = projectRelateService.getCourseList(projectId);
		String videoToken = "";
        try {
			videoToken = CourseApi.build(AliVodController.AppId).getToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(list != null){
			List<Map<String,Object>> maps = new ArrayList<>();
			for (StudentCourseCatalogue studentCourseCatalogue : list) {
				maps.addAll(courseRelationCoursewareService.getCoursewareList(studentCourseCatalogue.getId(), projectId, studentId));
			}
			for (StudentCourseCatalogue catalogue : list) {
				List<StudentCourseCatalogue> coursewareList = new ArrayList<>();
				for (Map<String, Object> map : maps) {
					Long courseId = (Long)map.get("courseId");
					if(catalogue.getId().equals(courseId)){
						Long id = (Long)map.get("id");
						String name = map.get("name") != null ? map.get("name").toString() : null;
						String coverPath = map.get("coverPath") != null ? map.get("coverPath").toString() : null;
						String videoPath = map.get("videoPath") != null ? map.get("videoPath").toString() : null;
						String fileId = map.get("fileId") != null ? map.get("fileId").toString() : null;
						String m3u8 = map.get("m3u8") != null ? map.get("m3u8").toString() : null;
						Long status = map.get("status") != null ? (Long) map.get("status") : null;
						String duration = map.get("duration") != null ? (String) map.get("duration") : "0";
						Integer courseWay = map.get("courseWay") != null ? (Integer) map.get("courseWay") : 1;
						Boolean thirdParty = map.get("thirdPartyId") != null ? Boolean.TRUE : Boolean.FALSE;
						if (Boolean.TRUE.equals(thirdParty)) {
							m3u8 = m3u8 + "?MtsHlsUriToken=" + videoToken;
						}
						//课件
						StudentCourseCatalogue courseware = new StudentCourseCatalogue();
						courseware.setId(id)
								.setName(name)
								.setCourseWay(courseWay)
								.setType(2)
								.setDuration(duration)  //时长，分钟
								.setCoverPath(coverPath)
								.setVideoAddress(StringUtils.isNotEmpty(m3u8) ? m3u8 : videoPath)
								.setFileId(fileId)
								.setThirdParty(thirdParty)
								.setStatus(status != null ? Math.toIntExact(status) : 0);
						coursewareList.add(courseware);
					}
				}
				catalogue.setCourseList(coursewareList);
			}
		}else {
			list = new ArrayList<>();
		}
		return list;
	}
}
