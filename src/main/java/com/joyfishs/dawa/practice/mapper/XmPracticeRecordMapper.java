package com.joyfishs.dawa.practice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.practice.entity.PracticeRecord;
import com.joyfishs.dawa.practice.vo.PracticeVo;

@Mapper
public interface XmPracticeRecordMapper extends BaseMapper<PracticeRecord> {

    List<PracticeRecord> findList(@Param("personId") Long personId);

    PracticeRecord getByPersonAndCourseId(@Param("personId") Long personId, @Param("courseId") Long courseId);

    List<PracticeVo> findPracticeList(@Param("type") String type, @Param("name") String name);
}
