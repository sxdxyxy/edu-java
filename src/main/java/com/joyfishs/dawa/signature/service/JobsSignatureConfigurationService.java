package com.joyfishs.dawa.signature.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.enums.WorkType;
import com.joyfishs.dawa.signature.domain.vo.JobsSignatureConfigurationVo;
import com.joyfishs.dawa.signature.entity.JobsSignatureConfiguration;
import com.joyfishs.dawa.signature.mapper.JobsSignatureConfigurationMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobsSignatureConfigurationService extends ServiceImpl<JobsSignatureConfigurationMapper, JobsSignatureConfiguration> {
    private final SysOrgService orgService;
    public JobsSignatureConfiguration queryById(Long configurationId) {
        return baseMapper.selectById(configurationId);
    }

    /**
     * 根据 ID 查询
     */
    public JobsSignatureConfigurationVo selectVoById(Long id) {
        JobsSignatureConfiguration obj = baseMapper.selectById(id);
        if (ObjectUtil.isNull(obj)) {
            return null;
        }
        JobsSignatureConfigurationVo result = new JobsSignatureConfigurationVo();
        BeanUtil.copyProperties(obj, result);
        return result;
    }

    public boolean del(Long id) {
        JobsSignatureConfiguration obj = getById(id);
        if (ObjectUtil.isEmpty(obj)) {
            throw new CustomException("要删除的记录不存在");
        }
        obj.setIsDelete(YesOrNoState.YES.getState());
        obj.setDeleteBy(SecurityUtil.getUserId());
        obj.setDeleteTime(new Date());
        return updateById(obj);
    }
    public List<JobsSignatureConfigurationVo> queryList(Long orgId, Integer workType, Long projectId) {
        LambdaQueryWrapper<JobsSignatureConfiguration> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobsSignatureConfiguration::getIsDelete, Boolean.FALSE);
        lqw.eq(ObjectUtil.isNotNull(orgId), JobsSignatureConfiguration::getOrgId, orgId);
        lqw.eq(ObjectUtil.isNotNull(projectId), JobsSignatureConfiguration::getProjectId, projectId);
        lqw.eq(ObjectUtil.isNotNull(workType), JobsSignatureConfiguration::getWorkType, workType);
        List<JobsSignatureConfiguration> list = baseMapper.selectList(lqw);
        List<JobsSignatureConfigurationVo> result = Lists.newArrayList();
        if (ObjectUtil.isNotNull(workType)) {
            list.forEach(item -> {
                JobsSignatureConfigurationVo vo = new JobsSignatureConfigurationVo();
                BeanUtil.copyProperties(item, vo);
                result.add(vo);
            });
            return result;
        }
        Multimap<Integer, String> multimap = ArrayListMultimap.create();
        list.forEach(item -> {
            multimap.put(item.getWorkType(), item.getDocumentName());
        });
        multimap.keySet().forEach(jt -> {
            Collection<String> documentNames = multimap.get(jt);
            JobsSignatureConfigurationVo vo = new JobsSignatureConfigurationVo();
            vo.setWorkType(jt);
            vo.setDocumentName(CollUtil.join(documentNames, "，"));
            result.add(vo);
        });
        Map<Integer, JobsSignatureConfigurationVo> resultMap = result.stream().collect(Collectors.toMap(JobsSignatureConfigurationVo::getWorkType, vo -> vo));
        for (WorkType type : WorkType.values()) {
            if (!resultMap.containsKey(type.getIndex())) {
                JobsSignatureConfigurationVo vo = new JobsSignatureConfigurationVo();
                vo.setWorkType(type.getIndex());
                vo.setDocumentName("未配置");
                vo.setWorkTypeName(type.getDesc());
                resultMap.put(type.getIndex(), vo);
            } else {
                resultMap.get(type.getIndex()).setWorkTypeName(type.getDesc());
            }
        }
        return Lists.newArrayList(resultMap.values());
    }
    public List<JobsSignatureConfiguration> queryList(Long orgId, Integer workType, List<Long> ids) {
        SysOrg org = orgService.getById(orgId);
        List<Long> orgIds = org.getPidList();
        orgIds.add(orgId);
        orgIds = orgIds.stream().filter(item -> item.longValue() > 0).collect(Collectors.toList());
        LambdaQueryWrapper<JobsSignatureConfiguration> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobsSignatureConfiguration::getIsDelete, Boolean.FALSE);
        lqw.in(JobsSignatureConfiguration::getOrgId, orgIds);
        lqw.eq(JobsSignatureConfiguration::getWorkType, workType);
        lqw.notIn(CollUtil.isNotEmpty(ids),JobsSignatureConfiguration::getId, ids);
        return baseMapper.selectList(lqw);
    }
}
