package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.entity.SafetyScoreTransaction;
import com.joyfishs.dawa.safety.mapper.SafetyScoreTransactionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 安全积分变动流水服务类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Slf4j
@Service
public class SafetyScoreTransactionService extends ServiceImpl<SafetyScoreTransactionMapper, SafetyScoreTransaction> {

    /**
     * 记录积分扣减
     */
    public void recordDeduct(Long personId, Integer beforeScore, Integer afterScore,
                            String violationCode, Long violationRecordId, Long operatorId) {
        SafetyScoreTransaction transaction = new SafetyScoreTransaction();
        transaction.setPersonId(personId);
        transaction.setChangeType(SafetyScoreTransaction.CHANGE_TYPE_DEDUCT);
        transaction.setChangeAmount(afterScore - beforeScore); // 负数
        transaction.setBeforeScore(beforeScore);
        transaction.setAfterScore(afterScore);
        transaction.setTriggerReason("violation:" + violationCode);
        transaction.setViolationRecordId(violationRecordId);
        transaction.setOperatorId(operatorId);
        transaction.setCreatedAt(new java.util.Date());

        save(transaction);
        log.debug("记录积分扣减流水: personId={}, beforeScore={}, afterScore={}", personId, beforeScore, afterScore);
    }

    /**
     * 记录积分恢复
     */
    public void recordRestore(Long personId, Integer beforeScore, Integer afterScore,
                             String reason, Long retrainingRecordId, Long operatorId) {
        SafetyScoreTransaction transaction = new SafetyScoreTransaction();
        transaction.setPersonId(personId);
        transaction.setChangeType(SafetyScoreTransaction.CHANGE_TYPE_RESTORE);
        transaction.setChangeAmount(afterScore - beforeScore); // 正数
        transaction.setBeforeScore(beforeScore);
        transaction.setAfterScore(afterScore);
        transaction.setTriggerReason(reason);
        transaction.setRetrainingRecordId(retrainingRecordId);
        transaction.setOperatorId(operatorId);
        transaction.setCreatedAt(new java.util.Date());

        save(transaction);
        log.debug("记录积分恢复流水: personId={}, beforeScore={}, afterScore={}", personId, beforeScore, afterScore);
    }

    /**
     * 记录积分清零
     */
    public void recordReset(Long personId, Integer beforeScore, Integer afterScore, Long operatorId) {
        SafetyScoreTransaction transaction = new SafetyScoreTransaction();
        transaction.setPersonId(personId);
        transaction.setChangeType(SafetyScoreTransaction.CHANGE_TYPE_RESET);
        transaction.setChangeAmount(afterScore - beforeScore);
        transaction.setBeforeScore(beforeScore);
        transaction.setAfterScore(afterScore);
        transaction.setTriggerReason("annual_reset");
        transaction.setOperatorId(operatorId);
        transaction.setCreatedAt(new java.util.Date());

        save(transaction);
        log.debug("记录积分清零流水: personId={}, beforeScore={}, afterScore={}", personId, beforeScore, afterScore);
    }

    /**
     * 查询人员的积分变动记录
     */
    public List<SafetyScoreTransaction> getByPersonId(Long personId) {
        return baseMapper.selectByPersonId(personId);
    }

    /**
     * 查询人员的最近N条积分变动记录
     */
    public List<SafetyScoreTransaction> getRecentByPersonId(Long personId, Integer limit) {
        return baseMapper.selectRecentByPersonId(personId, limit);
    }
}
