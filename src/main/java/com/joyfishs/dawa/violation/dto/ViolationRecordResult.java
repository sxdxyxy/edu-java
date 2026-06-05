package com.joyfishs.dawa.violation.dto;

import com.joyfishs.dawa.safety.dto.ScoreChangeResult;
import com.joyfishs.dawa.violation.entity.ViolationRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 违章录入结果（违章记录 + 积分联动结果）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ViolationRecordResult", description = "违章录入结果")
public class ViolationRecordResult {

    @ApiModelProperty("违章记录")
    private ViolationRecord record;

    @ApiModelProperty("积分变动结果（违章触发扣分时返回）")
    private ScoreChangeResult scoreResult;
}
