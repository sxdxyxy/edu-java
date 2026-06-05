package com.joyfishs.sms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.sms.domain.SysSms;

@Mapper
public interface SysSmsMapper extends BaseMapper<SysSms> {

    @Select("select count(1) from sys_sms where phone = #{phone} and type = #{type} and create_time >= #{createTime}")
    public int findByPhone(@Param("phone") String phone, @Param("type") String type, @Param("createTime") String createTime);

    @Select("select * from sys_sms where phone = #{phone} and type = #{type} and code = #{code} and is_use = 0 and create_time >= #{createTime}" +
            " order by create_time desc limit 1")
    public SysSms findByUserIdAndCode(@Param("phone") String phone, @Param("type") String type, @Param("code") String code, @Param("createTime") String createTime);

}
