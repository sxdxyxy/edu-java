package com.joyfishs.dawa.course.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.course.entity.CourseTag;
import com.joyfishs.dawa.course.mapper.CourseTagMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiaodai
 * @create 2021/11/15 19:00
 */

@Slf4j
@Service
public class CourseTagService extends ServiceImpl<CourseTagMapper, CourseTag> {

    /**
     * 保存标签，课程和词典的value
     * @param courseId  课程ID
     * @param value 词典的value值
     */
    public void saveTag(Long courseId, Integer value) {
        CourseTag tag = new CourseTag();
        tag.setCourseId(courseId);
        tag.setDictValue(value);
        tag.setCreateTime(new Date());
        tag.setCreateBy(SecurityUtil.getUserId());
        tag.setIsDelete(YesOrNoState.NO.getState());
        save(tag);
    }

    /**
     * 通过课程ID删除关联标签数据
     * @param courseId
     */
    public void delTagByCourseId(Long courseId){
        baseMapper.getUpdateByCourseId(courseId);
    }
}
