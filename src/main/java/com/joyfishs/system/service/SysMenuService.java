package com.joyfishs.system.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.entity.SysMenu;
import com.joyfishs.system.enums.MenuTypeEnum;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.mapper.SysMenuMapper;
import com.joyfishs.system.pojo.LoginMenuTreeNode;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Transactional(rollbackFor = Exception.class)
    public boolean add(SysMenu sysMenu) {
        // 校验参数
        checkParam(sysMenu, false);


        // 设置新的pid
        String newPids = createNewPids(sysMenu.getPid());
        sysMenu.setPids(newPids);

        // 设置启用状态
        sysMenu.setCreateBy(SecurityUtil.getUserId());
        sysMenu.setCreateTime(new Date());
        sysMenu.setIsDelete(YesOrNoState.NO.getState());

        return this.save(sysMenu);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean edit(SysMenu sysMenu) {

        // 校验参数
        checkParam(sysMenu, true);

        // 获取修改的菜单的旧数据（库中的）
        SysMenu oldMenu = getById(sysMenu.getId());
        if(oldMenu == null || oldMenu.getId() == null) throw new CustomException("未找到菜单");

        // 本菜单旧的pids
        Long oldPid = oldMenu.getPid();
        String oldPids = oldMenu.getPids();

        // 生成新的pid和pids
        Long newPid = sysMenu.getPid();
        String newPids = this.createNewPids(sysMenu.getPid());

        // 父节点有变化,更新子节点
        if (!newPid.equals(oldPid)) {
            // 查找所有叶子节点，包含子节点的子节点
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(SysMenu::getPids,  "[" + oldMenu.getId() + "]");
            List<SysMenu> list = this.list(queryWrapper);

            list.forEach(child -> {
                String oldParentCodesPrefix = oldPids + "[" + oldMenu.getId() + "]" + ",";
                String oldParentCodesSuffix = child.getPids().substring(oldParentCodesPrefix.length());
                // 子节点pids组成 = 当前菜单新pids + 当前菜单id + 子节点自己的pids后缀
                String menuParentCodes = newPids + "[" + oldMenu.getId() + "]" + "," + oldParentCodesSuffix;
                child.setPids(menuParentCodes);
            });
            this.updateBatchById(list);
        }

        // 设置新的pids
        sysMenu.setPids(newPids);
        return this.updateById(sysMenu);
    }

    /**
     * 校验参数
     */
    private void checkParam(SysMenu sysMenu, boolean isExcludeSelf) {
        //菜单类型（字典 0目录 1菜单 2按钮）
        Integer type = sysMenu.getType();

        String router = sysMenu.getRouter();

        String permission = sysMenu.getPermission();

        if (!MenuTypeEnum.BTN.getCode().equals(type) && ObjectUtil.isEmpty(router)) {
            throw new CustomException("路由地址为空，请检查router参数");
        }

        if (MenuTypeEnum.BTN.getCode().equals(type)) {
            if (ObjectUtil.isEmpty(permission)) {
                throw new CustomException("权限标识为空，请检查permission参数");
            }
        }

        // 如果是编辑菜单时候，pid和id不能一致，一致会导致无限递归
        if (isExcludeSelf) {
            if (sysMenu.getId().equals(sysMenu.getPid())) {
                throw new CustomException("父级菜单不能为当前节点，请重新选择父级菜单");
            }

            // 如果是编辑，父id不能为自己的子节点
            List<Long> childIdListById = this.getChildIdListById(sysMenu.getId());
            if(ObjectUtil.isNotEmpty(childIdListById)) {
                if(childIdListById.contains(sysMenu.getPid())) {
                    throw new CustomException("父节点不能为本节点的子节点，请重新选择父节点");
                }
            }
        }

        Long id = sysMenu.getId();
        String name = sysMenu.getName();
        String code = sysMenu.getCode();

        LambdaQueryWrapper<SysMenu> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysMenu::getName, name)
                .eq(SysMenu::getIsDelete, YesOrNoState.NO.getState());

        LambdaQueryWrapper<SysMenu> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysMenu::getCode, code)
                .eq(SysMenu::getIsDelete, YesOrNoState.NO.getState());

        if (isExcludeSelf) {
            queryWrapperByName.ne(SysMenu::getId, id);
            queryWrapperByCode.ne(SysMenu::getId, id);
        }
        long countByName = this.count(queryWrapperByName);
        long countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new CustomException("菜单名称重复，请检查name参数");
        }
        if (countByCode >= 1) {
            throw new CustomException("菜单编码重复，请检查code参数");
        }
    }
    /**
     * 创建pids的值
     * <p>
     * 如果pid是0顶级节点，pids就是 [0],
     * <p>
     * 如果pid不是顶级节点，pids就是 pid菜单的pids + [pid] + ,
     */
    private String createNewPids(Long pid) {
        if (pid.equals(0L)) {
            return "[" + 0 + "]"
                    + ",";
        } else {
            //获取父菜单
            SysMenu parentMenu = this.getById(pid);
            return parentMenu.getPids()
                    + "[" + pid + "]"
                    + ",";
        }
    }

    /**
     * 根据菜单ID删除菜单
     *
     * @param id
     * @return
     */
    @Transactional
    public boolean del(Long id, String deleteReason){
        log.info("SysMenuService - del - id:{}", id);

        //级联删除子节点
        List<Long> childIdList = this.getChildIdListById(id);
        childIdList.add(id);
        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysMenu::getId, childIdList)
                .set(SysMenu::getIsDelete, YesOrNoState.YES.getState())
                .set(SysMenu::getDeleteBy, SecurityUtil.getUserId())
                .set(SysMenu::getDeleteTime, new Date())
                .set(SysMenu::getDeleteReason, deleteReason);
        boolean flag = this.update(updateWrapper);

        // 删除角色菜单引用
        sysRoleMenuService.deleteRoleMenuListByMenuIdList(childIdList);

        return flag;
    }

    /**
     * 根据节点id获取所有子节点id集合
     */
    private List<Long> getChildIdListById(Long id) {
        List<Long> childIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SysMenu::getPids, "[" + id + "]");
        this.list(queryWrapper).forEach(sysMenu -> childIdList.add(sysMenu.getId()));
        return childIdList;
    }

    /**
     * 查询菜单列表
     * @param sysMenu
     * @return
     */
    public List<SysMenu> queryList(SysMenu sysMenu) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getIsDelete, YesOrNoState.NO.getState());
        //根据菜单名称模糊查询
        if (StringUtils.isNotEmpty(sysMenu.getName())) {
            queryWrapper.like(SysMenu::getName, sysMenu.getName());
        }
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysMenu::getIntCode);
        List<SysMenu> sysMenuList = this.list(queryWrapper);
        return sysMenuList;
    }

    public List<SysMenu> findAll(){
        log.info("SysMenuService - findAll");

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getIsDelete, YesOrNoState.NO.getState()).orderByAsc(SysMenu::getIntCode);

        return this.list(queryWrapper);
    }

    /**
     * 查出并组装树形结构
     * @param list 所有节点菜单
     * @param pid 父级id
     * @return
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> list, Long pid){
        //组装数据
        List<SysMenu> collect = list.stream().filter(sysMenu -> {
            return pid.equals(sysMenu.getPid());
        }).map(sysMenu -> {
            sysMenu.setChildren(getChildren(sysMenu, list));
            return sysMenu;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 在所有列表中查询当前项的子项
     * @param o  当前节点
     * @param all  所有数据
     */
    private List<SysMenu> getChildren(SysMenu o, List<SysMenu> all){
        List<SysMenu> children = all.stream().filter((sysMenu) -> {
            return o.getId().equals(sysMenu.getPid());
        }).map(sysMenu -> {
            sysMenu.setChildren(getChildren(sysMenu, all));
            return sysMenu;
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 获取系统菜单树，用于新增，编辑时选择上级节点
     * @return
     */
    public List<SysMenu> tree() {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getIsDelete, YesOrNoState.NO.getState());
        List<SysMenu> allMenu = list(queryWrapper);
        allMenu = this.buildMenuTree(allMenu, 0L);
        return allMenu;
    }

    /**
     * 获取系统菜单树，用于给角色授权时选择
     */
    public List<SysMenu> treeForGrant() {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getIsDelete, YesOrNoState.NO.getState());
        //如果是超级管理员给角色授权菜单时可选择所有菜单
        if (SecurityUtil.getLoginUser().ifAdmin()) {
            // 不加任何判断条件
        } else {
            //非超级管理员则获取自己拥有的菜单，分配给人员，防止越级授权
            Long userId = SecurityUtil.getUserId();
            List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
            if (ObjectUtil.isNotEmpty(roleIdList)) {
                List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);
                if (ObjectUtil.isNotEmpty(menuIdList)) {
                    queryWrapper.in(SysMenu::getId, menuIdList);
                } else {
                    //如果角色的菜单为空，则查不到菜单
                    return CollectionUtil.newArrayList();
                }
            } else {
                //如果角色为空，则根本没菜单
                return CollectionUtil.newArrayList();
            }
        }
        queryWrapper.orderByAsc(SysMenu::getIntCode);
        List<SysMenu> allMenu = list(queryWrapper);
        allMenu = this.buildMenuTree(allMenu, 0L);
        return allMenu;
    }

    /**
     * 将SysMenu格式菜单转换为LoginMenuTreeNode菜单
     */
    public List<LoginMenuTreeNode> convertSysMenuToLoginMenu(List<SysMenu> sysMenuList) {
        List<LoginMenuTreeNode> antDesignMenuTreeNodeList = CollectionUtil.newArrayList();
        sysMenuList.forEach(sysMenu -> {
            if(sysMenu.getType() != MenuTypeEnum.BTN.getCode()) {
                LoginMenuTreeNode loginMenuTreeNode = new LoginMenuTreeNode();
                loginMenuTreeNode.setComponent(sysMenu.getComponent());
                loginMenuTreeNode.setId(sysMenu.getId());
                loginMenuTreeNode.setName(sysMenu.getCode());
                loginMenuTreeNode.setPath(sysMenu.getRouter());
                loginMenuTreeNode.setPid(sysMenu.getPid());
                LoginMenuTreeNode.Meta mateItem = new LoginMenuTreeNode.Meta();
                mateItem.setTitle(sysMenu.getName());
                //是否可见
                mateItem.setShow(YesOrNoState.YES.getState() == sysMenu.getVisible());
                mateItem.setIcon(sysMenu.getIcon());
                loginMenuTreeNode.setMeta(mateItem);
                antDesignMenuTreeNodeList.add(loginMenuTreeNode);
            }
        });
        return antDesignMenuTreeNodeList;
    }
}
