package com.joyfishs.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.system.entity.SysDataDictionaryItem;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.mapper.SysDataDictionaryItemMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SysDataDictionaryItemService extends ServiceImpl<SysDataDictionaryItemMapper, SysDataDictionaryItem> {

    /**
     * 新增或修改词典项
     *
     * @param sysDataDictionaryItem
     * @return
     */
    @Override
    public boolean saveOrUpdate(SysDataDictionaryItem sysDataDictionaryItem){
        if(StringUtils.isNull(sysDataDictionaryItem.getId())){
            //校验参数，检查是否存在相同的名称和值
            checkParam(sysDataDictionaryItem, false);
            // 新增
            if(null == sysDataDictionaryItem.getSortid()) {
                sysDataDictionaryItem.setSortid(findMaxSortidByCode(sysDataDictionaryItem.getDictionaryCode()));
            }
            sysDataDictionaryItem.setCreateBy(SecurityUtil.getUserId());
            sysDataDictionaryItem.setCreateTime(new Date());
            sysDataDictionaryItem.setIsDelete(YesOrNoState.NO.getState());
            return save(sysDataDictionaryItem);
        } else {
            //校验参数，检查是否存在相同的名称和值
            checkParam(sysDataDictionaryItem, true);

            // 修改
            SysDataDictionaryItem sysDataDictionaryItemOld = getById(sysDataDictionaryItem.getId());
            if(sysDataDictionaryItemOld == null || StringUtils.isNull(sysDataDictionaryItemOld.getId())) {
                throw new CustomException("未找到词典项");
            }

            sysDataDictionaryItem.setCreateBy(null);
            sysDataDictionaryItem.setCreateTime(null);
            sysDataDictionaryItem.setIsDelete(null);
            sysDataDictionaryItem.setUpdateBy(SecurityUtil.getUserId());
            sysDataDictionaryItem.setUpdateTime(new Date());
            return updateById(sysDataDictionaryItem);
        }
    }

    /**
     * 校验参数，检查是否存在相同的名称
     */
    private void checkParam(SysDataDictionaryItem dictionary, boolean isExcludeSelf) {
        Long id = dictionary.getId();
        String name = dictionary.getName();
        Integer value = dictionary.getValue();

        LambdaQueryWrapper<SysDataDictionaryItem> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysDataDictionaryItem::getName, name)
                .eq(SysDataDictionaryItem::getDictionaryCode, dictionary.getDictionaryCode())
                .eq(SysDataDictionaryItem::getIsDelete, YesOrNoState.NO.getState());

        LambdaQueryWrapper<SysDataDictionaryItem> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysDataDictionaryItem::getValue, value)
                .eq(SysDataDictionaryItem::getDictionaryCode, dictionary.getDictionaryCode())
                .eq(SysDataDictionaryItem::getIsDelete, YesOrNoState.NO.getState());

        //是否排除自己，如果排除自己则不查询自己的id
        if (isExcludeSelf) {
            queryWrapperByName.ne(SysDataDictionaryItem::getId, id);
            queryWrapperByCode.ne(SysDataDictionaryItem::getId, id);
        }
        long countByName = this.count(queryWrapperByName);
        long countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new CustomException("名称重复，请检查名称参数");
        }
        if (countByCode >= 1) {
            throw new CustomException("词典项值重复，请检查词典项值参数");
        }
    }

    /**
     * 删除词典项
     *
     * @param idList
     * @param deleteReason
     * @return
     */
    public boolean del(List<Long> idList, String deleteReason){
        List<SysDataDictionaryItem> delList = new ArrayList<>();
        for (Long id : idList) {
            if(id == null) {
                continue;
            }

            SysDataDictionaryItem sysDataDictionaryItem = getById(id);
            if(sysDataDictionaryItem == null || sysDataDictionaryItem.getId() == null) {
                continue;
            }

            sysDataDictionaryItem.setIsDelete(YesOrNoState.YES.getState());
            sysDataDictionaryItem.setDeleteBy(SecurityUtil.getUserId());
            sysDataDictionaryItem.setDeleteTime(new Date());
            delList.add(sysDataDictionaryItem);
        }

        return updateBatchById(delList);
    }

    /**
     * 词典项查询
     *
     * @param sysDataDictionaryItem
     * @return
     */
    public List<SysDataDictionaryItem> findList(SysDataDictionaryItem sysDataDictionaryItem){
        if(StringUtils.isEmpty(sysDataDictionaryItem.getDictionaryCode())) {
            throw new CustomException("词典标识不能为空");
        }

        LambdaQueryWrapper<SysDataDictionaryItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDataDictionaryItem::getIsDelete, YesOrNoState.NO.getState())
                .eq(SysDataDictionaryItem::getDictionaryCode, sysDataDictionaryItem.getDictionaryCode());

        if(StringUtils.isNotEmpty(sysDataDictionaryItem.getName())) {
            queryWrapper.like(SysDataDictionaryItem::getName, sysDataDictionaryItem.getName());
        }
        if(StringUtils.isNotNull(sysDataDictionaryItem.getValue())) {
            queryWrapper.like(SysDataDictionaryItem::getValue, sysDataDictionaryItem.getValue());
        }

        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysDataDictionaryItem::getSortid);

        return list(queryWrapper);
    }

    /**
     * 词典项列表
     *
     * @param dictionaryCode
     * @return
     */
    public List<SysDataDictionaryItem> findList(String dictionaryCode){
        LambdaQueryWrapper<SysDataDictionaryItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDataDictionaryItem::getIsDelete, YesOrNoState.NO.getState())
                .eq(SysDataDictionaryItem::getDictionaryCode, dictionaryCode);
        queryWrapper.orderByAsc(SysDataDictionaryItem::getSortid);
        return list(queryWrapper);
    }

    /**
     * 根据词典标识获取该标识下的词典项最大显示顺序
     *
     * @param dictionaryCode
     * @return
     */
    private Integer findMaxSortidByCode(String dictionaryCode){
        Integer sortid = baseMapper.findMaxSortidByCode(dictionaryCode);
        if(StringUtils.isNull(sortid)) {
            sortid = 0;
        }
        sortid++;
        return sortid;
    }

    /**
     * 根据词典标识获取该标识下的词典项最大词典项目值
     *
     * @param dictionaryCode
     * @return
     */
    public Integer findMaxValueByCode(String dictionaryCode){
        Integer value = baseMapper.findMaxValueByCode(dictionaryCode);
        if(StringUtils.isNull(value)) {
            value = 0;
        }
        value++;
        return value;
    }


    public List<Dict> listByCode(String dictionaryCode) {
        //构造查询条件
        LambdaQueryWrapper<SysDataDictionaryItem> queryWrapper = new LambdaQueryWrapper<SysDataDictionaryItem>();
        queryWrapper.eq(SysDataDictionaryItem::getDictionaryCode, dictionaryCode)
                .eq(SysDataDictionaryItem::getIsDelete, YesOrNoState.NO.getState());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysDataDictionaryItem::getSortid);
        //查询dictTypeId下所有的字典项
        List<SysDataDictionaryItem> results = this.list(queryWrapper);

        //抽取code和value封装到map返回
        List<Dict> dictList = CollectionUtil.newArrayList();
        results.forEach(sysDictData -> {
            Dict dict = Dict.create();
            dict.put("id", sysDictData.getId());
            dict.put("name", sysDictData.getName());
            dict.put("value", sysDictData.getValue());
            dict.put("parentid", sysDictData.getParentid());
            dictList.add(dict);
        });

        return dictList;
    }

    /**
     * 通过课程的
     * @param dictonaryCode 字典code
     * @param value 字典value
     * @return
     */
    public  String findDictonaryBydictonaryCode(String dictonaryCode,Integer value){
       return baseMapper.findDictonaryBydictonaryCode(dictonaryCode,value);
    }

    public SysDataDictionaryItem getByCodeAndName(String dictionaryCode, String name) {
        LambdaQueryWrapper<SysDataDictionaryItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDataDictionaryItem::getDictionaryCode, dictionaryCode);
        queryWrapper.eq(SysDataDictionaryItem::getName, name);
        queryWrapper.eq(SysDataDictionaryItem::getIsDelete, YesOrNoState.NO.getState());
        return baseMapper.selectOne(queryWrapper);
    }

    public SysDataDictionaryItem getByCodeAndValue(String dictionaryCode, String value) {
        LambdaQueryWrapper<SysDataDictionaryItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDataDictionaryItem::getDictionaryCode, dictionaryCode);
        queryWrapper.eq(SysDataDictionaryItem::getValue, value);
        queryWrapper.eq(SysDataDictionaryItem::getIsDelete, YesOrNoState.NO.getState());
        return baseMapper.selectOne(queryWrapper);
    }
}
