package com.joyfishs.dawa.message.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.message.entity.Message;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询创建时间小于等于当前时间的条数，有消息时提前存进表中的
     * @param personId
     * @return
     */
    @Select(" select * from xm_message where person_id = #{personId} and is_delete = 0 and create_time <= now() " +
            " order by create_time desc ")
    List<Message> queryList(@Param("personId") Long personId);

    /**
     * 根据personId查询未读消息总数
     */
    @Select("select count(1) as total " +
            "from xm_message a " +
            "where a.person_id = #{personId} " +
            "and a.is_read = 0 " +
            "and a.is_delete = 0")
    Integer getMsgCount(@Param("personId") Long personId);

    /**
     * 消息接收，修改为已读
     */
    @Update("update xm_message set is_read = 1 where id = #{id}")
    int updateRead(@Param("id") Long id);

}
