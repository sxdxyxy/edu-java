package com.joyfishs.system.enums;

/**
 * 短信类型
 *
 * @author ykfnb*/
public enum SmsType {
    REGISTER("register", "1804456"),
    LOGIN("login", "1804456"),
    CHANGE_MOBILE("changeMobile", "1804970");

    private final String value;

    private final String templateId;

    SmsType(String value, String templateId) {
        this.value = value;
        this.templateId = templateId;
    }

    public String getValue() {
        return value;
    }

    public String getTemplateId() {
        return templateId;
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static SmsType getSmsType(String value) {
        SmsType[] smsTypeArr = SmsType.values();
        for (int i = 0; i < smsTypeArr.length; i++) {
            if (smsTypeArr[i].getValue().equals(value)) {
                return smsTypeArr[i];
            }
        }
        return null;
    }
}
