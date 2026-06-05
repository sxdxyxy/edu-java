package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import com.joyfishs.dawa.safety.entity.ViolationTypeConfig;
import com.joyfishs.dawa.safety.mapper.SafetyScoreAccountMapper;
import com.joyfishs.dawa.safety.mapper.ViolationTypeConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 安全积分核心服务类
 * 处理违章扣分、积分恢复、颜色判定等核心逻辑
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Slf4j
@Service
public class SafetyScoreService extends ServiceImpl<SafetyScoreAccountMapper, SafetyScoreAccount> {

    public static final String CHANGE_TYPE_DEDUCT = "deduct";
    public static final String CHANGE_TYPE_RESTORE = "restore";
    public static final String CHANGE_TYPE_RETRAINING = "retraining";

    @Autowired
    private SafetyScoreAccountMapper scoreAccountMapper;

    @Autowired
    private ViolationTypeConfigMapper violationTypeMapper;

    @Autowired
    @Lazy
    private SafetyScoreAccountService scoreAccountService;

    @Autowired
    @Lazy
    private SafetyRetrainingService retrainingService;

    @Autowired
    private SafetyScoreTransactionService transactionService;

    /**
     * 违章扣分并返回结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ScoreChangeResult deductScore(Long personId, String violationCode, Long operatorId) {
        log.info("开始扣分处理: personId={}, violationCode={}", personId, violationCode);

        ViolationTypeConfig config = violationTypeMapper.selectByViolationCode(violationCode);
        if (config == null) {
            log.warn("违章类型不存在: {}", violationCode);
            return null;
        }

        SafetyScoreAccount account = scoreAccountService.getOrCreateAccount(personId, null, null,
            SafetyScoreAccountService.WORK_TYPE_WORKER);

        int beforeScore = account.getCurrentScore();
        int deductScore = config.getDeductScore();
        int afterScore = Math.max(0, beforeScore - deductScore);

        account.setCurrentScore(afterScore);
        scoreAccountMapper.updateById(account);

        boolean needTraining = config.getTriggerTraining() != null
            && config.getTriggerTraining()
            && afterScore < scoreAccountService.getYellowThreshold(account.getWorkType());

        Long retrainingRecordId = null;
        if (needTraining) {
            log.info("触发安全再培训: personId={}, violationCode={}", personId, violationCode);
            try {
                var record = retrainingService.createRetrainingRecord(personId, violationCode, null, operatorId);
                if (record != null) {
                    retrainingRecordId = record.getId();
                }
                log.info("安全再培训记录创建成功: personId={}", personId);
            } catch (Exception e) {
                log.error("创建安全再培训记录失败: personId={}, violationCode={}", personId, violationCode, e);
            }
        }

        transactionService.recordDeduct(personId, beforeScore, afterScore, violationCode, null, operatorId);

        String color = scoreAccountService.getColorByScore(account.getWorkType(), afterScore);
        ScoreChangeResult result = new ScoreChangeResult(
            afterScore, color, needTraining, -deductScore, retrainingRecordId, beforeScore, CHANGE_TYPE_DEDUCT);

        log.info("扣分完成: personId={}, beforeScore={}, afterScore={}, color={}, needTraining={}, retrainingRecordId={}",
            personId, beforeScore, afterScore, color, needTraining, retrainingRecordId);

        return result;
    }

    /**
     * 恢复积分
     */
    @Transactional(rollbackFor = Exception.class)
    public ScoreChangeResult restoreScore(Long personId, Integer restoreAmount, String reason, Long operatorId) {
        log.info("开始恢复积分: personId={}, restoreAmount={}, reason={}", personId, restoreAmount, reason);

        SafetyScoreAccount account = scoreAccountService.getByPersonId(personId);
        if (account == null) {
            log.warn("人员积分账户不存在: personId={}", personId);
            return null;
        }

        int beforeScore = account.getCurrentScore();
        int maxScore = account.getInitialScore();
        int afterScore = Math.min(maxScore, beforeScore + restoreAmount);

        account.setCurrentScore(afterScore);
        scoreAccountMapper.updateById(account);

        transactionService.recordRestore(personId, beforeScore, afterScore, reason, null, operatorId);

        String color = scoreAccountService.getColorByScore(account.getWorkType(), afterScore);
        ScoreChangeResult result = new ScoreChangeResult(
            afterScore, color, false, restoreAmount, null, beforeScore, CHANGE_TYPE_RESTORE);

        log.info("恢复积分完成: personId={}, beforeScore={}, afterScore={}, color={}",
            personId, beforeScore, afterScore, color);

        return result;
    }

    /**
     * 安全再培训完成后恢复积分
     */
    @Transactional(rollbackFor = Exception.class)
    public ScoreChangeResult restoreAfterRetraining(Long personId, Long retrainingRecordId, Long operatorId) {
        log.info("安全再培训完成，恢复积分: personId={}, retrainingRecordId={}", personId, retrainingRecordId);

        Integer restoreAmount = 4;

        SafetyScoreAccount account = scoreAccountService.getByPersonId(personId);
        if (account == null) {
            log.warn("人员积分账户不存在: personId={}", personId);
            return null;
        }

        int beforeScore = account.getCurrentScore();
        int maxScore = account.getInitialScore();
        int afterScore = Math.min(maxScore, beforeScore + restoreAmount);

        account.setCurrentScore(afterScore);
        scoreAccountMapper.updateById(account);

        transactionService.recordRestore(personId, beforeScore, afterScore, "安全再培训完成", retrainingRecordId, operatorId);

        String color = scoreAccountService.getColorByScore(account.getWorkType(), afterScore);
        ScoreChangeResult result = new ScoreChangeResult(
            afterScore, color, false, restoreAmount, retrainingRecordId, beforeScore, CHANGE_TYPE_RETRAINING);

        log.info("安全再培训完成，积分恢复: personId={}, beforeScore={}, afterScore={}, color={}, restoreAmount={}",
            personId, beforeScore, afterScore, color, restoreAmount);

        return result;
    }

    public String getSafetyCodeColor(Long personId) {
        return scoreAccountService.getColorByPersonId(personId);
    }

    public Integer getCurrentScore(Long personId) {
        SafetyScoreAccount account = scoreAccountService.getByPersonId(personId);
        return account != null ? account.getCurrentScore() : null;
    }
}
