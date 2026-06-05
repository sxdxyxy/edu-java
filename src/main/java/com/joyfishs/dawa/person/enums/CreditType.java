package com.joyfishs.dawa.person.enums;

/**
 * 积分类型
 * 1=浏览公告，2=观看视频，3=单次登录观看视频时长累计30分钟，4=做练习题，5=参与考试
 *
 **/
public enum CreditType {
    READ_BULLETIN(1, "浏览公告",5),
    WATCH_VIDEO(2, "观看视频",10),
    CUMULATIVE_WATCH_VIDEO(3, "单次登录观看视频时长累计30分钟",30),
    DO_EXERCISES(4, "做练习题",15),
    EXAM(5, "参与考试",25);

    int index;
    String desc;
    int points;

    CreditType(Integer index, String desc, int points) {
        this.index = index;
        this.desc = desc;
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public Integer getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }

    public static CreditType value(int index) {
        for (CreditType wt : values()) {
            if (wt.index == index) {
                return wt;
            }
        }
        return null;
    }
}
