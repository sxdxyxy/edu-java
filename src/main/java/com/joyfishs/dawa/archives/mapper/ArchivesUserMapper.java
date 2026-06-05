package com.joyfishs.dawa.archives.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.archives.domain.*;

@Mapper
public interface ArchivesUserMapper extends BaseMapper<ArchivesUser> {

    /**
     * 查询人员档案列表
     */
    List<ArchivesUser> queryList(@Param("orgId") Long orgId, @Param("projectId") Long projectId, @Param("name") String name);

    /**
     * 查询人员档案-自主培训列表
     */
    List<ArchivesUserAutoTrain> queryListAutoTrain(@Param("personId") Long personId, @Param("projectName") String projectName, @Param("finishState") Integer finishState);

    /**
     * 查询自主培训-培训详情列表
     */
    List<ArchivesUserAutoTrainDetail> queryListAutoTrainDetail(@Param("personId") Long personId, @Param("projectId") Long projectId, @Param("courseName") String courseName);

    /**
     * 查询自主培训-答题记录列表
     */

    List<ArchivesUserAutoTrainAnswer> queryListAutoTrainAnswer(@Param("personId") Long personId, @Param("projectId") Long projectId);

    /**
     * 查询人员档案-终端培训列表
     */
    List<ArchivesUserTerminalTrain> queryListTerminalTrain(@Param("personId") Long personId, @Param("projectName") String projectName);
}
