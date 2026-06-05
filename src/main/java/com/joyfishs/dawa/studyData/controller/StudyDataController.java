package com.joyfishs.dawa.studyData.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.studyData.domain.StudyData;
import com.joyfishs.dawa.studyData.service.StudyDataService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/study/data")
public class StudyDataController extends BaseController {

    @Autowired
    private StudyDataService studyDataService;

    @GetMapping("/home")
    @PreAuthorize("@ss.hasPermi('study:data:home')")
    @Log(title = "学习统计首页", businessType = BusinessType.SELECT)
    public AjaxResult<?> home() {
        StudyData studyData = studyDataService.home();
        return AjaxResult.success(studyData);
    }

}
