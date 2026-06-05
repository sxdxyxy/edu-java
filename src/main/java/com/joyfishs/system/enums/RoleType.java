package com.joyfishs.system.enums;

/**
 * 角色类型
 *
 * @author ykfnb*/
public enum RoleType {
    /**
     * 学员
     */
    STUDENT("student" ),
    /**
     * 系统管理
     */
    MANAGER("platform_manager");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static RoleType getType(String value) {
        RoleType[] typeArr = RoleType.values();
        for (int i = 0; i < typeArr.length; i++) {
            if (typeArr[i].getValue().equals(value)) {
                return typeArr[i];
            }
        }
        return null;
    }
}
