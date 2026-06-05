package com.joyfishs.dawa.project.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.project.domain.vo.ProjectTerminalTrainSignVo;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrainSign;
import com.joyfishs.dawa.project.service.ProjectTerminalTrainService;
import com.joyfishs.dawa.project.service.ProjectTerminalTrainSignService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */

@Slf4j
@RestController
@Api(tags = "终端培训签到")
@RequestMapping("/project/terminalTrain")
public class ProjectTerminalTrainSignController extends BaseController {

	@Autowired
    ProjectTerminalTrainService terminalTrainService;

	@Autowired
	ProjectTerminalTrainSignService signService;

	@Autowired
	PersonService personService;

	@PostMapping("/enroll")
	@ApiOperation(value = "终端培训报名")
	public AjaxResult<?> enroll(@RequestBody ProjectTerminalTrainSign sign) {
		ProjectTerminalTrain train = terminalTrainService.getById(sign.getId());
		Long personId = sign.getPersonId();
		if(personId == null){
			personId = SecurityUtil.getPersonId();
			if(personId == null) {
				throw new CustomException("人员信息有误");
			}
			sign.setPersonId(personId);
		}
		//置换参数位置
		sign.setTrainId(sign.getId()).setId(null);
		ProjectTerminalTrainSign sign1 = signService.getSign(sign.getTrainId(), personId);
		if(sign1 != null && sign1.getId() != null) {
			return AjaxResult.error("已在当前项目中报名，请勿重复报名！");
		}
		Date trainEdate = train.getTrainEdate(); //培训结束时间
		Date now = DateUtil.date();  //当前时间

		//当前状态
		if(now.after(trainEdate)){
			throw new CustomException("当前培训时间已过，不能报名");
		}
		sign.setEnrollStatus(1); //设置报名状态未已报名
		sign.setEnrollTime(now);
		sign.setSignStatus(3); //设置签到状态未签到

		sign.setCreateBy(SecurityUtil.getUserId());
		sign.setCreateTime(now);
		return toAjax(signService.save(sign));
	}

	@GetMapping("/sign")
	@ApiOperation(value = "终端培训签到")
	public AjaxResult<?> sign(@RequestParam Long personId, @RequestParam Long trainId) {
		log.info("XmProjectTerminalTrainSignController - personId:{},trainId:{}",personId,trainId);
		ProjectTerminalTrainSign sign = signService.getSign(trainId,personId);
		if(sign == null || sign.getId() == null) {
			return AjaxResult.error("当前项目没有报名，请报名后再签到！");
		}
		ProjectTerminalTrain train = terminalTrainService.getById(sign.getTrainId());
		Date trainEdate = train.getTrainEdate(); //培训结束时间
		Date now = DateUtil.date();  //当前时间

		//当前状态
		Integer status = null;
		if(now.before(trainEdate)){
			status = 1;
		}else {
			throw new CustomException("当前培训时间已过，不能签到");
		}
		sign.setSignStatus(status);
		sign.setSignTime(now);

		sign.setCreateBy(SecurityUtil.getUserId());
		sign.setCreateTime(now);
		return toAjax(signService.updateById(sign));
	}

	@PostMapping("/cancel")
	@PreAuthorize("@ss.hasPermi('project:train:edit')")
	@ApiOperation(value = "取消签到")
	public AjaxResult<?> cancel(@RequestParam String id) {
		if (StringUtils.isEmpty(id)) {
			return AjaxResult.success();
		}
		return toAjax(signService.getBaseMapper().deleteById(id));
	}

	@GetMapping("/list")
	@PreAuthorize("@ss.hasPermi('terminal:train:list')")
	@ApiOperation(value = "查询报名过的人员列表，含签到情况")
	public AjaxResult<?> list(@RequestParam Long id){
		List<ProjectTerminalTrainSignVo> data = signService.getRegisteredList(id);
		return AjaxResult.success(data);
	}

	@GetMapping("/listEnroll")
	@PreAuthorize("@ss.hasPermi('terminal:train:list')")
	@ApiOperation(value = "查询参会人员列表")
	public AjaxResult<?> listEnroll(@RequestParam Long trainId){
		List<ProjectTerminalTrainSignVo> data = signService.getEnrollList(trainId);
		return AjaxResult.success(data);
	}

	@PostMapping("/signIn")
	@ApiOperation(value = "手动参会")
	public AjaxResult<?> signIn(@RequestParam Long trainId, @RequestParam Long personId)  {
		ProjectTerminalTrainSign sign1 = signService.getSign(trainId, personId);
		if(sign1 != null && sign1.getId() != null) {
			return AjaxResult.error("已在当前项目中存在！");
		}
		ProjectTerminalTrain train = terminalTrainService.getById(trainId);
		ProjectTerminalTrainSign sign = new ProjectTerminalTrainSign();
		sign.setTrainId(trainId);
		sign.setPersonId(personId);
		sign.setEnrollStatus(1); //设置报名状态已报名
		sign.setEnrollTime(train.getTrainSdate());
		sign.setSignTime(train.getTrainSdate());
		sign.setSignStatus(1); //设置签到状态
		sign.setCreateBy(SecurityUtil.getUserId());
		sign.setCreateTime(DateUtil.date());
		return AjaxResult.success(signService.save(sign));
	}
}
