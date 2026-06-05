package com.joyfishs.dawa.safety.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 积分变动结果DTO
 *
 * @author safe-edu
 * @since 2026-05-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ScoreChangeResult", description = "积分变动结果")
public class ScoreChangeResult {

    @ApiModelProperty("变动后的积分")
    private Integer afterScore;

    @ApiModelProperty("安全码颜色 GREEN/YELLOW/RED")
    private String color;

    @ApiModelProperty("是否需要触发培训")
    private Boolean needTraining;

    @ApiModelProperty("扣分分值（负数）/ 恢复分值（正数）")
    private Integer changeAmount;

    @ApiModelProperty("触发的再培训记录ID（needTraining=true时返回）")
    private Long retrainingRecordId;

    @ApiModelProperty("变动前积分")
    private Integer beforeScore;

    @ApiModelProperty("变动类型 deduct/restore/retraining/reset/adjust")
    private String changeType;
}
