package com.joyfishs.dawa.notice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.notice.entity.NoticePerson;

@Mapper
public interface NoticePersonMapper extends BaseMapper<NoticePerson> {

    @Select("select a.*,b.name as person_name,d.name as org_name,c.avatar " +
            "from xm_notice_person a " +
            "left join xm_person b on a.person_id = b.id " +
            "left join sys_user c on a.user_id = c.id " +
            "left join sys_org d on b.org_id = d.id " +
            "where a.notice_id = #{noticeId} " +
            "and a.is_read = 1 " +
            "order by a.read_time desc ")
    List<NoticePerson> listReadByNoticeId(@Param("noticeId") Long noticeId);

    @Select("select p.id as person_id,p.name as person_name,o.name as org_name " +
            "from xm_person p  " +
            "left join sys_org o on p.org_id = o.id  " +
            "where p.is_delete = 0 " +
            "and exists (select 1 from xm_notice n where n.id = #{noticeId}  " +
            "      and (p.org_id = n.range_id or o.pids like concat('%[', range_id ,']%') )  " +
            "      ) " +
            "and not exists (select 1 from xm_notice_person where notice_id = #{noticeId} and person_id = p.id and is_read = 1) ")
    List<NoticePerson> listUnreadByNoticeId(@Param("noticeId") Long noticeId);

    /**
     * 消息接收，修改为已读
     */
    /*@Update("update xm_notice_person set is_read = 1, read_time = now() where notice_id = #{noticeId} and user_id = #{userId} ")
    int updateRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);*/

    @Select("select * from xm_notice_person where notice_id = #{noticeId} and person_id = #{personId} limit 1 ")
    NoticePerson getByNoticeIdAndPersonId(@Param("noticeId") Long noticeId, @Param("personId") Long personId);
}
