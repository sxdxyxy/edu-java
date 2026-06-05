package com.joyfishs.dawa.notice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.notice.domain.vo.NoticeVo;
import com.joyfishs.dawa.notice.entity.Notice;
import com.joyfishs.dawa.notice.entity.NoticePerson;
import com.joyfishs.dawa.notice.mapper.NoticeMapper;
import com.joyfishs.dawa.org.entity.SysOrg;
import com.joyfishs.dawa.org.service.SysOrgService;
import com.joyfishs.dawa.person.domain.po.ModifyCreditEvent;
import com.joyfishs.dawa.person.enums.CreditType;
import com.joyfishs.dawa.student.entity.PersonCourseRelate;
import com.joyfishs.dawa.student.entity.ProjectPersonStudyRecord;
import com.joyfishs.dawa.student.service.PersonCourseRelateService;
import com.joyfishs.dawa.student.service.ProjectPersonStudyRecordService;
import com.joyfishs.system.entity.SysUser;
import com.joyfishs.system.enums.YesOrNoState;
import com.joyfishs.system.service.SysUserService;
import com.joyfishs.utils.SecurityUtil;
import com.joyfishs.utils.exception.CustomException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    @Autowired
    private NoticePersonService noticePersonService;

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ProjectPersonStudyRecordService studyRecordService;

    @Autowired
    private PersonCourseRelateService relateService;

    @Transactional
    public boolean saveOrUpdateNotice(Notice notice) {
        notice.setStatus(notice.getIsNowPublish() == 1 ? 1 : 0);
        if (notice.getIsNowPublish() == 1) {
            notice.setStatus(1);
            notice.setPublishTime(LocalDateTime.now());
        } else {
            notice.setStatus(0);
        }
        boolean flag = this.saveOrUpdate(notice);

        /*
        // 2022-05-19 修改为发布范围下所有人都能查看,故新增的时候不初始化XmNoticePerson,已读时插入数据

        // 保存公告下的接收人员
        this.executeSendPerson(notice.getId(), notice.getRangeId());*/

        return flag;
    }

//    private void executeSendPerson(Long noticeId, Long rangeId) {
//        // 先删除该公告下的接收人员
//        noticePersonService.deleteByNoticeId(noticeId);
//
//        // 根据范围id查询到单位(or 项目)
//        SysOrg sysOrg = sysOrgService.getById(rangeId);
//        if (null == sysOrg) throw new CustomException("发布范围选择有误，请重新操作");
//
//        List<NoticePerson> noticePersonList = new ArrayList<>();
//        if (sysOrg.getOrgType() == 1) {
//            // 如果范围为单位，则直接查询该单位下的人员
//            List<Person> personList = personService.getBaseMapper().queryList(new Person().setOrgId(rangeId));
//            if (personList.isEmpty()) throw new CustomException("该发布范围内接收人员为空，请重新选择");
//            for (Person person : personList) {
//                NoticePerson noticePerson = new NoticePerson();
//                noticePerson.setNoticeId(noticeId);
//                noticePerson.setPersonId(person.getId());
//                noticePerson.setUserId(person.getUserId());
//                noticePerson.setIsRead(YesOrNoState.NO.getState());
//                noticePersonList.add(noticePerson);
//            }
//        }
//
//        noticePersonService.saveBatch(noticePersonList);
//    }


    public List<NoticeVo> queryListCreate(Long userId, Integer type, String title) {
        List<NoticeVo> result = baseMapper.queryListCreate(userId, type, title);
        result.stream().filter(item->item.getRangeId()==0).forEach(item ->item.setRangeName("所有人"));
        return result;
    }

    public List<Notice> queryListReceive(Long personId, String title) {
        return baseMapper.queryListReceive(personId, title);
    }

    @Transactional
    public boolean del(Long id, String deleteReason) {
        Notice notice = getById(id);
        if (ObjectUtil.isNull(notice)) {
            throw new CustomException("删除数据不存在");
        }
        notice.setIsDelete(YesOrNoState.YES.getState());
        notice.setDeleteBy(SecurityUtil.getUserId());
        notice.setDeleteTime(new Date());
        notice.setDeleteReason(deleteReason);

        // 先删除该公告下的接收人员
        noticePersonService.deleteByNoticeId(id);

        return updateById(notice);
    }


    public NoticeVo get(Long id, Integer isEdit) {
        Notice notice = getById(id);
        if (ObjectUtil.isNull(notice)) {
            throw new CustomException("通知公告不存在");
        }
        SysUser user = sysUserService.getById(notice.getCreateBy());

        if (isEdit != null && 0 == isEdit) {  // 浏览量伽伽
            notice.setViewCount(notice.getViewCount() + 1);
            updateById(notice);
        }
        NoticeVo result = new NoticeVo();
        BeanUtil.copyProperties(notice, result);
        if (user != null) {
            result.setPublishName(user.getName());
        } else {
            result.setPublishName("未知发布人");
        }

        if(notice.getRangeId()==0){
            result.setRangeName("所有人");
        }else {
            SysOrg sysOrg = sysOrgService.getById(notice.getRangeId());
            result.setRangeName(sysOrg.getName());
        }

        SysOrg createOrg = sysOrgService.getById(notice.getCreateOrgId());
        if (null != createOrg) {
            result.setCreateOrgName(createOrg.getName());
        } else {
            result.setCreateOrgName("平台");
        }
        return result;
    }

    @Transactional
    public boolean executeRead(Long noticeId,Long userId,Long personId) {
        Notice notice = getById(noticeId);


        ModifyCreditEvent event = new ModifyCreditEvent();
        event.setBusinessId(noticeId.toString());
        event.setType(CreditType.READ_BULLETIN.getIndex());
        event.setPersonId(personId);
        SpringUtil.getApplicationContext().publishEvent(event);
        // 查询当前用户是否已读公告
        NoticePerson existNoticePerson = noticePersonService.getBaseMapper().getByNoticeIdAndPersonId(noticeId, personId);
        // 如果公告未读，则新增记录
        if (null == existNoticePerson) {
//            int updateCount = xmNoticePersonService.getBaseMapper().updateRead(noticeId, userId);
            // 2022-05-19 修改为发布范围下所有人都能查看,故新增的时候不初始化XmNoticePerson,已读时插入数据
            NoticePerson noticePerson = new NoticePerson();
            noticePerson.setNoticeId(noticeId);
            noticePerson.setPersonId(personId);
            noticePerson.setUserId(userId);
            noticePerson.setIsRead(YesOrNoState.YES.getState());
            noticePerson.setReadTime(new Date());
            noticePersonService.save(noticePerson);
            // 如果公告计算学时且学时大于0，则插入xm_person_course_relate ,xm_project_person_study_record
            if (notice.getIsCalculate() == 1 && notice.getClassHour().compareTo(BigDecimal.ZERO) == 1) {
                this.executeSaveRelate(noticeId, noticePerson.getPersonId(), notice.getClassHour());
            }
        }
        return true;
    }

    /**
     * 插入学时关联表
     * @param noticeId
     * @param personId
     * @param classHour
     */
    private void executeSaveRelate(Long noticeId, Long personId, BigDecimal classHour) {
        PersonCourseRelate relate = new PersonCourseRelate();
        relate.setNoticeId(noticeId)
                .setPersonId(personId)
                .setLearnHours(classHour);
        relate.setCreateBy(SecurityUtil.getUserId());
        relate.setCreateTime(new Date());
        relate.setIsDelete(YesOrNoState.NO.getState());
        relateService.save(relate);

        ProjectPersonStudyRecord studyRecord = new ProjectPersonStudyRecord();
        studyRecord.setNoticeId(noticeId)
                .setPersonId(personId)
                .setStudyHours(classHour);

        studyRecord.setCreateBy(SecurityUtil.getUserId());
        studyRecord.setCreateTime(new Date());
        studyRecord.setIsDelete(YesOrNoState.NO.getState());

        studyRecordService.save(studyRecord);
    }

    public List<NoticePerson> queryPersonList(Long noticeId, Integer isRead) {
        if (isRead == 1) { // 查询已读人员
            return noticePersonService.getBaseMapper().listReadByNoticeId(noticeId);
        } else {
            return noticePersonService.getBaseMapper().listUnreadByNoticeId(noticeId);
        }
    }

    /**
     * 移动端获取新闻
     *
     * @param personId 人员ID
     * @return
     */
    public List<NoticeVo> findHomeNewsBulletin(Long personId, Integer type, String title) {
        return baseMapper.findHomeNewsBulletin(personId, type, title);
    }

    /**
     * 移动端获取新闻
     *
     * @return
     */
    public List<NoticeVo> findHomeNewsBulletin(Integer type, String title) {
        return baseMapper.findHomeNewsBulletinByAnonymous(type, title);
    }

    @Transactional
    public boolean cancel(Long id) {
        Notice notice = getById(id);
        if (ObjectUtil.isNull(notice)) {
            throw new CustomException("撤销数据不存在");
        }
        notice.setStatus(2);
        notice.setUpdateBy(SecurityUtil.getUserId());
        notice.setUpdateTime(new Date());

        // 删除该公告下的接收人员
        noticePersonService.deleteByNoticeId(id);

        updateById(notice);
        return true;
    }
}
