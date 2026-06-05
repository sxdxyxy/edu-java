package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safety.entity.AppealRecord;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import com.joyfishs.dawa.safety.mapper.AppealRecordMapper;
import com.joyfishs.dawa.violation.mapper.ViolationRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 申诉记录服务类
 * 处理违章申诉的发起、审批和积分返还
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Slf4j
@Service
public class AppealRecordService extends ServiceImpl<AppealRecordMapper, AppealRecord> {

    @Autowired
    private AppealRecordMapper appealRecordMapper;

    @Autowired
    @Lazy
    private SafetyScoreService safetyScoreService;

    @Autowired
    private SafetyRetrainingService retrainingService;

    @Autowired
    private ViolationRecordMapper violationRecordMapper;

    @Autowired
    private SafetyNotificationService notificationService;

    @Autowired
    private SafetyPostDeductService postDeductService;

    /**
     * 发起申诉
     * <p>
     * 申诉不阻断扣分生效，违章状态变为 "appealed"。
     * </p>
     *
     * @param violationRecordId 违章记录ID
     * @param personId          申诉人ID（作业人员）
     * @param appealReason      申诉理由
     * @param appealEvidence    补充证据（JSON数组字符串）
     * @return 创建的申诉记录
     */
    @Transactional(rollbackFor = Exception.class)
    public AppealRecord createAppeal(Long violationRecordId, Long personId,
                                     String appealReason, String appealEvidence) {
        log.info("发起申诉: violationRecordId={}, personId={}", violationRecordId, personId);

        // 1. 检查是否已有申诉
        AppealRecord existing = appealRecordMapper.selectByViolationRecordId(violationRecordId);
        if (existing != null) {
            log.warn("该违章已有申诉记录: violationRecordId={}", violationRecordId);
            throw new IllegalStateException("该违章已有申诉记录，不能重复提交");
        }

        // 2. 更新违章记录状态为 "appealed"
        violationRecordMapper.updateStatusAndRemark(
                violationRecordId, "appealed", appealReason);

        // 3. 创建申诉记录
        AppealRecord appeal = new AppealRecord();
        appeal.setViolationRecordId(violationRecordId);
        appeal.setPersonId(personId);
        appeal.setAppealReason(appealReason);
        appeal.setAppealEvidence(appealEvidence);
        appeal.setAppealTime(new Date());
        appeal.setReviewResult(AppealRecord.RESULT_PENDING);
        appeal.setCreatedAt(new Date());

        save(appeal);

        log.info("申诉创建成功: appealId={}, violationRecordId={}", appeal.getId(), violationRecordId);
        return appeal;
    }

    /**
     * 审批申诉（安全主管操作）
     * <p>
     * 通过时：返还积分 + 更新违章状态为 "appeal_approved" + 刷新安全码 + 通知
     * 驳回时：保持违章状态为 "appeal_rejected" + 通知
     * </p>
     *
     * @param appealId      申诉记录ID
     * @param reviewerId    审批人ID（安全主管）
     * @param approved      是否通过
     * @param reviewComment 审批意见
     * @return 申诉审批结果（包含积分返还详情）
     */
    @Transactional(rollbackFor = Exception.class)
    public AppealReviewResult reviewAppeal(Long appealId, Long reviewerId,
                                            boolean approved, String reviewComment) {
        log.info("审批申诉: appealId={}, reviewerId={}, approved={}", appealId, reviewerId, approved);

        AppealRecord appeal = getById(appealId);
        if (appeal == null) {
            log.warn("申诉记录不存在: appealId={}", appealId);
            throw new IllegalArgumentException("申诉记录不存在");
        }

        if (!AppealRecord.RESULT_PENDING.equals(appeal.getReviewResult())) {
            log.warn("申诉已审批，不允许重复审批: appealId={}, result={}", appealId, appeal.getReviewResult());
            throw new IllegalStateException("该申诉已审批，不能重复操作");
        }

        // 1. 更新申诉记录
        String reviewResult = approved ? AppealRecord.RESULT_APPROVED : AppealRecord.RESULT_REJECTED;
        int scoreRestored = 0;

        if (approved) {
            // 2. 获取违章记录中的扣分分值
            com.joyfishs.dawa.violation.entity.ViolationRecord violationRecord = violationRecordMapper.selectById(appeal.getViolationRecordId());
            scoreRestored = violationRecord != null ? violationRecord.getDeductAmount() : 0;

            // 3. 返还积分（申诉通过时，返还违章扣分分值）
            if (scoreRestored > 0) {
                ScoreChangeResult scoreResult = safetyScoreService.restoreScore(
                        appeal.getPersonId(),
                        scoreRestored,
                        "申诉通过，积分返还",
                        reviewerId);

                if (scoreResult != null) {
                    // 4. 并行触发：刷新安全码 + 通知
                    postDeductService.refreshSafetyCode(appeal.getPersonId(), null, scoreResult.getColor());
                    notificationService.sendAppealResultNotification(appeal.getPersonId(), true, reviewComment);

                    log.info("申诉通过，积分已返还: appealId={}, personId={}, scoreRestored={}, newScore={}, newColor={}",
                            appealId, appeal.getPersonId(), scoreRestored,
                            scoreResult.getAfterScore(), scoreResult.getColor());
                }
            }

            violationRecordMapper.updateStatusAndRemark(
                    appeal.getViolationRecordId(), "appeal_approved", reviewComment);

        } else {
            violationRecordMapper.updateStatusAndRemark(
                    appeal.getViolationRecordId(), "appeal_rejected", reviewComment);

            // 通知申诉人
            notificationService.sendAppealResultNotification(appeal.getPersonId(), false, reviewComment);

            log.info("申诉驳回: appealId={}, personId={}", appealId, appeal.getPersonId());
        }

        // 6. 更新申诉记录审批结果
        appeal.setReviewResult(reviewResult);
        appeal.setReviewComment(reviewComment);
        appeal.setReviewerId(reviewerId);
        appeal.setReviewTime(new Date());
        appeal.setScoreRestored(scoreRestored);
        appeal.setScoreRestoredAt(approved ? new Date() : null);
        appeal.setUpdatedAt(new Date());

        updateById(appeal);

        // 7. 构建返回结果
        AppealReviewResult result = new AppealReviewResult();
        result.setAppealId(appealId);
        result.setApproved(approved);
        result.setScoreRestored(scoreRestored);
        result.setReviewTime(appeal.getReviewTime());

        return result;
    }

    /**
     * 查询待审批的申诉列表（安全主管工作台）
     */
    public List<AppealRecord> listPendingAppeals() {
        return appealRecordMapper.selectPendingAppeals();
    }

    /**
     * 查询申诉人的申诉记录
     */
    public List<AppealRecord> listByPersonId(Long personId) {
        return appealRecordMapper.selectByPersonId(personId);
    }

    /**
     * 申诉审批结果 DTO
     */
    @lombok.Data
    public static class AppealReviewResult {
        private Long appealId;
        private boolean approved;
        private int scoreRestored;
        private Date reviewTime;
    }
}
