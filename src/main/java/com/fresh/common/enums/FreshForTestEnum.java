package com.fresh.common.enums;


public enum FreshForTestEnum {
    SYSTEM("SYSTEM", "系统");

    private String value;
    private String text;

    FreshForTestEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

}
