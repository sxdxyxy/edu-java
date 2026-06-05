package com.joyfishs.dawa.person.enums;

/**
 * 工种
 * 1=普工，2=电工，3=电焊工，4=钢筋工，5=混凝土，6=架子工
 **/
public enum WorkType {
    GENERAL(1, "普工"),
    ELECTRICIAN(2, "电工"),
    WELDER(3, "焊工"),
    REBAR(4, "钢筋工"),
    CONCRETE(5, "混凝土"),
    SCAFFOLDER(6, "架子工"),
    MANAGE(7, "管理人员"),
    CARPENTER(8, "木工"),
    BRICKLAYER(9, "瓦工"),
    PUSH_MAN(10, "顶管工"),
    FORKLIFT(11, "叉车工"),
    LADDER_TRUCK(12, "登高车操作工"),
    PILOT(13, "驾驶员"),
    CRANE(14, "汽车吊司机"),
    EXCAVATOR(15, "挖机操作工"),
    SIGNAL_CABLEMAN(16, "信号司索工"),
    ROAD_ROLLER(17, "压路机"),
    PILE_DRIVER(18, "桩机操作手"),
    LOADER(19, "装载机操作工"),
    ;
    int index;
    String desc;

    WorkType(Integer index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public Integer getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }

    public static WorkType value(int index) {
        if (index == 0) {
            return null;
        }
        for (WorkType wt : values()) {
            if (wt.index == index) {
                return wt;
            }
        }
        return null;
    }
}
