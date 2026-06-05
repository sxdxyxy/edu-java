package com.joyfishs.dawa.safety.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safety.entity.SafetyScoreAccount;
import com.joyfishs.dawa.safety.entity.SafetyScoreTransaction;
import com.joyfishs.dawa.safety.service.SafetyScoreAccountService;
import com.joyfishs.dawa.safety.service.SafetyScoreService;
import com.joyfishs.dawa.safety.service.SafetyScoreTransactionService;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全积分控制器
 * 提供安全积分查询、扣分、恢复等REST API
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/safety/score")
@Api(tags = "安全积分管理")
public class SafetyScoreController {

    @Autowired
    private SafetyScoreService safetyScoreService;

    @Autowired
    private SafetyScoreAccountService safetyScoreAccountService;

    @Autowired
    private SafetyScoreTransactionService scoreTransactionService;

    @Autowired
    private SafetyCodeService safetyCodeService;

    @GetMapping("/person/{personId}")
    @ApiOperation("获取人员安全积分信息")
    public AjaxResult<SafetyScoreAccount> getScoreByPersonId(@PathVariable Long personId) {
        SafetyScoreAccount account = safetyScoreAccountService.getByPersonId(personId);
        if (account == null) {
            return AjaxResult.error("未找到安全积分账户");
        }
        account.setColor(safetyScoreService.getSafetyCodeColor(personId));
        return AjaxResult.success(account);
    }

    @GetMapping("/person/{personId}/color")
    @ApiOperation("获取人员安全码颜色")
    public AjaxResult<String> getSafetyCodeColor(@PathVariable Long personId) {
        String color = safetyScoreService.getSafetyCodeColor(personId);
        return AjaxResult.success(color);
    }

    @GetMapping("/person/{personId}/current")
    @ApiOperation("获取人员当前积分")
    public AjaxResult<Integer> getCurrentScore(@PathVariable Long personId) {
        Integer score = safetyScoreService.getCurrentScore(personId);
        return AjaxResult.success(score);
    }

    @PostMapping("/deduct")
    @ApiOperation("违章扣分")
    public AjaxResult<ScoreChangeResult> deductScore(
            @RequestParam Long personId,
            @RequestParam String violationCode,
            @RequestParam(required = false) Long operatorId) {
        ScoreChangeResult result = safetyScoreService.deductScore(personId, violationCode, operatorId);
        if (result == null) {
            return AjaxResult.error("扣分失败，请检查违章类型是否正确");
        }
        return AjaxResult.success(result);
    }

    @PostMapping("/restore")
    @ApiOperation("恢复积分")
    public AjaxResult<ScoreChangeResult> restoreScore(
            @RequestParam Long personId,
            @RequestParam Integer restoreAmount,
            @RequestParam String reason,
            @RequestParam(required = false) Long operatorId) {
        ScoreChangeResult result = safetyScoreService.restoreScore(personId, restoreAmount, reason, operatorId);
        if (result == null) {
            return AjaxResult.error("恢复积分失败，请检查人员是否有积分账户");
        }
        return AjaxResult.success(result);
    }

    @PostMapping("/restore/retraining")
    @ApiOperation("安全再培训完成后恢复积分")
    public AjaxResult<ScoreChangeResult> restoreAfterRetraining(
            @RequestParam Long personId,
            @RequestParam Long retrainingRecordId,
            @RequestParam(required = false) Long operatorId) {
        ScoreChangeResult result = safetyScoreService.restoreAfterRetraining(personId, retrainingRecordId, operatorId);
        if (result == null) {
            return AjaxResult.error("恢复积分失败，请检查人员是否有积分账户");
        }
        return AjaxResult.success(result);
    }

    @PostMapping("/account/create")
    @ApiOperation("创建安全积分账户")
    public AjaxResult<SafetyScoreAccount> createAccount(
            @RequestParam Long personId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(defaultValue = "worker") String workType) {
        SafetyScoreAccount account = safetyScoreAccountService.createAccount(personId, userId, projectId, workType);
        return AjaxResult.success(account);
    }

    @GetMapping("/list")
    @ApiOperation("获取安全积分账户列表")
    public AjaxResult<Map<String, Object>> list(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Page<SafetyScoreAccount> page = new Page<>(pageNum, pageSize);
        Page<SafetyScoreAccount> result = safetyScoreAccountService.page(page);

        for (SafetyScoreAccount account : result.getRecords()) {
            account.setColor(safetyScoreService.getSafetyCodeColor(account.getPersonId()));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("rows", result.getRecords());

        return AjaxResult.success(data);
    }

    @GetMapping("/transaction/{personId}")
    @ApiOperation("获取人员积分变动记录")
    public AjaxResult<List<SafetyScoreTransaction>> getTransaction(@PathVariable Long personId) {
        List<SafetyScoreTransaction> transactions = scoreTransactionService.getByPersonId(personId);
        return AjaxResult.success(transactions);
    }

    @PostMapping("/refresh/all")
    @ApiOperation("刷新所有安全码颜色")
    public AjaxResult<?> refreshAllColors() {
        log.info("手动触发刷新所有安全码颜色");
        // 获取所有账户并刷新颜色
        List<SafetyScoreAccount> accounts = safetyScoreAccountService.list();
        int count = 0;
        for (SafetyScoreAccount account : accounts) {
            try {
                String color = safetyScoreService.getSafetyCodeColor(account.getPersonId());
                count++;
            } catch (Exception e) {
                log.warn("刷新安全码颜色失败: personId={}", account.getPersonId(), e);
            }
        }
        return AjaxResult.success("已刷新 " + count + " 个账户的安全码颜色");
    }
}
