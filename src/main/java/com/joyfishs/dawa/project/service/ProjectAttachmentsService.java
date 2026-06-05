package com.joyfishs.dawa.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.project.entity.ProjectAttachments;
import com.joyfishs.dawa.project.mapper.ProjectAttachmentsMapper;

/**
 * 项目附件服务
 *
 * @author ykf
 */
@Service
public class ProjectAttachmentsService extends ServiceImpl<ProjectAttachmentsMapper, ProjectAttachments> {
    public List<ProjectAttachments> findList(Long projectId) {
        return baseMapper.selectList(new LambdaQueryWrapper<ProjectAttachments>().eq(ProjectAttachments::getProjectId, projectId));
    }

    public boolean saveBy(ProjectAttachments attachment) {
        return this.save(attachment);
    }
}
