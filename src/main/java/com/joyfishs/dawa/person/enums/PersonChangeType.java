package com.joyfishs.dawa.person.enums;

/** 删除状态 **/
public enum PersonChangeType {
    CREATE(0, "CREATE", "人员新增"),
    UPDATE(1, "UPDATE", "人员修改"),
    TRANSFER(2, "TRANSFER", "人员转移"),
    QUIT(3, "QUIT", "人员离职"),
    REJOIN(6, "REJOIN", "人员重新入职"),
    DELETE(4, "DELETE", "人员删除"),
    REGISTER(5, "REGISTER", "人员注册");

    Integer changeType;
    String reason;
    String reasonDesc;

    PersonChangeType(Integer changeType, String reason, String reasonDesc) {
        this.changeType = changeType;
        this.reason = reason;
        this.reasonDesc = reasonDesc;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public String getReason() {
        return reason;
    }

    public String getReasonDesc() {
        return reasonDesc;
    }
}
