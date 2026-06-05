package com.joyfishs.dawa.person.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.person.entity.EmergencyContacts;

@Mapper
public interface EmergencyContactsMapper extends BaseMapper<EmergencyContacts> {

    @Select(" select * from xm_person_emergency_contacts where person_id = #{personId}")
    List<EmergencyContacts> listByPersonId(Long personId);
}
