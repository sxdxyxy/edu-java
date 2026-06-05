package com.joyfishs.dawa.person.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.dawa.person.entity.PersonChangeLog;
import com.joyfishs.dawa.person.service.PersonChangeLogService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangkaifeng
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/person/changeLog")
public class PersonChangeLogController extends BaseController {

	private final PersonChangeLogService personChangeLogService;

	// 人员列表
	@GetMapping("/pageList")
	@Log(title = "查询人员变动信息列表", businessType = BusinessType.SELECT)
	public TableDataInfo<?> pageList(@RequestParam Long userId, @RequestParam Long orgId) {
		startPage();
		List<PersonChangeLog> list = personChangeLogService.listByUserIdAndOrgId(userId, orgId);
		return getDataTable(list);
	}

}
