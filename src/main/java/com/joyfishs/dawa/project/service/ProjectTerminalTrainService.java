package com.joyfishs.dawa.project.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.message.service.MessageService;
import com.joyfishs.dawa.project.domain.vo.ProjectList;
import com.joyfishs.dawa.project.entity.ProjectTerminalTrain;
import com.joyfishs.dawa.project.mapper.ProjectTerminalTrainMapper;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.date.DateUtil;

/**
*  终端培训服务
 * @author ykfnb
 */
@Service
public class ProjectTerminalTrainService extends ServiceImpl<ProjectTerminalTrainMapper, ProjectTerminalTrain> {

	@Autowired
	MessageService messageService;

	/**
	 * 批量删除
	 * @param idList
	 * @return
	 */
	public int del(List<Long> idList) {
		return baseMapper.batchDelete(idList, SecurityUtil.getUserId());
	}

	/**
	 * 查询终端培训列表
	 * @param status
	 * @param name
	 * @param startDate
	 * @param endDate
	 * @param projectId
	 * @return
	 */
	public List<ProjectList> findList(Integer status, String name, Date startDate, Date endDate, Long projectId) {
		return baseMapper.findList(SecurityUtil.getManagedOrgIds(),status,name,startDate,endDate,projectId);
	}

	/**
	 * 发布终端培训
	 * @param id
	 * @return
	 */
	public boolean release(Long id) {
		ProjectTerminalTrain train = this.getById(id);
		if(train == null){
			throw new CustomException("项目不存在");
		}
		if(train.getStatus() != 1){
			throw new CustomException("项目已发布，请勿重复发布");
		}
		train.setStatus(3); //终端培训发布时-状态变为已开始
		train.setUpdateBy(SecurityUtil.getUserId());
		train.setUpdateTime(DateUtil.date());
		boolean b = this.updateById(train);
		//发布之后发送消息
		messageService.sendMsgByOrgId(train.getOrgId(),DateUtil.date(),5, id, train.getTrainName());
		return b;
	}

	/** 刷新终端培训状态 */
	public void refreshTrainStatus(){
		baseMapper.refreshTrainStatus();
	}
}
