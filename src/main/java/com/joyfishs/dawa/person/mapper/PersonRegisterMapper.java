package com.joyfishs.dawa.person.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.entity.PersonRegister;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PersonRegisterMapper extends BaseMapper<PersonRegister> {

    List<Person> queryList(Long orgId);

    @Select("select * from xm_person_register where person_id = #{personId} and is_delete = 0")
    List<PersonRegister> findByPersonId(@Param("personId") Long personId);

    @Select("select * from xm_person_register where unionid = #{unionid} and is_delete = 0 order by create_time desc limit 1")
    public PersonRegister findByUnionid(@Param("unionid") String unionid);

    @Select("select * from xm_person_register where phone = #{phone} order by create_time desc limit 1")
    public PersonRegister findByPhone(@Param("phone") String phone);
}
