package com.joyfishs.dawa.project.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.project.domain.vo.ProjectList;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;

/**
*/
@Mapper
public interface ProjectTerminalTrainMapper extends BaseMapper<ProjectTerminalTrain> {


	/**
	 * 删除终端培训
	 * @param idList
	 * @param userId
	 * @return
	 */
	int batchDelete(@Param("list") List<Long> idList,@Param("userId") Long userId);

	/**
	 * 查询终端培训列表
	 * @param status
	 * @param name
	 * @param startDate
	 * @param endDate
	 * @param projectId
	 * @return
	 */
	List<ProjectList> findList(@Param("orgIds") List<Long> orgIds, @Param("status") Integer status, @Param("name") String name, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("projectId") Long projectId);

	List<ProjectTerminalTrain> findByOrgId(@Param("personId") Long personId);

	/** 刷新终端培训状态 */
	@Update(" call refresh_focus_train_status() ")
	void refreshTrainStatus();
}
