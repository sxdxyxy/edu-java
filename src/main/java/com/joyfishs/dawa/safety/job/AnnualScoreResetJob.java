package com.joyfishs.dawa.safety.job;

import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import com.joyfishs.dawa.safety.mapper.SafetyScoreAccountMapper;
import com.joyfishs.dawa.safety.service.SafetyScoreAccountService;
import com.joyfishs.dawa.safety.service.SafetyScoreTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 安全积分年度清零定时任务
 * 每年1月1日执行年度清零，将所有账户积分恢复为初始值
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Slf4j
@Component
public class AnnualScoreResetJob {

    @Autowired
    private SafetyScoreAccountMapper safetyScoreAccountMapper;

    @Autowired
    private SafetyScoreAccountService safetyScoreAccountService;

    @Autowired
    private SafetyScoreTransactionService scoreTransactionService;

    /**
     * 年度清零定时任务
     * 每年1月1日 00:00 执行
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void resetAnnualScore() {
        log.info("========== 安全积分年度清零任务开始 ==========");
        LocalDate today = LocalDate.now();
        LocalDate lastYear = today.minusYears(1);

        try {
            List<SafetyScoreAccount> accounts = safetyScoreAccountService.getNeedsResetAccounts(java.sql.Date.valueOf(lastYear));
            log.info("需要清零的账户数量: {}", accounts.size());

            int successCount = 0;
            int failCount = 0;

            for (SafetyScoreAccount account : accounts) {
                try {
                    int beforeScore = account.getCurrentScore();
                    int resetScore = account.getInitialScore();

                    // 更新积分
                    account.setCurrentScore(resetScore);
                    account.setAnnualResetDate(java.sql.Date.valueOf(today));
                    safetyScoreAccountMapper.updateById(account);

                    // 记录流水
                    scoreTransactionService.recordReset(account.getPersonId(), beforeScore, resetScore, null);

                    successCount++;
                    log.debug("账户清零成功: personId={}, beforeScore={}, afterScore={}",
                        account.getPersonId(), beforeScore, resetScore);

                } catch (Exception e) {
                    failCount++;
                    log.error("账户清零失败: personId={}", account.getPersonId(), e);
                }
            }

            log.info("========== 安全积分年度清零任务完成 ==========");
            log.info("成功: {}, 失败: {}", successCount, failCount);

        } catch (Exception e) {
            log.error("年度清零任务执行失败", e);
        }
    }

    /**
     * 手动触发年度清零（用于测试或紧急清零）
     *
     * @param resetDate 清零日期（可选，默认今天）
     * @return 成功清零的数量
     */
    public int manualReset(LocalDate resetDate) {
        if (resetDate == null) {
            resetDate = LocalDate.now();
        }

        log.info("手动触发年度清零: resetDate={}", resetDate);
        LocalDate checkDate = resetDate.minusYears(1);

        List<SafetyScoreAccount> accounts = safetyScoreAccountService.getNeedsResetAccounts(java.sql.Date.valueOf(checkDate));
        log.info("需要清零的账户数量: {}", accounts.size());

        int successCount = 0;
        for (SafetyScoreAccount account : accounts) {
            try {
                int beforeScore = account.getCurrentScore();
                int resetScore = account.getInitialScore();

                account.setCurrentScore(resetScore);
                account.setAnnualResetDate(java.sql.Date.valueOf(resetDate));
                safetyScoreAccountMapper.updateById(account);

                scoreTransactionService.recordReset(account.getPersonId(), beforeScore, resetScore, null);
                successCount++;

            } catch (Exception e) {
                log.error("账户清零失败: personId={}", account.getPersonId(), e);
            }
        }

        log.info("手动清零完成: 成功={}", successCount);
        return successCount;
    }
}
