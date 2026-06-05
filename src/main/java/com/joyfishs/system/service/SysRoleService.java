package com.joyfishs.system.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.mapper.SysRoleMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 新增或修改
     *
     * @param sysRole
     * @return
     */
    public boolean saveOrUpdate(SysRole sysRole){
        log.info("SysRoleService - saveOrUpdate sysRole:{}", sysRole);

        if(StringUtils.isNull(sysRole.getId())){
            //校验参数，检查是否存在相同的名称和编码
            checkParam(sysRole, false);
            // 新增
            sysRole.setParentId(sysRole.getParentId() == null ? -1 : sysRole.getParentId());
            sysRole.setCreateBy(SecurityUtil.getUserId());
            sysRole.setCreateTime(new Date());
            sysRole.setIsDelete(YesOrNoState.NO.getState());
            log.info("SysRoleService - saveOrUpdate save sysRole:{}", sysRole);

            return save(sysRole);
        } else {
            //校验参数，检查是否存在相同的名称和编码
            checkParam(sysRole, true);
            // 修改
            SysRole sysRoleOld = querySysRole(sysRole.getId());

            sysRoleOld.setName(sysRole.getName());
            sysRoleOld.setCode(sysRole.getCode());
            sysRoleOld.setSort(sysRole.getSort());

            sysRoleOld.setOrgCode(sysRole.getOrgCode());
            sysRoleOld.setParentId(sysRole.getParentId());
            sysRoleOld.setUpdateBy(SecurityUtil.getUserId());
            sysRoleOld.setUpdateTime(new Date());
            log.info("SysRoleService - saveOrUpdate update sysRole:{}", sysRoleOld);

            return updateById(sysRoleOld);
        }
    }

    /**
     * 校验参数，检查是否存在相同的名称
     */
    private void checkParam(SysRole sysRole, boolean isExcludeSelf) {
        Long id = sysRole.getId();
        String name = sysRole.getName();
        String code = sysRole.getCode();

        LambdaQueryWrapper<SysRole> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysRole::getName, name)
                .eq(SysRole::getIsDelete, YesOrNoState.NO.getState());

        LambdaQueryWrapper<SysRole> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysRole::getCode, code)
                .eq(SysRole::getIsDelete, YesOrNoState.NO.getState());

        //是否排除自己，如果排除自己则不查询自己的id
        if (isExcludeSelf) {
            queryWrapperByName.ne(SysRole::getId, id);
            queryWrapperByCode.ne(SysRole::getId, id);
        }
        long countByName = this.count(queryWrapperByName);
        long countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new CustomException("角色名称重复，请检查name参数");
        }
        if (countByCode >= 1) {
            throw new CustomException("角色编码重复，请检查code参数");
        }
    }

    /**
     * 获取系统角色
     */
    private SysRole querySysRole(Long id) {
        SysRole sysRole = this.getById(id);
        if (ObjectUtil.isNull(sysRole)) {
            throw new CustomException("角色不存在");
        }
        return sysRole;
    }

    /**
     * 删除
     */
    @Transactional
    public boolean del(Long id){
        SysRole sysRole = querySysRole(id);

        // 删除角色菜单引用
        sysRoleMenuService.deleteRoleMenuListByRoleId(sysRole.getId());
        // 删除用户角色引用
        sysUserRoleService.delByRoleId(sysRole.getId());

        sysRole.setIsDelete(YesOrNoState.YES.getState());
        sysRole.setDeleteBy(SecurityUtil.getUserId());
        sysRole.setDeleteTime(new Date());

        return updateById(sysRole);
    }

    /**
     * 查询角色
     *
     * @param sysRole
     * @return
     */
    public List<SysRole> findList(SysRole sysRole){
        log.info("SysRoleService - findList - sysRole:{}", sysRole);

        if(sysRole.getParentId() == null) sysRole.setParentId(-1L);

        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getParentId, sysRole.getParentId())
                .eq(SysRole::getIsDelete, YesOrNoState.NO.getState());

        if(StringUtils.isNotEmpty(sysRole.getName())) queryWrapper.like(SysRole::getName, sysRole.getName());
        if(StringUtils.isNotNull(sysRole.getOrgCode())) queryWrapper.eq(SysRole::getOrgCode, sysRole.getOrgCode());

        return list(queryWrapper);
    }

    /**
     * 查询所有角色信息
     * @return
     */
    public List<SysRole> findAll(){
        log.info("SysRoleService - findAll");

        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getIsDelete, YesOrNoState.NO.getState());

        return list(queryWrapper);
    }

    public List<Long> ownMenu(Long id) {
        SysRole sysRole = querySysRole(id);
        return sysRoleMenuService.getRoleMenuIdList(CollectionUtil.newArrayList(sysRole.getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void grantMenu(Long id, List<Long> grantMenuIdList) {
        this.querySysRole(id);
        sysRoleMenuService.grantMenu(id, grantMenuIdList);
    }

}
