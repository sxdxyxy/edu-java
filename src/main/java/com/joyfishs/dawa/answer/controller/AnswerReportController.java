package com.joyfishs.dawa.answer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.answer.service.AnswerReportService;
import com.joyfishs.utils.AjaxResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "答题报告")
@RequestMapping("/answer")
public class AnswerReportController {

    @Autowired
    private AnswerReportService answerReportService;

    @ApiOperation(value = "查看报告")
    @GetMapping("/viewReport")
    @PreAuthorize("@ss.hasAnyPermi('answerReport:viewReport')")
    public AjaxResult<?> viewReport(@RequestParam Long reportId) {
        return AjaxResult.success(answerReportService.viewReport(reportId));
    }

    @ApiOperation(value = "查看解析")
    @GetMapping("/viewResolution")
    @PreAuthorize("@ss.hasAnyPermi('answerReport:viewResolution')")
    public AjaxResult<?> viewResolution(@RequestParam Long reportId, Integer type) {
        return AjaxResult.success(answerReportService.viewResolution(reportId, type));
    }
}
