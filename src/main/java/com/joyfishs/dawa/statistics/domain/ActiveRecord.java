package com.joyfishs.dawa.statistics.domain;

import java.time.LocalDate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangkaifeng
 */
@Data
@NoArgsConstructor
@ApiModel(value = "活跃人数")
public class ActiveRecord {

    @ApiModelProperty("日期")
    private LocalDate date;

    @ApiModelProperty("人数")
    private long count;

    public ActiveRecord(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }
}
