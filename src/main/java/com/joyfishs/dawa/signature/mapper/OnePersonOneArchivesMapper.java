package com.joyfishs.dawa.signature.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.signature.entity.OnePersonOneArchives;

@Mapper
public interface OnePersonOneArchivesMapper extends BaseMapper<OnePersonOneArchives> {

    String maxRoundNumber();
}
