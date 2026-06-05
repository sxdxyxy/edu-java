package com.joyfishs.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.config.security.Md5PasswordEncoder;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.mapper.SysUserMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.SpringUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 新增或修改系统用户
     *
     * @param sysUser
     * @return
     */
    public boolean saveOrUpdate(SysUser sysUser){
        log.info("SysUserService - saveOrUpdate sysUser:{}", sysUser);

        if(StringUtils.isNull(sysUser.getId())){
            // 新增
            checkParam(sysUser, false);

            sysUser.setSalt(DigestUtil.md5Hex(sysUser.getUserName()));

            Md5PasswordEncoder md5PasswordEncoder = Md5PasswordEncoder.getInstance();
            md5PasswordEncoder.setSalt(sysUser.getSalt());
            sysUser.setPassword(md5PasswordEncoder.encode(sysUser.getPassword()));

            sysUser.setStatus(1);
            sysUser.setCreateBy(SecurityUtil.getUserId());
            sysUser.setCreateTime(new Date());
            sysUser.setIsDelete(YesOrNoState.NO.getState());
            log.info("SysUserService - saveOrUpdate save sysUser:{}", sysUser);

            return save(sysUser);
        } else {
            // 修改
            checkParam(sysUser, true);

            SysUser sysUserOld = getById(sysUser.getId());
            if(StringUtils.isNull(sysUserOld) || StringUtils.isNull(sysUserOld.getId()))
                throw new CustomException("未找到要修改的用户");

            // 禁止修改的内容
            sysUser.setSalt(null);
            sysUser.setPassword(null);

            sysUser.setUpdateBy(SecurityUtil.getUserId());
            sysUser.setUpdateTime(new Date());
            log.info("SysUserService - saveOrUpdate update sysUser:{}", sysUser);

            return updateById(sysUser);
        }
    }

    /**
     * 校验参数，检查是否存在相同的账号
     */
    private void checkParam(SysUser sysUser, boolean isExcludeSelf) {
        Long id = sysUser.getId();
        String userName = sysUser.getUserName();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, userName)
                .eq(SysUser::getIsDelete, YesOrNoState.NO.getState());
        //是否排除自己，如果是则查询条件排除自己id
        if (isExcludeSelf) {
            queryWrapper.ne(SysUser::getId, id);
        }
        long countByAccount = this.count(queryWrapper);
        //大于等于1个则表示重复
        if (countByAccount >= 1) {
            throw new CustomException("用户名：" + sysUser.getUserName() + "已存在");
        }
    }

    /**
     * 根据用户ID删除用户
     *
     * @param idList
     * @param deleteReason
     * @return
     */
    public boolean del(List<Long> idList, String deleteReason){
        log.info("SysUserService - del idList:{}", idList);
        log.info("SysUserService - del deleteReason:{}", deleteReason);

        if(StringUtils.isNull(idList) || idList.size() == 0) return true;

        List<SysUser> sysUserList = new ArrayList<>();
        for (Long id : idList) {
            SysUser sysUser = getById(id);

            if(StringUtils.isNull(sysUser) || StringUtils.isNull(sysUser.getId())) continue;

            // 删除用户角色引用
            SpringUtil.getBean(SysUserRoleService.class).delByUserId(sysUser.getId());

            sysUser.setIsDelete(YesOrNoState.YES.getState());
            sysUser.setDeleteBy(SecurityUtil.getUserId());
            sysUser.setDeleteTime(new Date());
            sysUser.setDeleteReason(deleteReason);
            log.info("SysUserService - del sysUser:{}", sysUser);

            sysUserList.add(sysUser);
        }

        return updateBatchById(sysUserList);
    }

    /**
     * 系统用户查询
     *
     * @param sysUser
     * @return
     */
    public List<SysUser> findList(SysUser sysUser){
        log.info("SysUserService - findList sysUser:{}", sysUser);

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getIsDelete, YesOrNoState.NO.getState());

        if(StringUtils.isNotEmpty(sysUser.getName())) queryWrapper.like(SysUser::getName, sysUser.getName());
        if(StringUtils.isNotEmpty(sysUser.getUserName())) queryWrapper.like(SysUser::getUserName, sysUser.getUserName());
        if(StringUtils.isNotEmpty(sysUser.getPhone())) queryWrapper.like(SysUser::getPhone, sysUser.getPhone());

        return list(queryWrapper);
    }

    /**
     * 根据登录名查询系统用户
     *
     * @param userName
     * @return
     */
    public SysUser findByUserName(String userName){
        log.info("SysUserService - findByUserName userName:{}", userName);
        return baseMapper.findByUserName(userName);
    }

    /**
     * 根据登录名或电话查询系统用户
     *
     * @param userName
     * @return
     */
    public SysUser findByUserNameOrPhone(String userName) {
        log.info("SysUserService - findByUserNameOrPhone userName:{}", userName);
        return baseMapper.findByUserNameOrPhone(userName);
    }

    /**
     * 根据电话查询用户
     *
     * @param phone
     * @return
     */
    public SysUser findByPhone(String phone) {
        return baseMapper.findByPhone(phone);
    }

    /**
     * 获取系统用户
     */
    private SysUser querySysUser(Long id) {
        SysUser sysUser = this.getById(id);
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException("用户不存在");
        }
        return sysUser;
    }

    public List<Long> ownRole(Long id) {
        SysUser sysUser = this.querySysUser(id);
        return sysUserRoleService.getUserRoleIdList(sysUser.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void grantRole(Long id, List<Long> grantRoleIdList) {
        this.querySysUser(id);
        sysUserRoleService.grantRole(id, grantRoleIdList);
        // 安全修复: 角色变更后清除用户的所有登录Token,使旧Token失效
        // TODO: 实现Token清除逻辑（getLoginUserFromContext方法待确认）
        // com.joyfishs.utils.SecurityUtil.getLoginUserFromContext()...
        // 如需强制所有Token失效,可调用: tokenService.delLoginUser(userToken)
    }

    // 重置密码
    @Transactional
    public boolean resetPwd(Long id) {
        // 修改用户密码
        SysUser sysUser = this.getById(id);
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException("未查询到用户信息");
        }
        sysUser.setSalt(DigestUtil.md5Hex(sysUser.getUserName()));

        Md5PasswordEncoder md5PasswordEncoder = Md5PasswordEncoder.getInstance();
        md5PasswordEncoder.setSalt(sysUser.getSalt());
        sysUser.setPassword(md5PasswordEncoder.encode(SysUser.DEFAULT_PASSWORD));
        return updateById(sysUser);
    }

    // 修改用户密码
    @Transactional
    public boolean changePwd(Long userId, String oldPassword, String password) {
        SysUser sysUser = this.getById(userId);
        if (ObjectUtil.isNull(sysUser)) {
            throw new CustomException("未查询到用户信息");
        }
        Md5PasswordEncoder.getInstance().setSalt(sysUser.getSalt());
        if(!Md5PasswordEncoder.getInstance().matches(oldPassword, sysUser.getPassword())){
            throw new CustomException("原密码不正确！");
        }
        return changePassword(sysUser,password);
    }

    // 修改密码
    @Transactional
    public boolean changePassword(SysUser sysUser, String password) {
        Md5PasswordEncoder md5PasswordEncoder = Md5PasswordEncoder.getInstance();
        md5PasswordEncoder.setSalt(sysUser.getSalt());
        sysUser.setPassword(md5PasswordEncoder.encode(password));
        sysUser.setUpdateBy(sysUser.getId());
        sysUser.setUpdateTime(new Date());
        return updateById(sysUser);
    }
}
