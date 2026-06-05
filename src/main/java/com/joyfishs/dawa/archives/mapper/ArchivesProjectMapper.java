package com.joyfishs.dawa.archives.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.archives.domain.ArchivesProject;
import com.joyfishs.dawa.archives.domain.ArchivesProjectUser;

@Mapper
public interface ArchivesProjectMapper extends BaseMapper<ArchivesProject> {


    /**
     * 查询项目档案列表
     * @param trainClass 培训种类
     * @param trainWay 培训方式
     * @param projectName 项目名称
     * @param projectId 项目ID
     * @return
     */
    List<ArchivesProject> findArchiveProjectList(@Param("trainClass") Integer trainClass, @Param("trainWay") Integer trainWay,@Param("projectName") String projectName, @Param("projectId") Long projectId);


    /**
     * 查询人员记录
     * @param projectId 项目ID
     * @param state 状态 全部，合格，不合格，未考试
     * @param userName 用户名称
     * @return
     */
    List<ArchivesProjectUser> findUserListPage(@Param("projectId") Long projectId, @Param("state") Integer state, @Param("userName") String userName);
}
