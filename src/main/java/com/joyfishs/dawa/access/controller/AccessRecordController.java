package com.joyfishs.dawa.access.controller;

import com.github.pagehelper.PageInfo;
import com.joyfishs.dawa.access.entity.AccessRecord;
import com.joyfishs.dawa.access.service.AccessRecordService;
import com.joyfishs.dawa.access.dto.AccessRecordDTO;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 准入记录控制器
 * 
 * @author safe-edu
 * @since 2026-03-29
 */
@RestController
@RequestMapping("/access")
public class AccessRecordController extends BaseController {

    @Autowired
    private AccessRecordService accessRecordService;

    /**
     * 查询用户的准入记录列表
     */
    @GetMapping("/user/{userId}")
    public AjaxResult<?> listByUser(@PathVariable Long userId) {
        List<AccessRecord> list = accessRecordService.listByUserId(userId);
        return AjaxResult.success(list);
    }

    /**
     * 查询项目的准入记录列表
     */
    @GetMapping("/project/{projectId}")
    public AjaxResult<?> listByProject(@PathVariable Long projectId) {
        List<AccessRecord> list = accessRecordService.listByProjectId(projectId);
        return AjaxResult.success(list);
    }

    /**
     * 查询用户今日的准入记录
     */
    @GetMapping("/today/{userId}")
    public AjaxResult<?> listToday(@PathVariable Long userId) {
        List<AccessRecord> list = accessRecordService.listTodayByUserId(userId);
        return AjaxResult.success(list);
    }

    /**
     * 查询当前在场人员
     */
    @GetMapping("/onsite/{projectId}")
    public AjaxResult<?> listOnSite(@PathVariable Long projectId) {
        List<AccessRecord> list = accessRecordService.listCurrentOnSite(projectId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("count", list.size());
        return AjaxResult.success(result);
    }

    /**
     * 记录进场
     */
    @PostMapping("/entry")
    public AjaxResult<?> recordEntry(@RequestBody AccessRecord record) {
        try {
            AccessRecord result = accessRecordService.recordAccess(
                record.getUserId(), record.getProjectId(), record.getAccessType(),
                record.getSafetyCodeColor(), record.getGateId(), record.getCameraSnapshot(),
                record.getLocation(), record.getRemarks());
            return AjaxResult.success("进场记录成功", result);
        } catch (Exception e) {
            return AjaxResult.error("记录失败：" + e.getMessage());
        }
    }

    /**
     * 记录出场
     */
    @PostMapping("/exit")
    public AjaxResult<?> recordExit(@RequestBody Map<String, Object> data) {
        try {
            Long accessId = Long.valueOf(data.get("accessId").toString());
            AccessRecord record = accessRecordService.recordExit(accessId);
            return AjaxResult.success("出场记录成功", record);
        } catch (Exception e) {
            return AjaxResult.error("记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取在场统计
     */
    @GetMapping("/on-site-count")
    public AjaxResult<?> getOnSiteCount(
            @RequestParam Long projectId) {
        List<AccessRecord> onsite = accessRecordService.listCurrentOnSite(projectId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("onSiteCount", onsite.size());
        result.put("projectId", projectId);
        return AjaxResult.success(result);
    }
    
    /**
     * 删除准入记录
     */
    @DeleteMapping("/{id}")
    public AjaxResult<?> deleteAccess(@PathVariable Long id) {
        try {
            accessRecordService.removeById(id);
            return AjaxResult.success("准入记录删除成功");
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取准入记录详情
     */
    @GetMapping("/{id}")
    public AjaxResult<?> getAccessDetail(@PathVariable Long id) {
        try {
            AccessRecord record = accessRecordService.getById(id);
            if (record == null) {
                return AjaxResult.error("准入记录不存在");
            }
            return AjaxResult.success(record);
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 统计今日进场人数
     */
    @GetMapping("/stats/{projectId}")
    public AjaxResult<?> getStats(@PathVariable Long projectId) {
        Integer count = accessRecordService.countTodayAccess(projectId);
        List<AccessRecord> onsite = accessRecordService.listCurrentOnSite(projectId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("todayAccessCount", count);
        result.put("currentOnSiteCount", onsite.size());
        return AjaxResult.success(result);
    }
    
    /**
     * 批量删除准入记录
     */
    @PostMapping("/batch-delete")
    @ApiOperation("批量删除准入记录")
    public AjaxResult<?> batchDelete(@RequestBody List<Long> ids) {
        try {
            accessRecordService.batchDelete(ids);
            return AjaxResult.success("准入记录批量删除成功");
        } catch (Exception e) {
            return AjaxResult.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量入场登记
     */
    @PostMapping("/batch-entry")
    @ApiOperation("批量入场登记")
    public AjaxResult<?> batchEntry(@RequestBody List<AccessRecordDTO> records) {
        try {
            List<AccessRecord> accessRecords = accessRecordService.batchEntry(records);
            return AjaxResult.success("批量入场登记成功", accessRecords);
        } catch (Exception e) {
            return AjaxResult.error("批量入场登记失败：" + e.getMessage());
        }
    }

    /**
     * 批量出场登记
     */
    @PostMapping("/batch-exit")
    @ApiOperation("批量出场登记")
    public AjaxResult<?> batchExit(@RequestBody List<Long> ids) {
        try {
            List<AccessRecord> updatedRecords = accessRecordService.batchExit(ids);
            return AjaxResult.success("批量出场登记成功", updatedRecords);
        } catch (Exception e) {
            return AjaxResult.error("批量出场登记失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询所有准入记录
     */
    @GetMapping("/list")
    public AjaxResult<?> listPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String accessType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long orgId) {
        startPage();
        Map<String, Object> params = new HashMap<>();
        if (userName != null && !userName.trim().isEmpty()) {
            params.put("userName", userName);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            params.put("phone", phone);
        }
        if (accessType != null && !accessType.trim().isEmpty()) {
            params.put("accessType", accessType);
        }
        if (projectId != null) {
            params.put("projectId", projectId);
        }
        if (orgId != null) {
            params.put("orgId", orgId);
        }
        List<AccessRecord> list = accessRecordService.listPageWithUser(params);
        PageInfo<AccessRecord> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        return AjaxResult.success(result);
    }

    /**
     * 获取准入统计概览
     */
    @GetMapping("/statistics/overview")
    @ApiOperation("获取准入统计概览")
    public AjaxResult<?> getStatisticsOverview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", accessRecordService.count());
        stats.put("todayEntryCount", accessRecordService.countTodayEntry());
        stats.put("todayExitCount", accessRecordService.countTodayExit());
        stats.put("currentOnSiteCount", accessRecordService.countCurrentOnSite());
        stats.put("normalCount", accessRecordService.countByAccessType("normal"));
        stats.put("temporaryCount", accessRecordService.countByAccessType("temporary"));
        stats.put("deniedCount", accessRecordService.countByAccessType("denied"));
        return AjaxResult.success(stats);
    }

    /**
     * 更新准入记录
     */
    @PutMapping("/{id}")
    @ApiOperation("更新准入记录")
    public AjaxResult<?> updateAccess(@PathVariable Long id, @RequestBody AccessRecord record) {
        try {
            AccessRecord existing = accessRecordService.getById(id);
            if (existing == null) {
                return AjaxResult.error("准入记录不存在");
            }
            record.setId(id);
            boolean result = accessRecordService.updateById(record);
            return result ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
        } catch (Exception e) {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }
}
