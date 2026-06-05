package com.joyfishs.dawa.person.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.person.entity.PersonCertificate;
import com.joyfishs.dawa.person.service.PersonCertificateService;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.poi.ExcelUtil;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ykfnb
 */
@Slf4j
@RestController
@RequestMapping("/person/certificate")
public class PersonCertificateController extends BaseController {

	@Autowired
	PersonCertificateService personCertificateService;
	@Autowired
	PersonService personService;

	// 新增证件
	@PostMapping("/add")
	@PreAuthorize("@ss.hasPermi('person:certificate:edit')")
	@Log(title = "证件新增", businessType = BusinessType.INSERT)
	public AjaxResult<?> add(@Validated @RequestBody PersonCertificate personCertificate) {
		log.info("XmPersonCertificateController - add xmPersonCertificate:{}", personCertificate);
		return AjaxResult.success(personCertificateService.executeOrUpdate(personCertificate));
	}

	// 删除证件
	@PostMapping("/del")
	@PreAuthorize("@ss.hasPermi('person:certificate:edit')")
	@Log(title = "删除证书", businessType = BusinessType.DELETE)
	public AjaxResult<?> del(@RequestParam Long id) {
		log.info("XmPersonCertificateController - del id:{}", id);
		if (ObjectUtil.isEmpty(id)) return AjaxResult.error();
		return toAjax(personCertificateService.del(id));
	}

	// 获取证书详情
	@GetMapping("/detail")
	@PreAuthorize("@ss.hasAnyPermi('person:certificate:edit')")
	@Log(title = "获取证书详情", businessType = BusinessType.SELECT)
	public AjaxResult<?> detail(@RequestParam Long id) {
		log.info("XmPersonCertificateController - detail id:{}", id);
		if (ObjectUtil.isEmpty(id)) return AjaxResult.error();
		return AjaxResult.success(personCertificateService.getById(id));
	}

	@GetMapping("/export")
	@PreAuthorize("@ss.hasAnyPermi('person:certificate:export')")
	@Log(title = "导出证书", businessType = BusinessType.EXPORT)
	public AjaxResult<?> export(@RequestParam Long id) {
		log.info("XmPersonCertificateController - export id:{}", id);
		if (ObjectUtil.isEmpty(id)) return AjaxResult.error();
		PersonCertificate certificate = personCertificateService.getById(id);

		// 加载人员姓名
		certificate.setPersonName(personService.getById(certificate.getPersonId()).getName());
		// 加载图片路径
		certificate.setFileUrl("");

		ArrayList<PersonCertificate> certificates = new ArrayList<>();
		certificates.add(certificate);

		ExcelUtil<PersonCertificate> excelUtil = new ExcelUtil<>(PersonCertificate.class);
		return excelUtil.exportExcel(certificates, "证书详情");
	}


}
