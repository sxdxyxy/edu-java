package com.joyfishs.dawa.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.student.domain.vo.FocusTrainVo;
import com.joyfishs.dawa.student.service.FocusTrainService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @program: dawa-java
 * @description: 学员集中培训controller
 * @author: Yjhon
 * @create: 2022-02-16 16:43
 */
@Slf4j
@RestController
@RequestMapping("/focusTrain")
public class FocusTrainController extends BaseController {

	@Autowired
	FocusTrainService focusTrainService;

	@GetMapping("/getList")
	public TableDataInfo<?> getFocusTrainList(@RequestParam Long personId,
	                                       @RequestParam(required = false) Integer status,
	                                       @RequestParam(required = false) String name){
		startPage();
		List<FocusTrainVo> list = focusTrainService.getList(personId, status, name);
		return getDataTable(list);
	}

	@GetMapping("/getDetail")
	public AjaxResult<?> getFocusTrainDetail(@RequestParam Long personId, @RequestParam Long trainId){
		log.info("XmStudentCourseListContrller - getMyCourseList params( personId:{},trainId:{} )",personId,trainId);
		FocusTrainVo vo = focusTrainService.getDetail(personId, trainId);
		return AjaxResult.success(vo);
	}
}
