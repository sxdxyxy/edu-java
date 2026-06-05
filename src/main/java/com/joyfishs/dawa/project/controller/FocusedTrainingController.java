package com.joyfishs.dawa.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.service.FocusedTrainingService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 */

@Slf4j
@RestController
@RequestMapping("/focused/training")
@Api(tags = "终端培训")
public class FocusedTrainingController extends BaseController {

    @Autowired
    private FocusedTrainingService focusedTrainingService;

    @GetMapping("/list")
    @ApiOperation(value = "集中培训列表")
    public TableDataInfo<?> findByPerson() {
        startPage();
        List<ProjectTerminalTrain>  projectTerminalTrains = focusedTrainingService.findByPerson();
        return getDataTable(projectTerminalTrains);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "培训详情")
    public AjaxResult<?> findTrainDetail(@RequestParam(required = true) Long projectId) {
        return AjaxResult.success(focusedTrainingService.findTrainDetail(projectId));
    }


}
