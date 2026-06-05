package com.joyfishs.dawa.course.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.course.domain.vo.CoursewareVo;
import com.joyfishs.dawa.course.entity.Courseware;

/**
 * <p>
 * 课件基本信息表 Mapper 接口
 * </p>
 *
 * @author xiaodai
 * @since 2021-08-24
 */
@Mapper
public interface CoursewareMapper extends BaseMapper<Courseware> {


    /**
     * 获取资源库列表
     * @param type
     * @return
     */
    List<CoursewareVo> findResourceList(@Param("type") Integer type);


    @Update("update xm_course_relation_courseware set is_delete = 0 and course_id = #{courseId}")
    void deleteCoursewarexmRelationById(@Param("courseId") Long id);

    @Select("SELECT * FROM xm_courseware WHERE third_party_id = #{thirdPartyId} LIMIT 1")
    Courseware selectByThirdPartyId(@Param("thirdPartyId")Long thirdPartyId);
    // 增加 view_count 计数
    @Update("UPDATE xm_courseware SET view_count = view_count + 1 WHERE id = #{courseId}")
    void addViewCount(@Param("courseId") Long courseId);
}
