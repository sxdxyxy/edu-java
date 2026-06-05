package com.joyfishs.dawa.project.controller;

import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.project.entity.EngineeringOrgRel;
import com.joyfishs.dawa.project.entity.EngineeringProject;
import com.joyfishs.dawa.project.service.EngineeringProjectService;
import com.joyfishs.system.controller.BaseController;
import com.joyfishs.utils.AjaxResult;
import com.joyfishs.utils.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工程项目控制器
 */
@RestController
@RequestMapping("/project/engineering")
public class EngineeringProjectController extends BaseController {

    @Autowired
    private EngineeringProjectService engineeringProjectService;

    /**
     * 分页查询工程项目
     */
    @GetMapping("/pageList")
    @PreAuthorize("@ss.hasPermi('project:engineering:list')")
    public TableDataInfo<?> pageList(EngineeringProject project,
                                     @RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        var page = engineeringProjectService.pageList(project, pageNum, pageSize);
        TableDataInfo<EngineeringProject> rspData = new TableDataInfo<>();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal((int) page.getTotal());
        rspData.setPageSize((int) page.getSize());
        rspData.setPageNo((int) page.getCurrent());
        return rspData;
    }

    /**
     * 获取所有工程项目（用于下拉选择）
     */
    @GetMapping("/listAll")
    @PreAuthorize("@ss.hasPermi('project:engineering:list')")
    public TableDataInfo<?> listAll() {
        List<EngineeringProject> list = engineeringProjectService.listAll();
        return getDataTable(list);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('project:engineering:list')")
    public AjaxResult<?> getById(@PathVariable Long id) {
        EngineeringProject project = engineeringProjectService.getById(id);
        return project != null ? AjaxResult.success("查询成功", project) : AjaxResult.error("记录不存在");
    }

    /**
     * 新增工程项目
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('project:engineering:edit')")
    public AjaxResult<?> add(@RequestBody EngineeringProject project) {
        // TODO: 从token获取当前用户ID
        project.setCreateBy(1L);
        return toAjax(engineeringProjectService.add(project));
    }

    /**
     * 更新工程项目
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('project:engineering:edit')")
    public AjaxResult<?> update(@RequestBody EngineeringProject project) {
        return toAjax(engineeringProjectService.update(project));
    }

    /**
     * 删除工程项目
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('project:engineering:remove')")
    public AjaxResult<?> delete(@PathVariable Long id,
                                @RequestParam(required = false) String deleteReason) {
        // TODO: 从token获取当前用户ID
        return toAjax(engineeringProjectService.delete(id, 1L, deleteReason));
    }

    /**
     * 获取与工程项目关联的单位列表
     */
    @GetMapping("/orgs/{engineeringId}")
    @PreAuthorize("@ss.hasPermi('project:engineering:list')")
    public AjaxResult<?> getOrgsByEngineeringId(@PathVariable Long engineeringId) {
        List<SysOrg> list = engineeringProjectService.listOrgsByEngineeringId(engineeringId);
        return AjaxResult.success("查询成功", list);
    }

    /**
     * 获取工程项目关联单位关系列表（带角色）
     */
    @GetMapping("/orgRels/{engineeringId}")
    @PreAuthorize("@ss.hasPermi('project:engineering:list')")
    public AjaxResult<?> getOrgRelsByEngineeringId(@PathVariable Long engineeringId) {
        List<EngineeringOrgRel> list = engineeringProjectService.listOrgRelsByEngineeringId(engineeringId);
        return AjaxResult.success("查询成功", list);
    }

    /**
     * 添加工程项目与单位关联
     */
    @PostMapping("/orgRel")
    @PreAuthorize("@ss.hasPermi('project:engineering:edit')")
    public AjaxResult<?> addOrgRel(@RequestBody EngineeringOrgRel rel) {
        int result = engineeringProjectService.addOrgRel(rel);
        if (result == -1) {
            return AjaxResult.error("该单位已关联此工程项目");
        }
        return toAjax(result);
    }

    /**
     * 删除工程项目与单位关联
     */
    @DeleteMapping("/orgRel/{id}")
    @PreAuthorize("@ss.hasPermi('project:engineering:edit')")
    public AjaxResult<?> deleteOrgRel(@PathVariable Long id) {
        return toAjax(engineeringProjectService.deleteOrgRel(id));
    }

    /**
     * 解除项目部与工程项目的关联
     */
    @DeleteMapping("/orgDisassociate/{orgId}")
    @PreAuthorize("@ss.hasPermi('project:engineering:edit')")
    public AjaxResult<?> disassociateOrg(@PathVariable Long orgId) {
        return toAjax(engineeringProjectService.disassociateOrg(orgId));
    }
}
