package com.hs.enums;

public enum ErrorCode {
    SUCCESS(200, "成功"),
    FAILED(10000, "失败"),
    ERROR(20000, "错误"),
    USER_EXIST(1000, "用户已存在"),
    CODE_ERROR(1001, "短信验证码错误");

    private final int code;

    private final String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
