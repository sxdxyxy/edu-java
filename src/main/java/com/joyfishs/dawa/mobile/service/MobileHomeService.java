package com.joyfishs.dawa.mobile.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyfishs.dawa.notice.domain.vo.NoticeVo;
import com.joyfishs.dawa.notice.service.NoticeService;
import com.joyfishs.dawa.project.entity.Project;
import com.joyfishs.dawa.project.service.ProjectService;

import cn.hutool.core.util.ObjUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author xiaodai
 * @create 2022-02-16 16:34
 */

@Slf4j
@Service
public class MobileHomeService {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private NoticeService noticeService;

    /**
     * 返回移动端首页综合数据
     *
     * @return
     */
    public List<Project> remindList(Long personId) {
        log.info("XmMobileHomeService - findHomeData personId:{}", personId);
        // 获取学习提醒
        List<Project> projectList = projectService.getReminDList(personId);
        log.info("XmMobileHomeService - findHomeData xmProjectList:{}", projectList);
        return projectList;
    }

    /**
     * 根据人员ID获取
     *
     * @param personId 人员ID
     * @param type
     * @return
     */
    public List<NoticeVo> newsBulletin(Long personId, Integer type, String title) {
        if (ObjUtil.isNull(personId)) {
            return newsBulletin(type, title);
        }
        List<NoticeVo> noticeList = noticeService.findHomeNewsBulletin(personId, type, title);
        return noticeList;
    }

    /**
     * 游客访问
     *
     * @param type
     * @return
     */
    public List<NoticeVo> newsBulletin(Integer type, String title) {
        List<NoticeVo> noticeList = noticeService.findHomeNewsBulletin(type, title);
        return noticeList;
    }
}
