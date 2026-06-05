package com.joyfishs.dawa.org.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.org.entity.SysOrgRole;
import com.joyfishs.dawa.org.mapper.SysOrgRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目部内部角色服务类
 */
@Service
public class SysOrgRoleService extends ServiceImpl<SysOrgRoleMapper, SysOrgRole> {

    /**
     * 根据项目部ID查询角色列表
     */
    public List<SysOrgRole> listByOrgId(Long orgId) {
        LambdaQueryWrapper<SysOrgRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrgRole::getOrgId, orgId).orderByAsc(SysOrgRole::getSort);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 新增角色
     */
    public int addRole(SysOrgRole role) {
        return baseMapper.insert(role);
    }

    /**
     * 更新角色
     */
    public int updateRole(SysOrgRole role) {
        return baseMapper.updateById(role);
    }

    /**
     * 删除角色
     */
    public int deleteRole(Long id) {
        return baseMapper.deleteById(id);
    }
}
