package com.joyfishs.dawa.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.project.entity.ProjectPersonRole;
import com.joyfishs.dawa.project.mapper.ProjectPersonRoleMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目人员职务 Service
 * <p>
 * 提供项目人员职务的分配、移除、查询等功能
 * </p>
 *
 * @author OpenClaw
 * @since 2026-04-26
 */
@Slf4j
@Service
public class ProjectPersonRoleService extends ServiceImpl<ProjectPersonRoleMapper, ProjectPersonRole> {

    /**
     * 分配职务
     * <p>
     * 为项目中的特定人员分配职务
     * </p>
     *
     * @param personId  人员 ID
     * @param projectId 项目 ID
     * @param roleCode  职务编码
     * @return 分配成功返回 true，已存在则返回 false
     */
    @Transactional
    public boolean assignRole(Long personId, Long projectId, String roleCode) {
        if (personId == null || projectId == null || roleCode == null) {
            throw new IllegalArgumentException("人员 ID、项目 ID 和职务编码不能为空");
        }

        if (!ProjectPersonRole.isValidRoleCode(roleCode)) {
            throw new IllegalArgumentException("无效的职务编码: " + roleCode);
        }

        // 检查是否已存在该职务
        if (hasRole(personId, projectId, roleCode)) {
            log.debug("人员 {} 在项目 {} 中已拥有职务 {}", personId, projectId, roleCode);
            return false;
        }

        ProjectPersonRole role = new ProjectPersonRole();
        role.setPersonId(personId);
        role.setProjectId(projectId);
        role.setRoleCode(roleCode);

        return save(role);
    }

    /**
     * 移除职务
     * <p>
     * 从项目中移除特定人员的职务
     * </p>
     *
     * @param personId  人员 ID
     * @param projectId 项目 ID
     * @param roleCode  职务编码
     * @return 移除成功返回 true，不存在则返回 false
     */
    @Transactional
    public boolean removeRole(Long personId, Long projectId, String roleCode) {
        if (personId == null || projectId == null || roleCode == null) {
            throw new IllegalArgumentException("人员 ID、项目 ID 和职务编码不能为空");
        }

        LambdaQueryWrapper<ProjectPersonRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPersonRole::getPersonId, personId)
               .eq(ProjectPersonRole::getProjectId, projectId)
               .eq(ProjectPersonRole::getRoleCode, roleCode);

        return remove(wrapper);
    }

    /**
     * 检查是否拥有职务
     * <p>
     * 检查特定人员是否在指定项目中拥有特定职务
     * </p>
     *
     * @param personId  人员 ID
     * @param projectId 项目 ID
     * @param roleCode  职务编码
     * @return 是否拥有该职务
     */
    @Transactional(readOnly = true)
    public boolean hasRole(Long personId, Long projectId, String roleCode) {
        if (personId == null || projectId == null || roleCode == null) {
            return false;
        }

        LambdaQueryWrapper<ProjectPersonRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPersonRole::getPersonId, personId)
               .eq(ProjectPersonRole::getProjectId, projectId)
               .eq(ProjectPersonRole::getRoleCode, roleCode);

        return count(wrapper) > 0;
    }

    /**
     * 获取人员的所有项目职务
     * <p>
     * 根据人员 ID 查询该人员在所有项目中的职务
     * </p>
     *
     * @param personId 人员 ID
     * @return 职务列表
     */
    @Transactional(readOnly = true)
    public List<ProjectPersonRole> getRolesByPersonId(Long personId) {
        if (personId == null) {
            throw new IllegalArgumentException("人员 ID 不能为空");
        }

        LambdaQueryWrapper<ProjectPersonRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPersonRole::getPersonId, personId)
               .orderByDesc(ProjectPersonRole::getCreateTime);

        return list(wrapper);
    }

    /**
     * 获取项目的所有职务
     * <p>
     * 根据项目 ID 查询该项目中的所有人员职务
     * </p>
     *
     * @param projectId 项目 ID
     * @return 职务列表
     */
    @Transactional(readOnly = true)
    public List<ProjectPersonRole> getRolesByProjectId(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("项目 ID 不能为空");
        }

        LambdaQueryWrapper<ProjectPersonRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPersonRole::getProjectId, projectId)
               .orderByDesc(ProjectPersonRole::getCreateTime);

        return list(wrapper);
    }

    /**
     * 获取人员在特定项目中的所有职务
     *
     * @param personId  人员 ID
     * @param projectId 项目 ID
     * @return 职务列表
     */
    @Transactional(readOnly = true)
    public List<ProjectPersonRole> getRolesByPersonIdAndProjectId(Long personId, Long projectId) {
        if (personId == null || projectId == null) {
            throw new IllegalArgumentException("人员 ID 和项目 ID 不能为空");
        }

        LambdaQueryWrapper<ProjectPersonRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPersonRole::getPersonId, personId)
               .eq(ProjectPersonRole::getProjectId, projectId)
               .orderByDesc(ProjectPersonRole::getCreateTime);

        return list(wrapper);
    }

    /**
     * 移除人员在项目中的所有职务
     *
     * @param personId  人员 ID
     * @param projectId 项目 ID
     * @return 移除的职务数量
     */
    @Transactional
    public int removeAllRoles(Long personId, Long projectId) {
        if (personId == null || projectId == null) {
            throw new IllegalArgumentException("人员 ID 和项目 ID 不能为空");
        }

        LambdaQueryWrapper<ProjectPersonRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectPersonRole::getPersonId, personId)
               .eq(ProjectPersonRole::getProjectId, projectId);

        List<ProjectPersonRole> roles = list(wrapper);
        if (roles.isEmpty()) {
            return 0;
        }

        remove(wrapper);
        return roles.size();
    }
}
