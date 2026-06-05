package com.joyfishs.dawa.utils;

import com.joyfishs.system.entity.SysDataDictionaryItem;
import com.joyfishs.system.service.SysDataDictionaryItemService;
import com.joyfishs.utils.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Dictionary {

    /**
     * 根据字典Code和Value获取字典项详情
     * 走redis缓存
     *
     * @param code
     * @param value
     * @return
     */
    public static SysDataDictionaryItem getDictionaryItem(String code, String value) {
        try {
            SysDataDictionaryItem item = SpringUtil.getBean(SysDataDictionaryItemService.class).getByCodeAndValue(code, value);

            if(item == null) item = new SysDataDictionaryItem();

            return item;
        } catch (Exception err) {
            log.error("获取字典缓存出错:", err);
            return new SysDataDictionaryItem();
        }
    }

}
