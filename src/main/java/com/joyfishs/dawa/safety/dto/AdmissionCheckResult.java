package com.joyfishs.dawa.safety.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 课程准入检查结果DTO
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "AdmissionCheckResult", description = "课程准入检查结果")
public class AdmissionCheckResult {

    @ApiModelProperty("是否通过检查")
    private Boolean passed;

    @ApiModelProperty("不通过原因")
    private String reason;

    @ApiModelProperty("警告信息（提示性，不阻止报名）")
    private List<String> warnings;

    @ApiModelProperty("安全码颜色")
    private String safetyCodeColor;

    @ApiModelProperty("当前积分")
    private Integer currentScore;

    public static AdmissionCheckResult pass() {
        return new AdmissionCheckResult(true, null, null, null, null);
    }

    public static AdmissionCheckResult fail(String reason) {
        return new AdmissionCheckResult(false, reason, null, null, null);
    }

    public static AdmissionCheckResult failWithWarnings(String reason, List<String> warnings) {
        return new AdmissionCheckResult(false, reason, warnings, null, null);
    }
}
