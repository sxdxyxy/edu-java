package com.joyfishs.dawa.violation.dto;

import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 违章即扣分完整结果
 * <p>
 * 违章录入后返回完整的处理结果，包含违章记录、积分变动、
 * 再培训信息、闸机联动状态等
 * </p>
 *
 * @author safe-edu
 * @since 2026-05-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ViolationAndDeductResult", description = "违章即扣分完整结果")
public class ViolationAndDeductResult {

    @ApiModelProperty("违章记录")
    private ViolationRecord record;

    @ApiModelProperty("积分变动结果")
    private ScoreChangeResult scoreResult;

    @ApiModelProperty("安全码颜色（扣分后）")
    private String safetyCodeColor;

    @ApiModelProperty("闸机联动结果")
    private GateResult gateResult;

    @ApiModelProperty("通知推送结果")
    private Boolean notificationSent;

    @ApiModelProperty("再培训记录ID（触发培训时）")
    private Long retrainingRecordId;

    @ApiModelProperty("课程准入是否已锁定")
    private Boolean admissionLocked;

    @ApiModelProperty("整体是否成功")
    private Boolean success;

    @ApiModelProperty("失败原因（success=false时返回）")
    private String errorMessage;

    @ApiModelProperty("违章记录ID（快捷引用）")
    private Long violationId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value = "GateResult", description = "闸机联动结果")
    public static class GateResult {
        @ApiModelProperty("是否发送成功")
        private Boolean success;

        @ApiModelProperty("闸机响应消息")
        private String message;

        @ApiModelProperty("安全码颜色")
        private String color;

        @ApiModelProperty("是否允许通行")
        private Boolean allow;
    }

    /**
     * 快速构建成功结果
     */
    public static ViolationAndDeductResult success(
            ViolationRecord record,
            ScoreChangeResult scoreResult,
            Long retrainingRecordId,
            Boolean admissionLocked) {
        ViolationAndDeductResult result = new ViolationAndDeductResult();
        result.setSuccess(true);
        result.setRecord(record);
        result.setViolationId(record.getId());
        result.setScoreResult(scoreResult);
        result.setSafetyCodeColor(scoreResult != null ? scoreResult.getColor() : null);
        result.setRetrainingRecordId(retrainingRecordId);
        result.setAdmissionLocked(admissionLocked);
        return result;
    }

    /**
     * 快速构建失败结果
     */
    public static ViolationAndDeductResult error(String message) {
        ViolationAndDeductResult result = new ViolationAndDeductResult();
        result.setSuccess(false);
        result.setErrorMessage(message);
        return result;
    }
}
