package com.fresh.core.enums;

public enum JsonResultEnum {
    SUCCESS("1", "SUCCESS"),
    FAIL("-1", "FAIL"),
    PERMISSION_DENIED("403", "403");

    private final String code;
    private final String text;

    JsonResultEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }
    public String getText() {
        return text;
    }
}
