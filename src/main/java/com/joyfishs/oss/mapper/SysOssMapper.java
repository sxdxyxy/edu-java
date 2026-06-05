package com.joyfishs.oss.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.oss.entity.SysOss;

@Mapper
public interface SysOssMapper extends BaseMapper<SysOss> {

    @Select("SELECT * FROM sys_oss WHERE url = #{fileUrl}")
    SysOss findByUrl(@Param("fileUrl") String fileUrl);
}
