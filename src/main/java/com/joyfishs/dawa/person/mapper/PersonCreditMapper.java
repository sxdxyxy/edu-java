package com.joyfishs.dawa.person.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.person.domain.vo.PersonCreditVo;
import com.joyfishs.dawa.person.entity.PersonCredit;

/**
 * @author yangkaifeng
 */
@Mapper
public interface PersonCreditMapper extends BaseMapper<PersonCredit> {

    List<PersonCreditVo> selectByPage(@Param("name") String name, @Param("orgId") Long orgId);
}
