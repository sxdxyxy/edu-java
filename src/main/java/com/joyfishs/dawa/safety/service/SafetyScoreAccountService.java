package com.joyfishs.dawa.safety.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import com.joyfishs.dawa.safety.mapper.SafetyScoreAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 安全积分账户服务类
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Slf4j
@Service
public class SafetyScoreAccountService extends ServiceImpl<SafetyScoreAccountMapper, SafetyScoreAccount> {

    /**
     * 岗位类型常量（兼容旧代码）
     */
    public static final String WORK_TYPE_WORKER = "worker";
    public static final String WORK_TYPE_SPECIALIZED = "specialized";
    public static final String WORK_TYPE_SAFETY_ADMIN = "safety_admin";

    @Autowired
    @Lazy
    private WorkTypeMappingService workTypeMappingService;

    /**
     * 获取默认初始积分（从映射表读取，无映射则用旧配置）
     */
    public int getDefaultInitialScore(String workType) {
        // 尝试从工种映射表中读取（workType 可能是工种编码字符串）
        // 旧接口传入的是 account_work_type，直接用
        return switch (workType) {
            case WORK_TYPE_SPECIALIZED -> 12;
            case WORK_TYPE_SAFETY_ADMIN -> 10;
            default -> 12; // worker 默认 12
        };
    }

    /**
     * 根据工种编码创建账户（Phase 1 新增）
     * 自动从映射表读取积分配置，无需手动指定岗位类型
     *
     * @param personId 人员ID
     * @param userId 用户ID
     * @param projectId 项目部ID
     * @param personWorkType 人员工种编码（xm_person.work_type，1-19）
     * @return 创建的账户
     */
    public SafetyScoreAccount createAccountByWorkType(Long personId, Long userId, Long projectId, Integer personWorkType) {
        // 通过映射表解析岗位类型
        String accountWorkType = workTypeMappingService.resolveAccountWorkType(personWorkType, WORK_TYPE_WORKER);
        int initialScore = workTypeMappingService.getInitialScoreByWorkType(personWorkType);

        log.info("创建安全积分账户（工种映射）: personId={}, personWorkType={}, accountWorkType={}, initialScore={}",
                personId, personWorkType, accountWorkType, initialScore);

        SafetyScoreAccount account = new SafetyScoreAccount();
        account.setPersonId(personId);
        account.setUserId(userId);
        account.setProjectId(projectId);
        account.setWorkType(accountWorkType);
        account.setPersonWorkType(personWorkType);
        account.setInitialScore(initialScore);
        account.setCurrentScore(initialScore);
        account.setStatus("active");
        save(account);
        return account;
    }

    /**
     * 创建安全积分账户（旧接口，保留兼容）
     */
    public SafetyScoreAccount createAccount(Long personId, Long userId, Long projectId, String workType) {
        int initialScore = getDefaultInitialScore(workType);

        SafetyScoreAccount account = new SafetyScoreAccount();
        account.setPersonId(personId);
        account.setUserId(userId);
        account.setProjectId(projectId);
        account.setWorkType(workType);
        account.setInitialScore(initialScore);
        account.setCurrentScore(initialScore);
        account.setStatus("active");
        save(account);
        return account;
    }

    /**
     * 获取或创建账户（优先使用工种映射）
     * 如果传入 personWorkType，使用新方法创建账户
     */
    public SafetyScoreAccount getOrCreateAccount(Long personId, Long userId, Long projectId, String workType, Integer personWorkType) {
        SafetyScoreAccount account = getByPersonId(personId);
        if (account == null) {
            if (personWorkType != null) {
                account = createAccountByWorkType(personId, userId, projectId, personWorkType);
            } else {
                account = createAccount(personId, userId, projectId, workType);
            }
        }
        return account;
    }

    /**
     * 获取或创建账户（旧接口，保留兼容）
     */
    public SafetyScoreAccount getOrCreateAccount(Long personId, Long userId, Long projectId, String workType) {
        return getOrCreateAccount(personId, userId, projectId, workType, null);
    }

    /**
     * 根据人员ID获取账户
     */
    public SafetyScoreAccount getByPersonId(Long personId) {
        return baseMapper.selectByPersonId(personId);
    }

    /**
     * 根据积分和岗位类型判断颜色
     */
    public String getColorByScore(String workType, int score) {
        int greenThreshold = getGreenThreshold(workType);
        int yellowThreshold = getYellowThreshold(workType);
        if (score >= greenThreshold) {
            return "GREEN";
        } else if (score >= yellowThreshold) {
            return "YELLOW";
        } else {
            return "RED";
        }
    }

    /**
     * 获取绿码阈值
     */
    public int getGreenThreshold(String workType) {
        return switch (workType) {
            case WORK_TYPE_SPECIALIZED -> 12;
            case WORK_TYPE_SAFETY_ADMIN -> 10;
            default -> 12; // worker
        };
    }

    /**
     * 获取黄码阈值
     */
    public int getYellowThreshold(String workType) {
        return switch (workType) {
            case WORK_TYPE_SPECIALIZED -> 6;
            case WORK_TYPE_SAFETY_ADMIN -> 5;
            default -> 6; // worker
        };
    }

    /**
     * 根据人员ID判断安全码颜色
     */
    public String getColorByPersonId(Long personId) {
        SafetyScoreAccount account = getByPersonId(personId);
        if (account == null) {
            return "UNKNOWN";
        }
        return getColorByScore(account.getWorkType(), account.getCurrentScore());
    }

    /**
     * 查询需要年度清零的账户
     */
    public List<SafetyScoreAccount> getNeedsResetAccounts(Date date) {
        return baseMapper.selectNeedsReset(date);
    }

    /**
     * 执行年度清零
     */
    public void performAnnualReset(Date resetDate) {
        List<SafetyScoreAccount> accounts = getNeedsResetAccounts(resetDate);
        for (SafetyScoreAccount account : accounts) {
            account.setCurrentScore(account.getInitialScore());
            account.setAnnualResetDate(resetDate);
            updateById(account);
        }
    }
}
