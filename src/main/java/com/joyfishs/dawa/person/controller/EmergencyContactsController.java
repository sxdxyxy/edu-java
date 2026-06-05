package com.joyfishs.dawa.person.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.joyfishs.dawa.person.entity.EmergencyContacts;
import com.joyfishs.dawa.person.service.EmergencyContactsService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;

import cn.hutool.core.util.ObjUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/person/contacts")
public class EmergencyContactsController extends BaseController {

    @Autowired
    private EmergencyContactsService contactsService;

    // 查询紧急联系人
    @GetMapping("/list")
    public AjaxResult<?> listByOrgId(@RequestParam(required = false) Long personId) {
        if (ObjUtil.isNull(personId)) {
            personId = SecurityUtil.getPersonId();
        }
        return AjaxResult.success(contactsService.listByPersonId(personId));
    }

    // 新增紧急联系人
    @PostMapping("/add")
    public AjaxResult<?> addPersonOrg(@RequestBody EmergencyContacts param) {
        return AjaxResult.success(contactsService.addContact(param));
    }

    // 删除紧急联系人
    @PostMapping("/remove")
    public AjaxResult<?> removePersonOrg(@RequestParam Long contactId) {
        return AjaxResult.success(contactsService.removeContact(contactId));
    }
}
