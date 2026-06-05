package com.joyfishs.dawa.person.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.person.entity.PersonOrg;

@Mapper
public interface PersonOrgMapper extends BaseMapper<PersonOrg> {

    @Select("select GROUP_CONCAT(b.name) " +
            "from xm_person_org a " +
            "left join sys_org b on a.org_id = b.id " +
            "where a.person_id = #{personId} ")
    String getOrgNameByPersonId(Long personId);
}
