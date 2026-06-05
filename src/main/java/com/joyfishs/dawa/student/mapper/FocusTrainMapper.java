package com.joyfishs.dawa.student.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joyfishs.dawa.student.domain.vo.FocusTrainVo;

@Mapper
public interface FocusTrainMapper extends BaseMapper<FocusTrainVo> {

	/**
	 * 获取集中培训列表
	 * @param status 状态
	 * @param name 名称
	 * @return
	 */
	List<FocusTrainVo> getList(@Param("personId") Long personId,
							   @Param("status") Integer status,
							   @Param("name") String name);

	/**
	 * 查询签到人员数量
	 * @param trainId
	 * @return
	 */
	Integer getSignCount(Long trainId);

	/**
	 * 查询报名状态
	 * @param personId
	 * @param trainId
	 * @return
	 */
	Integer getSignStatus(@Param("personId") Long personId,
	                      @Param("trainId") Long trainId);

	FocusTrainVo getDetail(Long trainId);
}
