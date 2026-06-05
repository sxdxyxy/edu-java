package com.joyfishs.dawa.project.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.project.entity.ProjectDict;
import com.joyfishs.dawa.project.mapper.ProjectDictMapper;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.utils.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 项目关联的数据表  1-项目类型
 *
 * @author ykfnb
 */
@Service
public class ProjectDictService extends ServiceImpl<ProjectDictMapper, ProjectDict> {

    /**
     * 删除种类
     *
     * @param id
     * @return
     */
    public boolean del(Long id) {
        ProjectDict obj = getById(id);
        obj.setIsDelete(YesOrNoState.YES.getState());
        obj.setDeleteBy(SecurityUtil.getUserId());
        obj.setDeleteTime(new Date());
        return updateById(obj);
    }


    /**
     * 查询属性列表
     *
     * @param code
     * @param type
     * @return
     */
    public List<ProjectDict> getList(String code, Integer type) {
        return baseMapper.findList(code, type);
    }

    /**
     * 检查种类名称是否已存在
     *
     * @param name 种类名称
     * @param type 类型
     * @return true-存在，false-不存在
     */
    public boolean isNameExists(String name, Integer type) {
        return baseMapper.countByName(name, type) > 0;
    }
}
