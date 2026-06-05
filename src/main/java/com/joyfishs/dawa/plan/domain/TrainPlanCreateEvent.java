package com.joyfishs.dawa.plan.domain;

import com.joyfishs.dawa.plan.entity.TrainPlan;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "TrainPlanCreateEvent", description = "培训计划创建事件")
public class TrainPlanCreateEvent {

    private TrainPlan trainPlan;

    public TrainPlanCreateEvent(TrainPlan trainPlan) {
        this.trainPlan = trainPlan;
    }
}
