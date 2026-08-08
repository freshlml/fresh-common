package com.fresh.core.utils.bytes;

import java.util.Arrays;

public enum ByteUnitEnum {
    B("B", 1),
    KB("KB", 1024),
    MB("MB", 1024 * 1024),
    GB("GB", 1024 * 1024 * 1024),
    TB("TB", 1024 * 1024 * 1024 * 1024L);

    private final String value;
    private final long size;

    ByteUnitEnum(String value, long size) {
        this.value = value;
        this.size = size;
    }
    public long getSize() {
        return this.size;
    }

    public static ByteUnitEnum convert(String value) {
        return Arrays.stream(ByteUnitEnum.values()).filter(per -> per.value.equals(value)).findFirst().orElse(null);
    }

}
