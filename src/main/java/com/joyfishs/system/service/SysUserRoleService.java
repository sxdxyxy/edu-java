package com.joyfishs.system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.entity.SysUserRole;
import com.joyfishs.system.mapper.SysUserRoleMapper;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysUserRoleService extends ServiceImpl<SysUserRoleMapper, SysUserRole> {

    /**
     * 新能用户角色
     *
     * @param userId
     * @param roleIdList
     * @return
     */
    @Transactional
    public boolean save(Long userId, List<Long> roleIdList){
        log.info("SysUserRoleService - save userId:{}", userId);
        log.info("SysUserRoleService - save roleIdList:{}", roleIdList);

        if(StringUtils.isNull(userId)) throw new CustomException("用户ID不能为空");
        if(StringUtils.isNull(roleIdList) || roleIdList.size() == 0) throw new CustomException("角色ID不能为空");

        List<SysUserRole> sysUserRoleList = new ArrayList<>();
        for (Long roleId : roleIdList) {
            if(StringUtils.isNull(roleId)) continue;

            sysUserRoleList.add(new SysUserRole().setUserId(userId).setRoleId(roleId));
        }

        return saveBatch(sysUserRoleList);
    }

    /**
     * 根据用户ID删除
     *
     * @param userId
     * @return
     */
    public boolean delByUserId(Long userId){
        log.info("SysUserRoleService - delByUserId userId:{}", userId);

        if(StringUtils.isNull(userId)) throw new CustomException("用户ID不能为空");

        return baseMapper.delByUserId(userId) > 0;
    }

    /**
     * 根据角色ID删除
     *
     * @param roleId
     * @return
     */
    public boolean delByRoleId(Long roleId){
        log.info("SysUserRoleService - delByRoleId roleId:{}", roleId);

        if(StringUtils.isNull(roleId)) throw new CustomException("角色ID不能为空");

        return baseMapper.delByRoleId(roleId) > 0;
    }

    /**
     * 根据用户ID和角色ID删除
     *
     * @param userId
     * @param roleIdList
     * @return
     */
    @Transactional
    public boolean delByUserIdAndRoleId(Long userId, List<Long> roleIdList){
        log.info("SysUserRoleService - delByUserIdAndRoleId userId:{}", userId);
        log.info("SysUserRoleService - delByUserIdAndRoleId roleIdList:{}", roleIdList);

        if(StringUtils.isNull(userId)) throw new CustomException("用户ID不能为空");
        if(StringUtils.isNull(roleIdList) || roleIdList.size() == 0) throw new CustomException("角色ID不能为空");

        for (Long roleId : roleIdList) {
            baseMapper.delByUserIdAndRoleId(userId, roleId);
        }

        return true;
    }

    /**
     * 根据角色ID获取用户
     *
     * @param roleId
     * @return
     */
    public List<SysUser> findUserListByRoleId(Long roleId){
        log.info("SysUserRoleService - findUserListByRoleId roleId:{}", roleId);

        if(StringUtils.isNull(roleId)) return new ArrayList<>();

        return baseMapper.findUserListByRoleId(roleId);
    }

    /**
     * 根据用户ID获取角色
     *
     * @param userid
     * @return
     */
    public List<SysRole> findRoleListByUserId(Long userid){
        log.info("SysUserRoleService - findRoleListByUserId roleId:{}", userid);

        if(StringUtils.isNull(userid)) return new ArrayList<>();

        return baseMapper.findRoleListByUserId(userid);
    }

    /* 2021-08-28 新增方法*/

    public List<Long> getUserRoleIdList(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);

        return this.list(queryWrapper).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    public void grantRole(Long userId, List<Long> grantRoleIdList) {
        //删除所拥有角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);
        //授权角色
        grantRoleIdList.forEach(roleId -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            this.save(sysUserRole);
        });
    }

    public SysUserRole getByUserIdAndRoleCode(Long userId, String roleCode) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        queryWrapper.inSql(SysUserRole::getRoleId, "select id from sys_role where is_delete = 0 and code = '" + roleCode + "'");
        queryWrapper.last("limit 1");
        return this.getOne(queryWrapper);
    }

}
