package com.joyfishs.dawa.person.enums;

/**
 * 亲属关系
 **/
public enum PersonRelation {
    FATHER(1, "父亲"),
    MOTHER(2, "母亲"),
    CHILDREN(3, "子女"),
    SPOUSE(4, "配偶"),
    RELATIVES(5, "亲人"),
    FRIEND(6, "朋友"),
    OTHER(7, "其它");

    int index;
    String desc;

    PersonRelation(Integer index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public static PersonRelation value(int index) {
        for (PersonRelation wt : values()) {
            if (wt.index == index) {
                return wt;
            }
        }
        return null;
    }

    public Integer getIndex() {
        return index;
    }

    public String getDesc() {
        return desc;
    }
}
