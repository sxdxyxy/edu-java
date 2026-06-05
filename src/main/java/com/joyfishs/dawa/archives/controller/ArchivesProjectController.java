package com.joyfishs.dawa.archives.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.archives.domain.*;
import com.joyfishs.dawa.archives.service.ArchivesProjectService;
import com.joyfishs.dawa.archives.service.ArchivesUserService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目档案
 */

@Slf4j
@RestController
@RequestMapping("/archives/project")
public class ArchivesProjectController extends BaseController {

    @Autowired
    private ArchivesProjectService archivesProjectService;
    @Autowired
    private ArchivesUserService archivesUserService;

    @GetMapping("/listPage")
    @PreAuthorize("@ss.hasPermi('archives:project:list')")
    public TableDataInfo<?> listPage(
            @RequestParam(value = "trainClass", required = false, defaultValue = "") Integer trainClass,
            @RequestParam(value = "trainWay", required = false, defaultValue = "") Integer trainWay,
            @RequestParam(value = "projectName", required = false, defaultValue = "") String projectName,
            @RequestParam(value = "projectId", required = false, defaultValue = "") Long projectId){
        startPage();
        List<ArchivesProject> list = archivesProjectService.listPage(trainClass, trainWay, projectName, projectId);
        return getDataTable(list);
    }

    @GetMapping("/user/listPage")
    @PreAuthorize("@ss.hasPermi('archives:project:user:list')")
    public TableDataInfo<?> userListPage(
            @RequestParam(value = "id", required = false, defaultValue = "") Long id,
            @RequestParam(value = "state", required = false, defaultValue = "") Integer state,
            @RequestParam(value = "userName", required = false, defaultValue = "") String userName){
        startPage();
        List<ArchivesProjectUser> list = archivesProjectService.userListPage(id, state, userName);
        return getDataTable(list);
    }

    @GetMapping("/user/course/listPage")
    @PreAuthorize("@ss.hasPermi('archives:project:user:course:list')")
    public TableDataInfo<?> userCourseListPage(
            @RequestParam(value = "projectId", required = false, defaultValue = "") Long projectId,
            @RequestParam(value = "userId", required = false, defaultValue = "") Long userId,
            @RequestParam(value = "courseName", required = false, defaultValue = "") String courseName){
        startPage();
        List<ArchivesUserAutoTrainDetail> list = archivesUserService.autoTrainDetailPageList(userId, projectId, courseName);
        return getDataTable(list);
    }

    @GetMapping("/user/answer/listPage")
    @PreAuthorize("@ss.hasPermi('archives:project:user:answer:list')")
    public TableDataInfo<?> userAnswerListPage(
            @RequestParam(value = "projectId", required = false, defaultValue = "") Long projectId,
            @RequestParam(value = "userId", required = false, defaultValue = "") Long userId){
        startPage();
        List<ArchivesUserAutoTrainAnswer> list = archivesUserService.autoTrainAnswerPageList(userId, projectId);
        return getDataTable(list);
    }
}
