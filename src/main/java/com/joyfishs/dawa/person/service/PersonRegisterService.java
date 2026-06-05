package com.joyfishs.dawa.person.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.domain.param.PersonRegisterReq;
import com.joyfishs.dawa.person.domain.result.PersonListResult;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.entity.PersonRegister;
import com.joyfishs.dawa.person.enums.PersonChangeType;
import com.joyfishs.dawa.person.mapper.PersonRegisterMapper;
import com.joyfishs.system.config.security.Md5PasswordEncoder;
import com.joyfishs.system.entity.SysRole;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.enums.DeviceType;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.service.SysUserRoleService;
import com.joyfishs.system.service.SysUserService;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PersonRegisterService extends ServiceImpl<PersonRegisterMapper, PersonRegister> {

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PersonService personService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private PersonChangeLogService personChangeLogService;

    public List<PersonListResult> findList(Long orgId) {
        List<Person> registerList = baseMapper.queryList(orgId);
        List<PersonListResult> list = personService.packPersonResult(registerList);
        return list;
    }

    @Transactional
    public PersonRegister add(PersonRegisterReq req) {
        if (Validator.isEmpty(req.getOrgId())) {
            req.setOrgId(sysOrgService.getByCode(PersonRegister.DEFAULT_ORG_CODE).getId());
        }
        // 1. 注册信息
        PersonRegister register = baseMapper.findByPhone(req.getPhone());
        if (ObjectUtil.isNull(register)) {
            register = new PersonRegister();
            register.setUserName(req.getPhone());
            register.setPhone(req.getPhone());
            register.setName(req.getName());
            register.setIsDelete(YesOrNoState.NO.getState());
            register.setCreateTime(new Date());
            register.setRegDevice(DeviceType.valueOfType(req.getDeviceType()));
        }
        register.setOrgId(req.getOrgId());
        register.setOpenid(req.getOpenid());
        register.setUnionid(req.getUnionid());
        //如果是已删除用户，修改删除状态为正常
        if (register.getIsDelete() == YesOrNoState.YES.getState()) {
            register.setIsDelete(YesOrNoState.NO.getState());
        }

        // 2. 用户信息
        SysUser sysUser = sysUserService.findByPhone(req.getPhone());
        if (ObjectUtil.isNull(sysUser)) {
            sysUser = new SysUser();
            /*sysUser.setStatus(2); // 用户状态为注册*/
            // 2022-05-12 改为注册自动审核通过
            sysUser.setStatus(1);
            sysUser.setCreateTime(new Date());
            sysUser.setIsDelete(YesOrNoState.NO.getState());
            BeanUtil.copyProperties(register, sysUser, "id");
            sysUser.setSalt(DigestUtil.md5Hex(register.getUserName()));
            Md5PasswordEncoder md5PasswordEncoder = Md5PasswordEncoder.getInstance();
            md5PasswordEncoder.setSalt(sysUser.getSalt());
            sysUser.setPassword(md5PasswordEncoder.encode(StringUtils.isEmpty(req.getPassword()) ? SysUser.DEFAULT_PASSWORD : req.getPassword()));
            sysUserService.save(sysUser);
        }
        //如果是已删除用户，修改删除状态为正常
        if (sysUser.getIsDelete() == YesOrNoState.YES.getState()) {
            sysUser.setIsDelete(YesOrNoState.NO.getState());
            sysUserService.updateById(sysUser);
        }

        // 3. 人员信息
        Person person = personService.findByPhone(req.getPhone());
        if (ObjectUtil.isNull(person)) {
            person = new Person();
            person.setUserId(sysUser.getId());
            /*person.setState(2); // 人员状态为注册*/
            // 2022-05-12 改为注册自动审核通过
            person.setState(1);
            person.setIsAdmin(2);
            // 设置注册时间
            person.setRegisterDate(register.getCreateTime());
            person.setCreateTime(new Date());
            person.setIsDelete(YesOrNoState.NO.getState());
        }
        BeanUtil.copyProperties(sysUser, person, "id");
        person.setOrgId(register.getOrgId());
        //如果是已删除用户，修改删除状态为正常
        if (person.getIsDelete() == YesOrNoState.YES.getState()) {
            person.setIsDelete(YesOrNoState.NO.getState());
        }

        //personService.checkParam(person, false);
        personService.saveOrUpdate(person);
        // 保存学员角色信息
        if (sysUserRoleService.findRoleListByUserId(sysUser.getId()).isEmpty()) {
            sysUserRoleService.save(sysUser.getId(), CollectionUtil.newArrayList(SysRole.STUDENT_ROLE));
        }
        // 记录人员变动
        personChangeLogService.executeSave(sysUser.getId(), PersonChangeType.REGISTER.getReasonDesc(), person.getOrgId(), JSONObject.toJSONString(person), sysUser.getId());

        register.setPersonId(person.getId());
        this.saveOrUpdate(register);
        return register;
    }

    public PersonRegister findByUnionid(String unionid) {
        return baseMapper.findByUnionid(unionid);
    }

    public PersonRegister findByPhone(String phone) {
        return baseMapper.findByPhone(phone);
    }

    /**
     * 根据用户名查询注册信息
     */
    public PersonRegister getByUserName(String userName) {
        LambdaQueryWrapper<PersonRegister> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonRegister::getUserName, userName);
        queryWrapper.eq(PersonRegister::getIsDelete, YesOrNoState.NO.getState());
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据人员ID查询注册信息
     *
     * @param personId
     * @return
     */
    public List<PersonRegister> findByPersonId(Long personId) {
        List<PersonRegister> personRegistersList = baseMapper.findByPersonId(personId);
        return personRegistersList;
    }

    /**
     * 根据人员ID删除注册信息
     *
     * @param personId
     */
    public void delByPersonId(Long personId, String deleteReason) {
        List<PersonRegister> personRegisterList = findByPersonId(personId);
        for (PersonRegister personRegister : personRegisterList) {
            personRegister.setIsDelete(YesOrNoState.YES.getState());
            personRegister.setDeleteBy(SecurityUtil.getUserId());
            personRegister.setDeleteTime(new Date());
            personRegister.setDeleteReason(deleteReason);

            updateById(personRegister);
        }
    }
}
