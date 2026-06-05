package com.joyfishs.dawa.signature.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.signature.entity.OnePersonOneArchives;
import com.joyfishs.dawa.signature.mapper.OnePersonOneArchivesMapper;
import com.joyfishs.utils.StringUtils;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.util.NumberUtil;

@Service
public class OnePersonOneArchivesService extends ServiceImpl<OnePersonOneArchivesMapper, OnePersonOneArchives> {

    private final static String PREFIX = "DA";
    private final static String ROUND_CODE= "001";

    public OnePersonOneArchives find(Long orgId, Long personId, Integer workType) {
        LambdaQueryWrapper<OnePersonOneArchives> lqw = Wrappers.lambdaQuery();
        lqw.eq(OnePersonOneArchives::getOrgId, orgId);
        lqw.eq(OnePersonOneArchives::getPersonId, personId);
        lqw.eq(OnePersonOneArchives::getWorkType, workType);
        return getOne(lqw);
    }

    /**
     * 编号生成规则
     * 1、如果当前时间没有核酸轮次，直接新增 ，默认为：DA-yyyyMMdd001
     * 2、查找当前那一天最大的编号，自增1
     *
     * @param code 当前最大编码
     */
    private static String getNextNum(String code) {
        String roundCode = ROUND_CODE;
        if (StringUtils.isNotNull(code) && NumberUtil.isInteger(code)) {
            int intCode = Integer.parseInt(code) + 1;
            if (intCode < 999) {
                roundCode = String.format(String.valueOf(intCode));
            } else {
                throw new CustomException("顺序编号达到最大");
            }
        }
        return new DecimalFormat("000").format(Integer.parseInt(roundCode));
    }

    public String getNumber() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String code = PREFIX + '-' + df.format(new Date());
        String maxNumber = baseMapper.maxRoundNumber();
        String newNumber = null;
        if (StringUtils.isEmpty(maxNumber)) {
            newNumber = code + ROUND_CODE;
        } else {
            //切割字符串，取查到编号的最后三位
            String getMaxCode = maxNumber.substring(11, 14);
            newNumber = code + getNextNum(getMaxCode);
        }
        return newNumber;
    }

}
