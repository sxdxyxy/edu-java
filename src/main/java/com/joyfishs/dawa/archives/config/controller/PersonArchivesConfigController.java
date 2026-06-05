package com.joyfishs.dawa.archives.config.controller;

import com.joyfishs.dawa.archives.config.entity.PersonArchivesConfig;
import com.joyfishs.dawa.archives.config.entity.PersonArchivesData;
import com.joyfishs.dawa.archives.config.service.PersonArchivesConfigService;
import com.joyfishs.dawa.archives.config.service.PersonArchivesDataService;
import com.joyfishs.system.annotation.Log;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.system.domain.R;
import com.joyfishs.system.enums.BusinessType;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 人员档案配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/safetySupervision/archives/config")
public class PersonArchivesConfigController extends BaseController {

    @Autowired
    private PersonArchivesConfigService configService;
    
    @Autowired
    private PersonArchivesDataService archivesDataService;

    @ApiOperation("获取档案配置列表（按工种+机构，或仅按机构）")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('archives:config:list')")
    public AjaxResult<?> list(@RequestParam(required = false) Integer workType,
                           @RequestParam(required = false) Long orgId,
                           @RequestParam(required = false) Long projectId) {
        if (orgId == null) {
            return AjaxResult.error("机构 ID 不能为空");
        }

        List<PersonArchivesConfig> list = configService.listConfigs(workType, orgId, projectId);
        return AjaxResult.success(list);
    }

    @ApiOperation("获取配置详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('archives:config:query')")
    public AjaxResult<?> getInfo(@PathVariable Long id) {
        try {
            PersonArchivesConfig config = configService.getByIdWithCheck(id);
            return AjaxResult.success(config);
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation("新增档案配置")
    @PostMapping(value = { "", "/save" })
    @PreAuthorize("@ss.hasPermi('archives:config:add')")
    @Log(title = "新增档案配置", businessType = BusinessType.INSERT)
    public AjaxResult<?> add(@RequestBody PersonArchivesConfig config) {
        try {
            config.setIsActive(true);
            // 设置当前用户
            Long userId = SecurityUtil.getUserId();
            config.setCreateBy(userId);
            config.setUpdateBy(userId);
            if (config.getSortOrder() == null) {
                config.setSortOrder(0);
            }
            if (config.getId() != null) {
                boolean result = configService.updateById(config);
                return result ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
            } else {
                boolean result = configService.save(config);
                return result ? AjaxResult.success("添加成功") : AjaxResult.error("添加失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("添加失败：" + e.getMessage());
        }
    }

    @ApiOperation("修改档案配置")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('archives:config:edit')")
    @Log(title = "修改档案配置", businessType = BusinessType.UPDATE)
    public AjaxResult<?> edit(@PathVariable Long id, @RequestBody PersonArchivesConfig config) {
        try {
            configService.getByIdWithCheck(id);
            config.setId(id);
            // 设置更新用户
            config.setUpdateBy(SecurityUtil.getUserId());
            boolean result = configService.updateById(config);
            return result ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除档案配置")
    @DeleteMapping(value = { "/{id}", "/delete/{id}" })
    @PreAuthorize("@ss.hasPermi('archives:config:remove')")
    @Log(title = "删除档案配置", businessType = BusinessType.DELETE)
    public AjaxResult<?> remove(@PathVariable Long id) {
        try {
            configService.getByIdWithCheck(id);
            boolean result = configService.removeById(id);
            return result ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation("启用/禁用档案配置")
    @PutMapping("/{id}/active")
    @PreAuthorize("@ss.hasPermi('archives:config:edit')")
    @Log(title = "状态修改", businessType = BusinessType.UPDATE)
    public AjaxResult<?> toggleActive(@PathVariable Long id, @RequestParam Boolean isActive) {
        try {
            PersonArchivesConfig config = configService.getByIdWithCheck(id);
            config.setIsActive(isActive);
            config.setUpdateBy(SecurityUtil.getUserId());
            boolean result = configService.updateById(config);
            return result ? AjaxResult.success(isActive ? "启用成功" : "禁用成功") : AjaxResult.error("操作失败");
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation("初始化标准一人一档方案")
    @PostMapping("/initialize-default")
    @PreAuthorize("@ss.hasPermi('archives:config:add')")
    @Log(title = "初始化标准方案", businessType = BusinessType.INSERT)
    public AjaxResult<?> initializeDefault(@RequestParam Long orgId) {
        try {
            configService.initializeDefaultScheme(orgId);
            return AjaxResult.success("初始化成功");
        } catch (Exception e) {
            return AjaxResult.error("初始化失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取工种配置汇总列表")
    @GetMapping("/summary")
    public AjaxResult<?> summary(@RequestParam(required = false) Long orgId) {
        if (orgId == null) {
            orgId = 0L; // 或默认机构
        }
        
        // 获取该机构下所有配置
        List<PersonArchivesConfig> allConfigs = configService.getByOrgId(orgId);
        
        // 硬编码工种名称（与旧版保持一致，后续可对接字典）
        String[] workTypeNames = {"", "普工", "电工", "焊工", "起重工", "架子工", "安全员", "技术员", "管理员"};
        
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (int i = 1; i < workTypeNames.length; i++) {
            final int wt = i;
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("workType", wt);
            item.put("workTypeName", workTypeNames[wt]);
            
            // 查找该工种下的配置数量
            long count = allConfigs.stream().filter(c -> c.getWorkType() == wt).count();
            item.put("documentName", count > 0 ? "已配置 " + count + " 份档案模板" : "未配置");
            result.add(item);
        }
        
        return AjaxResult.success(result);
    }

    // --- 档案数据管理接口 (Merged from DataController) ---

    @ApiOperation("获取档案数据列表")
    @GetMapping("/data/list")
    @PreAuthorize("@ss.hasPermi('archives:data:list')")
    public AjaxResult<?> listData(PersonArchivesData query) {
        return AjaxResult.success(archivesDataService.listData(query));
    }

    @ApiOperation("保存档案数据")
    @PostMapping("/data/save")
    @PreAuthorize("@ss.hasPermi('archives:data:edit')")
    @Log(title = "保存档案数据", businessType = BusinessType.UPDATE)
    public AjaxResult<?> saveData(@RequestBody PersonArchivesData data) {
        try {
            boolean success = archivesDataService.saveOrUpdate(data);
            return success ? AjaxResult.success("保存成功") : AjaxResult.error("保存失败");
        } catch (Exception e) {
            return AjaxResult.error("保存失败：" + e.getMessage());
        }
    }

    @ApiOperation("自动同步系统数据")
    @PostMapping("/data/auto-sync")
    @PreAuthorize("@ss.hasPermi('archives:data:sync')")
    @Log(title = "自动同步档案数据", businessType = BusinessType.OTHER)
    public AjaxResult<?> autoSync(@RequestParam Long configId, @RequestParam Long orgId, @RequestParam(required = false) Integer workType) {
        try {
            int count = archivesDataService.autoSyncDataFromSystem(configId, orgId, workType);
            return AjaxResult.success("成功同步了 " + count + " 条人员档案数据");
        } catch (Exception e) {
            return AjaxResult.error("同步失败：" + e.getMessage());
        }
    }

    @ApiOperation("预览档案文档")
    @GetMapping("/data/preview/{id}")
    @PreAuthorize("@ss.hasPermi('archives:data:query')")
    public AjaxResult<?> preview(@PathVariable Long id, @RequestParam(required = false) String format) {
        try {
            String url = archivesDataService.generatePreviewUrl(id, format);
            return AjaxResult.success("预览生成成功", url);
        } catch (Exception e) {
            return AjaxResult.error("预览失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取档案数据二维码")
    @GetMapping("/data/qrcode/{id}")
    public void qrcode(@PathVariable Long id, javax.servlet.http.HttpServletResponse response) {
        try {
            byte[] bytes = archivesDataService.generateQrCode(id);
            response.setContentType("image/png");
            response.getOutputStream().write(bytes);
        } catch (Exception e) {
            log.error("获取二维码失败", e);
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"" + e.getMessage() + "\"}");
            } catch (Exception ex) {
                log.error("返回错误响应失败", ex);
            }
        }
    }

    @ApiOperation("删除档案数据")
    @DeleteMapping("/data/delete/{id}")
    @PreAuthorize("@ss.hasPermi('archives:data:delete')")
    @Log(title = "删除档案数据", businessType = BusinessType.DELETE)
    public AjaxResult<?> removeData(@PathVariable Long id) {
        boolean success = archivesDataService.removeById(id);
        return success ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
}
