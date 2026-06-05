package com.joyfishs.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.entity.SysDataDictionary;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.mapper.SysDataDictionaryMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysDataDictionaryService extends ServiceImpl<SysDataDictionaryMapper, SysDataDictionary> {

    /**
     * 新增或修改词典分类
     *
     * @param sysDataDictionary
     * @return
     */
    public boolean saveOrUpdate(SysDataDictionary sysDataDictionary){
        log.info("SysDataDictionaryService - saveOrUpdate sysDataDictionary:{}", sysDataDictionary);

        if(sysDataDictionary.getId() == null){
            //校验参数，检查是否存在相同的名称和编码
            checkParam(sysDataDictionary, false);
            // 新增词典分类
            sysDataDictionary.setDictionaryCode(findDictionaryCode());
            sysDataDictionary.setCreateBy(SecurityUtil.getUserId());
            sysDataDictionary.setCreateTime(new Date());
            sysDataDictionary.setIsDelete(YesOrNoState.NO.getState());
            log.info("SysDataDictionaryService - saveOrUpdate save sysDataDictionary:{}", sysDataDictionary);

            return save(sysDataDictionary);
        }else{
            //校验参数，检查是否存在相同的名称和编码
            checkParam(sysDataDictionary, true);

            // 修改词典分类
            SysDataDictionary sysDataDictionaryOld = getById(sysDataDictionary.getId());
            if(sysDataDictionaryOld == null || sysDataDictionaryOld.getId() == null)
                throw new CustomException("未找到词典分类");

            sysDataDictionaryOld.setDictionaryName(sysDataDictionary.getDictionaryName());
            sysDataDictionaryOld.setUpdateBy(SecurityUtil.getUserId());
            sysDataDictionaryOld.setUpdateTime(new Date());
            log.info("SysDataDictionaryService - saveOrUpdate save sysDataDictionary:{}", sysDataDictionaryOld);

            return updateById(sysDataDictionaryOld);
        }

    }

    /**
     * 校验参数，检查是否存在相同的名称
     */
    private void checkParam(SysDataDictionary dictionary, boolean isExcludeSelf) {
        Long id = dictionary.getId();
        String name = dictionary.getDictionaryName();
        String code = dictionary.getDictionaryCode();

        LambdaQueryWrapper<SysDataDictionary> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysDataDictionary::getDictionaryName, name)
                .eq(SysDataDictionary::getIsDelete, YesOrNoState.NO.getState());

        LambdaQueryWrapper<SysDataDictionary> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysDataDictionary::getDictionaryCode, code)
                .eq(SysDataDictionary::getIsDelete, YesOrNoState.NO.getState());

        //是否排除自己，如果排除自己则不查询自己的id
        if (isExcludeSelf) {
            queryWrapperByName.ne(SysDataDictionary::getId, id);
            queryWrapperByCode.ne(SysDataDictionary::getId, id);
        }
        long countByName = this.count(queryWrapperByName);
        long countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new CustomException("名称重复，请检查名称参数");
        }
        if (countByCode >= 1) {
            throw new CustomException("编码重复，请检查编码参数");
        }
    }

    /**
     * 删除词典分类
     *
     * @param idList
     * @param deleteReason
     * @return
     */
    public boolean del(List<Long> idList, String deleteReason){
        log.info("SysDataDictionaryService - del idList:{}", idList);
        log.info("SysDataDictionaryService - del deleteReason:{}", deleteReason);

        List<SysDataDictionary> delList = new ArrayList<>();
        for (Long id : idList) {
            if(id == null) continue;

            SysDataDictionary sysDataDictionary = getById(id);
            if(sysDataDictionary == null || sysDataDictionary.getId() == null) continue;

            sysDataDictionary.setIsDelete(YesOrNoState.YES.getState());
            sysDataDictionary.setDeleteBy(SecurityUtil.getUserId());
            sysDataDictionary.setDeleteTime(new Date());
            log.info("SysDataDictionaryService - del sysDataDictionary:{}", sysDataDictionary);

            delList.add(sysDataDictionary);
        }

        return updateBatchById(delList);
    }

    /**
     * 词典分类查询
     *
     * @param sysDataDictionary
     * @return
     */
    public List<SysDataDictionary> findList(SysDataDictionary sysDataDictionary){
        log.info("SysDataDictionaryService - findList sysDataDictionary:{}", sysDataDictionary);

        LambdaQueryWrapper<SysDataDictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDataDictionary::getIsDelete, YesOrNoState.NO.getState());

        if(StringUtils.isNotEmpty(sysDataDictionary.getDictionaryCode()))
            queryWrapper.like(SysDataDictionary::getDictionaryCode, sysDataDictionary.getDictionaryCode());
        if(StringUtils.isNotEmpty(sysDataDictionary.getDictionaryName()))
            queryWrapper.like(SysDataDictionary::getDictionaryName, sysDataDictionary.getDictionaryName());

        return list(queryWrapper);
    }

    /**
     * 获取词典标识
     *
     * @return
     */
    private String findDictionaryCode(){
        long count = count();

        return String.format("%04d", count+1);
    }

}
