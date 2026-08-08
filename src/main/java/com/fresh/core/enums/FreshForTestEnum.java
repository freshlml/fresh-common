package com.fresh.core.enums;


public enum FreshForTestEnum {
    SYSTEM("SYSTEM", "系统");

    private final String value;
    private final String text;

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
