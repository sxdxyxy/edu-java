package com.joyfishs.dawa.syscourse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.syscourse.domain.SysRecommendCourse;
import com.joyfishs.dawa.syscourse.service.SysCourseService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiaodai
 * @create 2022/1/5 10:15
 */

@Slf4j
@RestController
@RequestMapping("/sys/recommend")
public class SysCourseController extends BaseController {
    @Autowired
    private SysCourseService sysCourseService;

    @GetMapping("/detail")
    @Log(title = "系统课程推荐详情", businessType = BusinessType.SELECT)
    public AjaxResult<?> sysRecommendCourseDetail(@RequestParam(required = true) Long projectId) {
        SysRecommendCourse project = sysCourseService.findSysRecommend(projectId);
        return AjaxResult.success(project);
    }


    @GetMapping("/listPage")
    @Log(title = "系统课程推荐列表", businessType = BusinessType.SELECT)
    public TableDataInfo<?> sysRecommendCourseList() {
        startPage();
        List<SysRecommendCourse> projectList = sysCourseService.findSysRecommendList();
        return getDataTable(projectList);
    }


}
