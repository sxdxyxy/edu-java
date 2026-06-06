package com.joyfishs.dawa.org.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.mapper.SysOrgMapper;
import com.joyfishs.oss.domain.UploadResult;
import com.joyfishs.oss.service.SysOssService;
import com.joyfishs.system.config.CosConfig;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;

@Service
@RequiredArgsConstructor
public class SysOrgService extends ServiceImpl<SysOrgMapper, SysOrg> {
    private final WxMaService wxMaService;
    private final SysOssService ossService;
    @Lazy
    @Autowired
    private PersonService personService;

    public List<SysOrg> queryList(SysOrg sysOrg) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrg::getIsDelete, YesOrNoState.NO.getState());
        queryWrapper.eq(SysOrg::getOrgType, sysOrg.getOrgType());

        if(!SecurityUtil.getLoginUser().ifAdmin()) {
            if (sysOrg.getOrgType() == 1) { // 查询单位范围,只能查看当前登录人所在单位及下级
                Long currentOrgId = SecurityUtil.getOrgId();
                queryWrapper.and(query -> query.eq(SysOrg::getCreateBy, SecurityUtil.getUserId()).or()
                        .eq(SysOrg::getId, currentOrgId).or().like(SysOrg::getPids, "[" + currentOrgId + "]"));
            } else if (sysOrg.getOrgType() == 2){ // 查询项目范围:可查看 1)自己创建的 2)自己参与的 3)自己所在单位及其所有下属单位创建的
                Long currentOrgId = SecurityUtil.getOrgId();
                // 当前用户所在单位的 pids 链(向上) + 单位自身 id 的列表
                // 用 sys_org 自己的 pids 字段做"祖先链路"匹配
                String currentOrgPidPath = "[" + currentOrgId + "]";
                // 通过 pids 包含 currentOrgId 的所有 org 视为"同单位体系"内的祖先单位
                // 然后再查这些祖先单位下属的所有 org_type=2 项目
                queryWrapper.and(query -> query.eq(SysOrg::getCreateBy, SecurityUtil.getUserId()).or()
                        .inSql(SysOrg::getId, "select org_id from xm_person_org where person_id = " + SecurityUtil.getPersonId()).or()
                        // 兜底:用户所在单位的所有下属项目(包含 pids 含当前单位 id 的项目,以及 pid 等于当前单位 id 的项目)
                        .like(SysOrg::getPids, currentOrgPidPath).or()
                        .eq(SysOrg::getPid, currentOrgId));
            }
        }

        //根据名称模糊查询
        if (StringUtils.isNotEmpty(sysOrg.getName())) {
            queryWrapper.like(SysOrg::getName, sysOrg.getName());
        }
        // 根据机构id查询
        if (ObjectUtil.isNotEmpty(sysOrg.getId())) {
            queryWrapper.eq(SysOrg::getId, sysOrg.getId());
        }

        // 根据父机构id查询
        if (ObjectUtil.isNotEmpty(sysOrg.getPid())) {
            queryWrapper.and(query -> query.eq(SysOrg::getId, sysOrg.getPid()).or().like(SysOrg::getPids, "[" + sysOrg.getPid() + "]"));

        }
        //根据创建时间排序
        queryWrapper.orderByAsc(SysOrg::getCreateTime);
        List<SysOrg> sysOrgList = this.list(queryWrapper);
        return sysOrgList;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean add(SysOrg sysOrg) {
        // 校验参数
        checkParam(sysOrg, false);

        // 设置新的pid
        String newPids = createNewPids(sysOrg.getPid());
        sysOrg.setPids(newPids);

        // 设置启用状态
        sysOrg.setCreateBy(SecurityUtil.getUserId());
        sysOrg.setCreateTime(new Date());
        sysOrg.setIsDelete(YesOrNoState.NO.getState());

        // 组织类型 为单位部门 或 项目部 时自动生成邀请码
        if (sysOrg.getOrgType() == 1 && (sysOrg.getType() == 1 || sysOrg.getType() == 2)) {
            sysOrg.setInvitationCode(StringUtils.getFixLenNumString(6));
        }
        return this.save(sysOrg);
    }

    public String createWxaCode(Long orgId) throws WxErrorException {
        SysOrg sysOrg = this.getById(orgId);
        if (ObjectUtil.isNull(sysOrg)) {
            return null;
        }
        // 如果没有邀请码，先生成
        if (StringUtils.isBlank(sysOrg.getInvitationCode())) {
            sysOrg.setInvitationCode(StringUtils.getFixLenNumString(6));
            this.updateById(sysOrg);
        }
        File qrCodeFile = wxMaService.getQrcodeService().createWxaCodeUnlimit(sysOrg.getInvitationCode(), "pages/login/index", true, "release", 430, true, null, false);
        String signedFileKey = (CosConfig.WXA_CODE + StrUtil.C_SLASH + DateUtil.format(new Date(), "yyyyMMdd") + StrUtil.C_SLASH + qrCodeFile.getName());
        UploadResult uploadResult = ossService.upload(qrCodeFile, signedFileKey);
        sysOrg.setWxaCodeUrl(uploadResult.getUrl());
        this.updateById(sysOrg);
        return uploadResult.getUrl();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean edit(SysOrg sysOrg) {
        // 校验参数
        checkParam(sysOrg, true);

        // 获取修改的机构的旧数据（库中的）
        SysOrg oldOrg = getById(sysOrg.getId());
        if(oldOrg == null || oldOrg.getId() == null) {
            throw new CustomException("未找到机构");
        }

        // 组织类型 为单位部门 类型 orgType为单位时 自动生成邀请码
        if (sysOrg.getOrgType() == 1 && sysOrg.getType() == 1 && (oldOrg.getOrgType() != 1 || oldOrg.getType() != 1)) {
            if (StrUtil.isEmpty(sysOrg.getInvitationCode())) {
                sysOrg.setInvitationCode(StringUtils.getFixLenNumString(6));
            }
        } else if (sysOrg.getOrgType() != 1 || sysOrg.getType() != 1){
           // sysOrg.setInvitationCode("");
        }

        // 本机构旧的pids
        Long oldPid = oldOrg.getPid();
        String oldPids = oldOrg.getPids();

        // 生成新的pid和pids
        Long newPid = sysOrg.getPid();
        String newPids = this.createNewPids(sysOrg.getPid());

        // 父节点有变化,更新子节点
        if (!newPid.equals(oldPid)) {
            // 查找所有叶子节点，包含子节点的子节点
            LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(SysOrg::getPids,  "[" + oldOrg.getId() + "]");
            List<SysOrg> list = this.list(queryWrapper);

            list.forEach(child -> {
                String oldParentCodesPrefix = oldPids + "[" + oldOrg.getId() + "]" + ",";
                String oldParentCodesSuffix = child.getPids().substring(oldParentCodesPrefix.length());
                // 子节点pids组成 = 当前机构新pids + 当前机构id + 子节点自己的pids后缀
                String orgParentCodes = newPids + "[" + oldOrg.getId() + "]" + "," + oldParentCodesSuffix;
                child.setPids(orgParentCodes);
            });
            this.updateBatchById(list);
        }

        // 设置新的pids
        sysOrg.setPids(newPids);
        return this.updateById(sysOrg);
    }

    /**
     * 校验参数
     */
    private void checkParam(SysOrg sysOrg, boolean isExcludeSelf) {
        // 如果是编辑机构时候，pid和id不能一致，一致会导致无限递归
        if (isExcludeSelf) {
            if (sysOrg.getId().equals(sysOrg.getPid())) {
                throw new CustomException("父级机构不能为当前节点，请重新选择父级机构");
            }

            // 如果是编辑，父id不能为自己的子节点
            List<Long> childIdListById = this.getChildIdListById(sysOrg.getId());
            if(ObjectUtil.isNotEmpty(childIdListById)) {
                if(childIdListById.contains(sysOrg.getPid())) {
                    throw new CustomException("父节点不能为本节点的子节点，请重新选择父节点");
                }
            }
        }

        // 判断单位是否挂在部门下面
        SysOrg parentOrg = this.getById(sysOrg.getPid());
        if (sysOrg.getType() == 1 && (null != parentOrg && parentOrg.getType() == 2)) {
            throw new CustomException("单位类型不能为部门类型子节点");
        }

        Long id = sysOrg.getId();
        String name = sysOrg.getName();
        String code = sysOrg.getCode();

        LambdaQueryWrapper<SysOrg> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysOrg::getName, name)
                .eq(SysOrg::getIsDelete, YesOrNoState.NO.getState());

        LambdaQueryWrapper<SysOrg> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysOrg::getCode, code)
                .eq(SysOrg::getIsDelete, YesOrNoState.NO.getState());

        if (isExcludeSelf) {
            queryWrapperByName.ne(SysOrg::getId, id);
            queryWrapperByCode.ne(SysOrg::getId, id);
        }
        long countByName = this.count(queryWrapperByName);
        long countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new CustomException("机构名称重复，请检查name参数");
        }
        if (countByCode >= 1) {
            throw new CustomException("机构编码重复，请检查code参数");
        }
    }
    /**
     * 创建pids的值
     * <p>
     * 如果pid是0顶级节点，pids就是 [0],
     * <p>
     * 如果pid不是顶级节点，pids就是 pid机构的pids + [pid] + ,
     */
    private String createNewPids(Long pid) {
        if (pid.equals(0L)) {
            return "[" + 0 + "]"
                    + ",";
        } else {
            //获取父机构
            SysOrg parentOrg = this.getById(pid);
            return parentOrg.getPids()
                    + "[" + pid + "]"
                    + ",";
        }
    }

    /**
     * 根据节点id获取所有子节点id集合
     */
    public List<Long> getChildIdListById(Long id) {
        List<Long> childIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SysOrg::getPids, "[" + id + "]");
        this.list(queryWrapper).forEach(sysOrg -> childIdList.add(sysOrg.getId()));
        return childIdList;
    }

    @Transactional
    public boolean del(Long id, String deleteReason){
        // 该机构下有人员，则不能删
        if (personService.count(Wrappers.<Person>lambdaQuery().eq(Person::getOrgId, id)) > 0) {
            throw new CustomException("该机构下有人员，不能删除");
        }
        //级联删除子节点
        List<Long> childIdList = this.getChildIdListById(id);
        childIdList.add(id);
        LambdaUpdateWrapper<SysOrg> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysOrg::getId, childIdList)
                .set(SysOrg::getIsDelete, YesOrNoState.YES.getState())
                .set(SysOrg::getDeleteBy, SecurityUtil.getUserId())
                .set(SysOrg::getDeleteTime, new Date())
                .set(SysOrg::getDeleteReason, deleteReason);
        boolean flag = this.update(updateWrapper);


        return flag;
    }

    /**
     * 根据orgId获取单位及部门名称
     * @param orgId
     * @return
     */
    public String[] getOrgUnitDept(Long orgId) {
        String[] orgArr = new String[2];
        if (orgId == null) {
            orgArr[0] = "";
            orgArr[1] = "";
            return orgArr;
        }
        SysOrg sysOrg = this.getById(orgId);
        if (sysOrg == null) {
            orgArr[0] = "";
            orgArr[1] = "";
            return orgArr;
        }
        if (sysOrg.getType() == 1) {
            // 如果组织为单位，则直接返回单位名称
            orgArr[0] = sysOrg.getName();
            orgArr[1] = "";
        } else {
            // 先查询出部门信息，再查询所属单位
            SysOrg parentOrg = this.findUnitByOrgId(sysOrg.getPid());
            orgArr[0] = parentOrg != null ? parentOrg.getName() : "";
            orgArr[1] = sysOrg.getName();
        }
        return orgArr;
    }

    /**
     * 通过orgId查询单位信息
     * @param orgId
     * @return
     */
    public SysOrg findUnitByOrgId(Long orgId) {
        SysOrg sysOrg = this.getById(orgId);
        if (ObjectUtil.isNull(sysOrg)) {
            return null;
        }
        if (sysOrg.getType() == 1) {
            // 如果组织为单位，则直接返回单位
            return sysOrg;
        }
        // 如果组织为部门，则根据pid查询单位
        return this.findUnitByOrgId(sysOrg.getPid());
    }

    /**
     * 通过name查询org
     */
    public SysOrg getByName(String orgName) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrg::getName, orgName);
        queryWrapper.eq(SysOrg::getIsDelete, YesOrNoState.NO.getState());
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 通过code查询org
     * @param code
     * @return
     */
    public SysOrg getByCode(String code) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrg::getCode, code);
        queryWrapper.eq(SysOrg::getIsDelete, YesOrNoState.NO.getState());
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据邀请码查询org
     */
    public SysOrg getByInvitationCode(String invitationCode) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrg::getInvitationCode, invitationCode);
        queryWrapper.eq(SysOrg::getIsDelete, YesOrNoState.NO.getState());
        return baseMapper.selectOne(queryWrapper);
    }
}
