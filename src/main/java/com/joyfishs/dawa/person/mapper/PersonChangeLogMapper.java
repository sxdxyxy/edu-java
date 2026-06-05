package com.joyfishs.dawa.person.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.person.entity.PersonChangeLog;

@Mapper
public interface PersonChangeLogMapper extends BaseMapper<PersonChangeLog> {

    @Select("select a.*,b.name as org_name from xm_person_change_log a " +
            "left join sys_org b on a.org_id = b.id " +
            "where a.user_id = #{userId} " +
            "and (a.org_id = #{orgId} or b.pids like concat('%[', #{orgId} ,']%') )")
    List<PersonChangeLog> queryList(@Param("userId") Long userId, @Param("orgId") Long orgId);
}
