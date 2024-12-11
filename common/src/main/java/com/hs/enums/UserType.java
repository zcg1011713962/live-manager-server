package com.hs.enums;

public enum UserType {
    PATIENT("patient", "患者"),
    DOCTOR("doctor", "医生"),
    ADMIN("admin", "管理员");


    private final String type;

    private final String desc;

    UserType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
