package com.joyfishs.dawa.statistics.domain;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangkaifeng
 */
@Data
@ApiModel(value = "首页动态")
@NoArgsConstructor
public class EventsList {

    @ApiModelProperty("事件时间")
    private LocalDateTime eventTime ;

    @ApiModelProperty("事件对象")
    private String target ;

    @ApiModelProperty("事件内容")
    private String event ;

    public EventsList(LocalDateTime eventTime, String target, String event) {
        this.eventTime = eventTime;
        this.target = target;
        this.event = event;
    }
}
