package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.course.entity.Course;
import com.joyfishs.dawa.person.entity.Person;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safety.entity.SafetyRetrainingRecord;
import com.joyfishs.dawa.safety.entity.ViolationTypeConfig;
import com.joyfishs.dawa.safety.mapper.SafetyRetrainingRecordMapper;
import com.joyfishs.dawa.safety.mapper.ViolationTypeConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 安全再培训服务类
 * 处理违章触发后的安全再培训记录管理
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Slf4j
@Service
public class SafetyRetrainingService extends ServiceImpl<SafetyRetrainingRecordMapper, SafetyRetrainingRecord> {

    @Autowired
    private ViolationTypeConfigMapper violationTypeConfigMapper;

    @Autowired
    private PersonService personService;

    @Autowired
    private CourseMatchingService courseMatchingService;

    @Autowired
    @Lazy
    private SafetyScoreService safetyScoreService;

    @Autowired
    @Lazy
    private SafetyNotificationService notificationService;

    /**
     * 创建安全再培训记录（自动匹配课程）
     *
     * @param personId 人员ID
     * @param violationCode 违章代码
     * @param violationRecordId 违章记录ID（可为null）
     * @param operatorId 操作人ID
     * @return 创建的培训记录
     */
    @Transactional(rollbackFor = Exception.class)
    public SafetyRetrainingRecord createRetrainingRecord(Long personId, String violationCode, Long violationRecordId, Long operatorId) {
        log.info("创建安全再培训记录: personId={}, violationCode={}, violationRecordId={}", personId, violationCode, violationRecordId);

        // 1. 获取违章配置
        ViolationTypeConfig config = violationTypeConfigMapper.selectByViolationCode(violationCode);
        Integer trainingHours = config != null ? config.getTrainingHours() : 8;

        // 2. 获取人员工种，用于课程匹配
        Person person = personService.getById(personId);
        Integer personWorkType = (person != null) ? person.getWorkType() : null;

        // 3. 自动匹配课程
        Course matchedCourse = courseMatchingService.matchRetrainingCourse(personWorkType);

        // 4. 构建记录
        SafetyRetrainingRecord record = new SafetyRetrainingRecord();
        record.setPersonId(personId);
        record.setViolationCode(violationCode);
        record.setViolationRecordId(violationRecordId);
        record.setTrainingHours(trainingHours);
        if (matchedCourse != null) {
            record.setTrainingCourseId(matchedCourse.getId());
        }
        record.setStatus(SafetyRetrainingRecord.STATUS_PENDING);
        record.setOperatorId(operatorId);
        record.setCreatedAt(new Date());

        save(record);

        log.info("安全再培训记录创建成功: id={}, personId={}, courseId={}", record.getId(), personId, matchedCourse != null ? matchedCourse.getId() : null);
        return record;
    }

    /**
     * 开始再培训
     */
    @Transactional(rollbackFor = Exception.class)
    public void startRetraining(Long recordId) {
        log.info("开始安全再培训: recordId={}", recordId);
        SafetyRetrainingRecord record = getById(recordId);
        if (record == null) {
            log.warn("安全再培训记录不存在: recordId={}", recordId);
            return;
        }
        record.setStatus(SafetyRetrainingRecord.STATUS_ONGOING);
        record.setStartDate(new Date());
        updateById(record);
    }

    /**
     * 确认再培训完成（管理员操作，自动恢复积分）
     *
     * @param recordId 培训记录ID
     * @param confirmedBy 确认人ID
     * @return 积分恢复结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ScoreChangeResult confirmCompletion(Long recordId, Long confirmedBy) {
        log.info("确认再培训完成: recordId={}, confirmedBy={}", recordId, confirmedBy);

        SafetyRetrainingRecord record = getById(recordId);
        if (record == null) {
            log.warn("安全再培训记录不存在: recordId={}", recordId);
            return null;
        }

        if (!SafetyRetrainingRecord.STATUS_PENDING.equals(record.getStatus())
                && !SafetyRetrainingRecord.STATUS_ONGOING.equals(record.getStatus())) {
            log.warn("再培训记录状态不允许确认: recordId={}, status={}", recordId, record.getStatus());
            return null;
        }

        // 1. 恢复积分
        ScoreChangeResult scoreResult = safetyScoreService.restoreAfterRetraining(record.getPersonId(), recordId, confirmedBy);

        // 2. 更新记录状态
        record.setStatus(SafetyRetrainingRecord.STATUS_COMPLETED);
        record.setEndDate(new Date());
        record.setConfirmedBy(confirmedBy);
        record.setConfirmedAt(new Date());
        record.setScoreRestored(scoreResult != null ? scoreResult.getChangeAmount() : 4);
        updateById(record);

        // 3. 发送通知
        if (scoreResult != null) {
            notificationService.sendRetrainingCompletedNotification(
                    record.getPersonId(),
                    scoreResult.getChangeAmount(),
                    scoreResult.getAfterScore());
        }

        log.info("再培训确认完成: recordId={}, scoreRestored={}, newScore={}", recordId, record.getScoreRestored(), scoreResult != null ? scoreResult.getAfterScore() : null);
        return scoreResult;
    }

    /**
     * 完成再培训（通过/未通过）
     */
    @Transactional(rollbackFor = Exception.class)
    public ScoreChangeResult completeRetraining(Long recordId, boolean passed, Long operatorId) {
        log.info("完成安全再培训: recordId={}, passed={}", recordId, passed);

        SafetyRetrainingRecord record = getById(recordId);
        if (record == null) {
            log.warn("安全再培训记录不存在: recordId={}", recordId);
            return null;
        }

        ScoreChangeResult scoreResult = null;
        if (passed) {
            Integer scoreRestored = 4;
            scoreResult = safetyScoreService.restoreAfterRetraining(record.getPersonId(), recordId, operatorId);
            record.setStatus(SafetyRetrainingRecord.STATUS_COMPLETED);
            record.setScoreRestored(scoreResult != null ? scoreResult.getChangeAmount() : scoreRestored);
            record.setConfirmedBy(operatorId);
            record.setConfirmedAt(new Date());
        } else {
            record.setStatus(SafetyRetrainingRecord.STATUS_FAILED);
        }
        record.setEndDate(new Date());
        updateById(record);

        if (scoreResult != null) {
            notificationService.sendRetrainingCompletedNotification(
                    record.getPersonId(),
                    scoreResult.getChangeAmount(),
                    scoreResult.getAfterScore());
        }

        return scoreResult;
    }

    /**
     * 查询人员待完成的再培训记录
     */
    public List<SafetyRetrainingRecord> getPendingRetraining(Long personId) {
        return baseMapper.selectPendingByPersonId(personId);
    }

    /**
     * 查询人员是否有待完成的再培训
     */
    public boolean hasPendingRetraining(Long personId) {
        List<SafetyRetrainingRecord> records = baseMapper.selectPendingOrOngoingByPersonId(personId);
        return records != null && !records.isEmpty();
    }
}
