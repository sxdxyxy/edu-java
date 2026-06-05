package com.joyfishs.dawa.safety.service;

import com.joyfishs.dawa.notice.entity.Notice;
import com.joyfishs.dawa.notice.entity.NoticePerson;
import com.joyfishs.dawa.notice.service.NoticePersonService;
import com.joyfishs.dawa.notice.service.NoticeService;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 安全通知服务
 * <p>
 * 违章扣分、积分变动等安全事件的微信/短信/站内通知
 * </p>
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Slf4j
@Service
public class SafetyNotificationService {

    /** 通知类型：安全提醒 */
    private static final int NOTICE_TYPE_SAFETY = 2;

    @Autowired
    private PersonService personService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticePersonService noticePersonService;

    /**
     * 发送违章扣分通知
     */
    public void sendDeductionNotification(Long personId, String violationName,
            int deductScore, int currentScore, String newColor) {
        log.info("发送违章扣分通知: personId={}, violation={}, deduct={}, current={}, color={}",
                personId, violationName, deductScore, currentScore, newColor);

        Person person = personService.getById(personId);
        if (person == null) {
            log.warn("发送扣分通知失败-人员不存在: personId={}", personId);
            return;
        }

        String colorText = switch (newColor) {
            case "RED" -> "红码（禁止进场）";
            case "YELLOW" -> "黄码（预警/受限）";
            default -> "绿码";
        };

        String message = String.format(
                "您因「%s」被扣%d分，当前积分%d分，安全码已变为%s。请注意安全作业。",
                violationName, deductScore, currentScore, colorText);

        if (person.getPhone() != null && !person.getPhone().isEmpty()) {
            sendMessage(person.getPhone(), message, person.getUserId());
        }

        sendInnerNotice(person.getUserId(), person.getId(),
                person.getName() + "被扣分通知",
                violationName + "，" + message);
    }

    /**
     * 发送再培训完成通知
     */
    public void sendRetrainingCompletedNotification(Long personId, int scoreRestored, int currentScore) {
        log.info("发送再培训完成通知: personId={}, scoreRestored={}, current={}",
                personId, scoreRestored, currentScore);

        Person person = personService.getById(personId);
        if (person == null) return;

        String message = String.format(
                "您的安全再培训已通过，积分恢复%d分，当前积分%d分，安全码已恢复为绿码。",
                scoreRestored, currentScore);

        if (person.getPhone() != null) {
            sendMessage(person.getPhone(), message, person.getUserId());
        }
        sendInnerNotice(person.getUserId(), person.getId(), "安全再培训已通过", message);
    }

    /**
     * 发送申诉结果通知
     */
    public void sendAppealResultNotification(Long personId, boolean approved, String reason) {
        log.info("发送申诉结果通知: personId={}, approved={}", personId, approved);

        Person person = personService.getById(personId);
        if (person == null) return;

        String result = approved ? "通过" : "驳回";
        String message = String.format(
                "您提交的违章申诉已处理，结果：%s。%s",
                result, approved ? "积分已返还，感谢您的监督。" : "维持原判，如有异议请联系安全管理员。");

        if (person.getPhone() != null) {
            sendMessage(person.getPhone(), message, person.getUserId());
        }
        sendInnerNotice(person.getUserId(), person.getId(), "违章申诉结果通知", message);
    }

    // ===== 私有方法 =====

    private void sendMessage(String phone, String message, Long userId) {
        // TODO: 对接微信模板消息或短信网关（腾讯云短信/企业微信）
        // 当前实现：记录日志
        log.info("【通知发送】手机号={}, message={}, userId={}", phone, message, userId);
    }

    /**
     * 保存站内通知
     */
    @Transactional(rollbackFor = Exception.class)
    private void sendInnerNotice(Long userId, Long personId, String title, String content) {
        if (userId == null) {
            log.warn("【站内通知】userId为空，跳过保存: title={}", title);
            return;
        }
        try {
            Notice notice = new Notice();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setType(NOTICE_TYPE_SAFETY);
            notice.setRangeId(0L);
            notice.setIsMustRead(0);
            notice.setIsCalculate(0);
            notice.setIsNowPublish(1);
            notice.setStatus(1);
            notice.setPublishTime(LocalDateTime.now());
            notice.setViewCount(0);
            notice.setIsDelete(0);

            noticeService.saveOrUpdateNotice(notice);

            NoticePerson noticePerson = new NoticePerson();
            noticePerson.setNoticeId(notice.getId());
            noticePerson.setPersonId(personId);
            noticePerson.setUserId(userId);
            noticePerson.setIsRead(0);

            noticePersonService.save(noticePerson);

            log.info("【站内通知】保存成功: userId={}, title={}, noticeId={}", userId, title, notice.getId());
        } catch (Exception e) {
            log.error("【站内通知】保存失败: userId={}, title={}, error={}", userId, title, e.getMessage());
        }
    }
}
