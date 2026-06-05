package com.joyfishs.dawa.notice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.notice.domain.vo.NoticeVo;
import com.joyfishs.dawa.notice.entity.Notice;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    // 查询发布的通知
    List<NoticeVo> queryListCreate(@Param("userId") Long userId, @Param("type") Integer type, @Param("title") String title);

    // 查询接收的通知
    List<Notice> queryListReceive(@Param("personId") Long personId, @Param("title") String title);

    List<NoticeVo> findHomeNewsBulletin(@Param("personId") Long personId, @Param("type") Integer type, @Param("title") String title);

    List<NoticeVo> findHomeNewsBulletinByAnonymous(@Param("type") Integer type, @Param("title") String title);
}
