package com.joyfishs.dawa.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.mapper.SysOrgMapper;
import com.joyfishs.dawa.project.entity.EngineeringOrgRel;
import com.joyfishs.dawa.project.entity.EngineeringProject;
import com.joyfishs.dawa.project.mapper.EngineeringOrgRelMapper;
import com.joyfishs.dawa.project.mapper.EngineeringProjectMapper;
import com.joyfishs.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 工程项目服务类
 */
@Service
public class EngineeringProjectService {

    @Autowired
    private EngineeringProjectMapper engineeringProjectMapper;

    @Autowired
    private EngineeringOrgRelMapper engineeringOrgRelMapper;

    @Autowired
    private SysOrgMapper sysOrgMapper;

    /**
     * 分页查询工程项目
     */
    public IPage<EngineeringProject> pageList(EngineeringProject project, int pageNum, int pageSize) {
        Page<EngineeringProject> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EngineeringProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(project.getName()), EngineeringProject::getName, project.getName())
               .eq(project.getStatus() != null, EngineeringProject::getStatus, project.getStatus())
               .eq(EngineeringProject::getIsDelete, 0)
               .orderByDesc(EngineeringProject::getCreateTime);
        return engineeringProjectMapper.selectPage(page, wrapper);
    }

    /**
     * 查询所有工程项目（用于下拉选择）
     */
    public List<EngineeringProject> listAll() {
        LambdaQueryWrapper<EngineeringProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EngineeringProject::getIsDelete, 0)
               .orderByDesc(EngineeringProject::getCreateTime);
        return engineeringProjectMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询
     */
    public EngineeringProject getById(Long id) {
        return engineeringProjectMapper.selectById(id);
    }

    /**
     * 新增工程项目
     */
    public int add(EngineeringProject project) {
        project.setIsDelete(0);
        project.setCreateTime(LocalDateTime.now());
        return engineeringProjectMapper.insert(project);
    }

    /**
     * 更新工程项目
     */
    public int update(EngineeringProject project) {
        project.setUpdateTime(LocalDateTime.now());
        return engineeringProjectMapper.updateById(project);
    }

    /**
     * 删除工程项目（逻辑删除）
     */
    public int delete(Long id, Long userId, String reason) {
        EngineeringProject project = new EngineeringProject();
        project.setId(id);
        project.setIsDelete(1);
        project.setDeleteBy(userId);
        project.setDeleteReason(reason);
        project.setDeleteTime(LocalDateTime.now());
        return engineeringProjectMapper.updateById(project);
    }

    /**
     * 获取与工程项目关联的单位列表
     */
    public List<SysOrg> listOrgsByEngineeringId(Long engineeringId) {
        if (engineeringId == null) {
            return new ArrayList<>();
        }
        // 查询关联表获取单位ID列表
        LambdaQueryWrapper<EngineeringOrgRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(EngineeringOrgRel::getEngineeringId, engineeringId);
        List<EngineeringOrgRel> rels = engineeringOrgRelMapper.selectList(relWrapper);

        if (rels == null || rels.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> orgIds = rels.stream().map(EngineeringOrgRel::getOrgId).collect(Collectors.toSet());

        // 查询单位详情
        LambdaQueryWrapper<SysOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.in(SysOrg::getId, orgIds)
                  .eq(SysOrg::getIsDelete, 0)
                  .eq(SysOrg::getOrgType, 1); // 只查询组织机构类型
        return sysOrgMapper.selectList(orgWrapper);
    }

    /**
     * 获取工程项目关联单位关系列表（带角色信息和组织详情）
     */
    public List<EngineeringOrgRel> listOrgRelsByEngineeringId(Long engineeringId) {
        if (engineeringId == null) {
            return new ArrayList<>();
        }
        // 通过 sys_org 表的 engineering_id 字段查询关联的项目部
        LambdaQueryWrapper<SysOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(SysOrg::getEngineeringId, engineeringId)
                  .eq(SysOrg::getIsDelete, 0);
        List<SysOrg> orgs = sysOrgMapper.selectList(orgWrapper);

        // 转换为 EngineeringOrgRel 格式
        List<EngineeringOrgRel> result = new ArrayList<>();
        for (SysOrg org : orgs) {
            EngineeringOrgRel rel = new EngineeringOrgRel();
            rel.setId(org.getId()); // 使用 org.id 作为关联ID
            rel.setEngineeringId(engineeringId);
            rel.setOrgId(org.getId());
            rel.setName(org.getName());
            rel.setCode(org.getCode());
            rel.setRoleType("construct"); // 默认施工单位
            result.add(rel);
        }
        return result;
    }

    /**
     * 添加工程项目与单位关联（通过设置 sys_org.engineering_id）
     */
    public int addOrgRel(EngineeringOrgRel rel) {
        // 直接更新 sys_org 的 engineering_id 字段
        SysOrg org = sysOrgMapper.selectById(rel.getOrgId());
        if (org == null) {
            return 0;
        }
        if (org.getEngineeringId() != null && org.getEngineeringId().equals(rel.getEngineeringId())) {
            return -1; // 已存在
        }
        org.setEngineeringId(rel.getEngineeringId());
        return sysOrgMapper.updateById(org);
    }

    /**
     * 删除工程项目与单位关联
     */
    public int deleteOrgRel(Long id) {
        return engineeringOrgRelMapper.deleteById(id);
    }

    /**
     * 解除项目部与工程项目的关联（通过清空 sys_org.engineering_id）
     */
    public int disassociateOrg(Long orgId) {
        SysOrg org = sysOrgMapper.selectById(orgId);
        if (org == null) {
            return 0;
        }
        org.setEngineeringId(null);
        return sysOrgMapper.updateById(org);
    }

    /**
     * 删除工程项目与单位关联（按工程ID和单位ID）
     */
    public int deleteOrgRelByEngAndOrg(Long engineeringId, Long orgId) {
        LambdaQueryWrapper<EngineeringOrgRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EngineeringOrgRel::getEngineeringId, engineeringId)
               .eq(EngineeringOrgRel::getOrgId, orgId);
        return engineeringOrgRelMapper.delete(wrapper);
    }
}
