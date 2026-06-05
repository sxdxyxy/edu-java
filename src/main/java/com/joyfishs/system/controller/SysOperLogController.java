package com.joyfishs.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.entity.SysOperLog;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.system.service.SysOperLogService;
import com.joyfishs.utils.page.TableDataInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("sys/operLog")
public class SysOperLogController extends BaseController {

    @Autowired
    private SysOperLogService sysOssService;

    /**
     * 分页查询
     *
     * @return RestResponse
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sys:operLog:list')")
    @Log(title = "系统文件分页查询", businessType = BusinessType.SELECT)
    public TableDataInfo<?> getList(SysOperLog sysOperLog) {
        log.info("SysOperLogController - getList sysOperLog:{}", sysOperLog);

        startPage();
        List<SysOperLog> list = sysOssService.queryList(sysOperLog);

        return getDataTable(list);
    }

}
