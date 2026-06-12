package com.joyfishs.dawa.safety.service;

import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.safetycode.service.SafetyCodeService;
import com.joyfishs.dawa.person.service.PersonService;
import com.joyfishs.dawa.person.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 违章扣分后并行触发服务
 * <p>
 * 扣分完成后，并行触发闸机联动、通知推送、准入锁定等后续影响
 * </p>
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Slf4j
@Service
public class SafetyPostDeductService {

    @Autowired
    private SafetyCodeService safetyCodeService;

    @Autowired
    private SafetyRetrainingService retrainingService;

    @Autowired
    private AdmissionCheckService admissionCheckService;

    @Autowired
    private PersonService personService;

    @Autowired
    private SafetyNotificationService notificationService;

    /**
     * 闸机回调地址。由 application.yml 的 {@code safety.gate.callback-url} 注入。
     * 不同环境(dev/staging/prod)使用各自的闸机地址,避免本机回环。
     */
    @Value("${safety.gate.callback-url:http://127.0.0.1:8000/access/gate/callback}")
    private String gateCallbackUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 闸机联动：红码时通知闸机禁止通行
     * <p>
     * 闸机每次刷卡都会调用 GateController 的 /callback 接口实时检查安全码颜色。
     * 此方法额外发送一次通知，确保闸机侧已知晓状态变化。
     * 若闸机支持主动推送指令，也可在此调用闸机控制 API。
     * </p>
     *
     * @param personId 人员ID
     * @param projectId 项目ID
     * @param color 安全码颜色
     */
    @Async
    public void triggerGateAccess(Long personId, Long projectId, String color) {
        if ("RED".equals(color)) {
            log.info("红码触发闸机禁止通行: personId={}, projectId={}", personId, projectId);

            // 获取人员信息
            Person person = personService.getById(personId);
            if (person != null && projectId != null) {
                // 主动通知闸机（模拟闸机回调触发，重新检查该人员安全码）
                try {
                    Map<String, Object> request = new HashMap<>();
                    request.put("personId", personId);
                    request.put("projectId", projectId);
                    request.put("gateId", "SYSTEM_NOTIFY");
                    request.put("action", "notify");

                    ResponseEntity<?> resp = restTemplate.postForEntity(
                            gateCallbackUrl, request, Object.class);

                    log.info("闸机通知发送成功: personId={}, projectId={}, response={}",
                            personId, projectId, resp.getStatusCode());
                } catch (Exception e) {
                    // 闸机通知失败不影响主流程（闸机下次刷卡时仍会正确检查 DB 中的安全码颜色）
                    log.warn("闸机通知发送失败（不影响闸机实时检查）: personId={}, error={}",
                            personId, e.getMessage());
                }
            }
        } else {
            log.debug("非红码，不触发闸机禁止: personId={}, color={}", personId, color);
        }
    }

    /**
     * 安全码刷新：通知所有在线设备刷新该人员的安全码显示
     *
     * @param personId 人员ID
     * @param projectId 项目ID
     * @param color 安全码颜色
     */
    @Async
    public void refreshSafetyCode(Long personId, Long projectId, String color) {
        try {
            // 重新评估安全码颜色（确保 DB 状态最新）
            if (personId != null && projectId != null) {
                safetyCodeService.reevaluateColor(personId, projectId, null, null);
            }
            log.info("安全码刷新完成: personId={}, projectId={}, color={}", personId, projectId, color);
        } catch (Exception e) {
            log.warn("安全码刷新失败: personId={}, projectId={}", personId, projectId, e);
        }
    }

    /**
     * 锁定课程准入：再培训完成前禁止报名相关课程
     *
     * @param personId 人员ID
     * @param retrainingRecordId 再培训记录ID
     * @param violationName 违章名称
     */
    @Async
    public void lockCourseAdmission(Long personId, Long retrainingRecordId, String violationName) {
        if (retrainingRecordId == null) {
            return;
        }
        try {
            log.info("锁定课程准入: personId={}, retrainingRecordId={}, violation={}",
                    personId, retrainingRecordId, violationName);
            // AdmissionCheckService.hasPendingRetraining() 已实现，准入检查时会自动阻断
        } catch (Exception e) {
            log.warn("锁定课程准入失败: personId={}", personId, e);
        }
    }

    /**
     * 推送扣分通知：微信模板消息 / 短信
     *
     * @param personId 人员ID
     * @param violationName 违章名称
     * @param deductScore 扣分分值
     * @param currentScore 当前积分
     * @param newColor 新安全码颜色
     */
    @Async
    public void sendDeductionNotification(Long personId, String violationName,
            int deductScore, int currentScore, String newColor) {
        try {
            notificationService.sendDeductionNotification(personId, violationName,
                    deductScore, currentScore, newColor);
            log.info("扣分通知已发送: personId={}, violation={}", personId, violationName);
        } catch (Exception e) {
            log.warn("扣分通知发送失败: personId={}", personId, e);
        }
    }

    /**
     * 并行触发所有后续影响
     * <p>
     * 扣分完成后调用此方法，所有动作异步并行执行
     * </p>
     *
     * @param personId 人员ID
     * @param projectId 项目ID（可为null）
     * @param violationName 违章名称
     * @param deductScore 扣分分值
     * @param scoreResult 积分变动结果
     * @param retrainingRecordId 再培训记录ID（可为null）
     */
    public void triggerAll(Long personId, Long projectId, String violationName,
            int deductScore, ScoreChangeResult scoreResult, Long retrainingRecordId) {
        String color = scoreResult != null ? scoreResult.getColor() : null;
        int currentScore = scoreResult != null ? scoreResult.getAfterScore() : 0;

        log.info("并行触发扣分后续影响: personId={}, color={}, retrainingRecordId={}",
                personId, color, retrainingRecordId);

        // 闸机联动
        triggerGateAccess(personId, projectId, color);

        // 安全码刷新
        refreshSafetyCode(personId, projectId, color);

        // 锁定课程准入
        lockCourseAdmission(personId, retrainingRecordId, violationName);

        // 推送通知
        sendDeductionNotification(personId, violationName, deductScore, currentScore, color);
    }
}
