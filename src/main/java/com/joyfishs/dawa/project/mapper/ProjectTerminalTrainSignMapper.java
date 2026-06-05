package com.joyfishs.dawa.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.project.domain.vo.ProjectTerminalTrainSignVo;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrainSign;

/**
* @Entity gen.domain.XmProjectTerminalTrainSign
*/
@Mapper
public interface ProjectTerminalTrainSignMapper extends BaseMapper<ProjectTerminalTrainSign> {

	/**
	 * 查询终端培训报到
	 * @param trainId
	 * @param personId
	 * @return
	 */
    ProjectTerminalTrainSign getSign(@Param("trainId") Long trainId, @Param("personId") Long personId);

	/** 获取签到列表 */

	List<ProjectTerminalTrainSignVo> getRegisteredList(@Param("id") Long id);

	/** 获取签到或者报名数量 */
	Long getSignNum(@Param("id") Long id,@Param("type") Integer type);

	List<ProjectTerminalTrainSignVo> getTerminalTrainByPersonId(@Param("personId") Long personId);
}
