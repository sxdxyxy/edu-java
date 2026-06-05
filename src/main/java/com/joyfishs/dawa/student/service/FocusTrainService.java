package com.joyfishs.dawa.student.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.student.domain.vo.FocusTrainVo;
import com.joyfishs.dawa.student.mapper.FocusTrainMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 集中培训service
 */
@Slf4j
@Service
public class FocusTrainService extends ServiceImpl<FocusTrainMapper, FocusTrainVo> {

	/**
	 * 获取终端培训列表
	 * @param personId
	 * @param status
	 * @param name
	 * @return
	 */
	public List<FocusTrainVo> getList(Long personId, Integer status, String name) {
		return baseMapper.getList(personId,status,name);
	}

	/**
	 * 获取终端培训详情
	 * @param personId
	 * @param trainId
	 * @return
	 */
	public FocusTrainVo getDetail(Long personId, Long trainId) {
		log.info("XmFocusTrainService - getDetail params( personId:{},trainId:{} )",personId,trainId);
		FocusTrainVo byId = baseMapper.getDetail(trainId);
		byId.setSignNum(getSignCount(trainId));
		if(getSignStatus(personId,trainId) > 0) byId.setSignStatus(2); //已报名
		return byId;
	}

	/**
	 * 查询项目下报名人员数量
	 * @param trainId
	 * @return
	 */
	public Integer getSignCount(Long trainId){
		log.info("XmFocusTrainService - getSignCount:{}",trainId);
		return baseMapper.getSignCount(trainId);
	}

	/** 获取签到状态 */
	public Integer getSignStatus(Long personId,Long trainId){
		return baseMapper.getSignStatus(personId, trainId);
	}
}
