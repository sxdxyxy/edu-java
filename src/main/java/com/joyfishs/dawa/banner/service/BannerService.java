package com.joyfishs.dawa.banner.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.joyfishs.dawa.banner.domain.vo.BannerVo;
import com.joyfishs.dawa.banner.entity.Banner;
import com.joyfishs.dawa.banner.mapper.BannerMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;

@Service
public class BannerService extends ServiceImpl<BannerMapper, Banner> {
    public List<Banner> queryPageList() {
        LambdaQueryWrapper<Banner> lqw = Wrappers.lambdaQuery();
        lqw.eq(Banner::getIsDelete, Boolean.FALSE);
        List<Banner> result = baseMapper.selectList(lqw);
        return result;
    }
    public Banner queryById(Long bannerId){
        return baseMapper.selectById(bannerId);
    }

    /**
     * 根据 ID 查询
     */
    public BannerVo selectVoById(Long id) {
        Banner obj = baseMapper.selectById(id);
        if (ObjectUtil.isNull(obj)) {
            return null;
        }
        BannerVo result =new BannerVo();
        BeanUtil.copyProperties(obj,result);
        return result;
    }

    public boolean del(Long id) {
        Banner obj = getById(id);
        if (ObjectUtil.isEmpty(obj)) {
            throw new CustomException("要删除的记录不存在");
        }
        obj.setIsDelete(YesOrNoState.YES.getState());
        obj.setDeleteBy(SecurityUtil.getUserId());
        obj.setDeleteTime(new Date());
        return updateById(obj);
    }

    public List<BannerVo> queryList() {
        LambdaQueryWrapper<Banner> lqw = Wrappers.lambdaQuery();
        lqw.eq(Banner::getIsDelete, Boolean.FALSE);
        lqw.eq(Banner::getPublish, Boolean.TRUE);
        lqw.le(Banner::getStartTime, LocalDateTime.now());
        lqw.and(wrapper -> wrapper.isNull(Banner::getEndTime).or().gt(Banner::getEndTime, LocalDateTime.now()));
        List<Banner> list = baseMapper.selectList(lqw);
        List<BannerVo> result = Lists.newArrayList();
        list.forEach(item -> {
            BannerVo vo = new BannerVo();
            BeanUtil.copyProperties(item, vo);
            result.add(vo);
        });
        return result;
    }
}
