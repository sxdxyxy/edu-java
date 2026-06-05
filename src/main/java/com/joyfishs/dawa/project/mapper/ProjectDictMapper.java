package com.joyfishs.dawa.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.project.entity.ProjectDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity gen.domain.XmProjectDict
 */
@Mapper
public interface ProjectDictMapper extends BaseMapper<ProjectDict> {


    /**
     * 查询种类列表
     *
     * @param code
     * @param type
     * @return
     */
    List<ProjectDict> findList(@Param("code") String code, @Param("type") Integer type);

    /**
     * 根据名称查询种类是否存在
     *
     * @param name 种类名称
     * @param type 类型
     * @return 存在的记录数
     */
    @Select("select count(1) from xm_project_dict where is_delete = 0 and name = #{name} and type = #{type}")
    int countByName(@Param("name") String name, @Param("type") Integer type);
}
