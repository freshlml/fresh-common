package com.fresh.common.utils.bytes;

import java.util.Arrays;

public enum ByteUnitEnum {
    B("B", 1),
    KB("KB", 1024),
    MB("MB", 1024 * 1024),
    GB("GB", 1024 * 1024 * 1024),
    TB("TB", 1024 * 1024 * 1024 * 1024);

    private String suffix;
    private long size;

    ByteUnitEnum(String suffix, long size) {
        this.suffix = suffix;
        this.size = size;
    }
    public long getSize() {return this.size;}

    public static ByteUnitEnum convert(String suffix) {
        return Arrays.stream(ByteUnitEnum.values()).filter(per -> per.suffix.equals(suffix)).findFirst().orElse(null);
    }

}
