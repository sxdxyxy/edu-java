package com.joyfishs.dawa.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.notice.entity.NoticePerson;
import com.joyfishs.dawa.notice.mapper.NoticePersonMapper;

@Service
public class NoticePersonService extends ServiceImpl<NoticePersonMapper, NoticePerson> {

    // 根据公告查询人员
    public List<NoticePerson> listByNoticeId(Long noticeId) {
        LambdaQueryWrapper<NoticePerson> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoticePerson::getNoticeId, noticeId);
        return list(queryWrapper);
    }

    // 根据公告删除人员
    public void deleteByNoticeId(Long noticeId) {
        LambdaQueryWrapper<NoticePerson> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticePerson::getNoticeId, noticeId);
        baseMapper.delete(wrapper);
    }
}
