package com.joyfishs.system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.entity.SysMenu;
import com.joyfishs.system.entity.SysRoleMenu;
import com.joyfishs.system.mapper.SysRoleMenuMapper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysRoleMenuService extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> {

    public List<Long> getRoleMenuIdList(List<Long> roleIdList) {
        if(ObjectUtil.isNotEmpty(roleIdList)) {
            LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysRoleMenu::getRoleId, roleIdList);
            return this.list(queryWrapper).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        }
        return CollectionUtil.newArrayList();
    }

    public void grantMenu(Long roleId, List<Long> grantMenuIdList) {
        //删除所拥有菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);
        //授权菜单
        grantMenuIdList.forEach(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            this.save(sysRoleMenu);
        });
    }

    public void deleteRoleMenuListByMenuIdList(List<Long> menuIdList) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysRoleMenu::getMenuId, menuIdList);
        this.remove(queryWrapper);
    }

    public void deleteRoleMenuListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);
    }

    /**
     * 根据角色ID获取菜单
     * @param roleId
     * @return
     */
    public List<SysMenu> findMenuListByRoleId(Long roleId) {
        return baseMapper.findMenuListByRoleId(roleId);

    }
}
