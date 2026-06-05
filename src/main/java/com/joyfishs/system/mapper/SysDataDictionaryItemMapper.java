package com.joyfishs.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.system.entity.SysDataDictionaryItem;

@Mapper
public interface SysDataDictionaryItemMapper extends BaseMapper<SysDataDictionaryItem> {

    @Select("select max(sortid) from sys_data_dictionary_item where dictionary_code = #{dictionaryCode}")
    public Integer findMaxSortidByCode(@Param("dictionaryCode") String dictionaryCode);

    @Select("select max(value) from sys_data_dictionary_item where dictionary_code = #{dictionaryCode}")
    public Integer findMaxValueByCode(@Param("dictionaryCode") String dictionaryCode);


    /**
     * 通过名称和code 查询词典项
     * @param name
     * @param dictionaryCode
     * @return 返回一条数据
     */
    @Select("SELECT * FROM sys_data_dictionary_item WHERE name=#{name} AND dictionary_code=#{dictionaryCode} ")
    SysDataDictionaryItem findByNameAndCode(@Param("name") String name, String dictionaryCode);

    /**
     * 通过值和code 查询词典项
     * @param value
     * @param dictionaryCode
     * @return
     */
    @Select("SELECT * FROM sys_data_dictionary_item WHERE value=#{value} AND dictionary_code=#{dictionaryCode} ")
    SysDataDictionaryItem findByValueAndCode(@Param("value") int value, String dictionaryCode);

    @Select("select name from sys_data_dictionary_item where dictionary_code = #{dictonaryCode} and value = #{value} and is_delete=0")
    String findDictonaryBydictonaryCode(@Param("dictonaryCode") String dictonaryCode, @Param("value") Integer value);
}
