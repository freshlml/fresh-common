package com.fresh.common.utils.bytes;

import com.fresh.common.exception.BizException;
import com.fresh.common.utils.AssertUtils;
import com.fresh.common.utils.NumberUnitUtils;
import com.fresh.common.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *封装字节数，eg: 1B, 2KB, 3MB, 4GB, 5TB
 *
 */
public class ByteSize {

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;
    private static final Pattern PATTERN = Pattern.compile("^([+\\-]?\\d+)([a-zA-Z]{0,2})$");

    private final long bytes;

    public ByteSize(long bytes) {
        this.bytes = bytes;
    }

    /**
     *
     * @param bytes 字节数
     * @return
     */
    public static ByteSize ofBytes(long bytes) {
        return new ByteSize(bytes);
    }

    /**
     *
     * @param mount 多少个KB
     * @throws ArithmeticException if the mount * KB overflows a long
     * @return
     */
    public static ByteSize ofKBytes(long mount) {
        return new ByteSize(Math.multiplyExact(mount, KB));
    }

    /**
     *
     * @param mount 多少个MB
     * @throws ArithmeticException if the mount * MB overflows a long
     * @return
     */
    public static ByteSize ofMBytes(long mount) {
        return new ByteSize(Math.multiplyExact(mount, MB));
    }

    /**
     *
     * @param mount 多少个GB
     * @throws ArithmeticException if the mount * GB overflows a long
     * @return
     */
    public static ByteSize ofGBytes(long mount) {
        return new ByteSize(Math.multiplyExact(mount, GB));
    }

    /**
     *
     * @param mount 多少个TB
     * @throws ArithmeticException if the mount * TB overflows a long
     * @return
     */
    public static ByteSize ofTBytes(long mount) {
        return new ByteSize(Math.multiplyExact(mount, TB));
    }

    /**
     *
     * @param text
     * @throws IllegalArgumentException 当数值超过long
     * @return
     */
    public static ByteSize parse(CharSequence text) {
        return parse(text, null);
    }
    public static ByteSize parse(CharSequence text, ByteUnitEnum unit) {
        AssertUtils.notNull(text, "text文本不能为null");
        ByteUnitEnum defaultUnit = unit != null ? unit : ByteUnitEnum.B;

        Matcher matcher = PATTERN.matcher(text);
        AssertUtils.isTrue(matcher.matches(),"text不能匹配格式pattern");

        String suffix = matcher.group(2);
        ByteUnitEnum unitNow = ByteUnitEnum.convert(suffix);
        AssertUtils.ifTrue( (unitNow == null && !StringUtils.isEmpty(suffix)), "text文本单位错误");
        if(unitNow == null) unitNow = defaultUnit;

        try {
            Long amount = Long.valueOf(matcher.group(1)); //group(1)可能超过long
            return ByteSize.ofBytes(Math.multiplyExact(amount, unitNow.getSize()));
        } catch (Exception e) {
            throw new IllegalArgumentException("'" + text + "' is too large", e);
        }
    }

    public long toBytes() { return this.bytes; }
    public long toKBytes() { return this.bytes / KB; }
    public long toMBytes() { return this.bytes / MB; }
    public long toGBytes() { return this.bytes / GB; }
    public long toTBytes() { return this.bytes / TB; }
    public boolean isNegative() { return this.bytes < 0; }

    @Override
    public String toString() {
        return this.bytes + "Byte";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteSize byteSize = (ByteSize) o;
        return this.bytes == byteSize.bytes;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.bytes);
    }

    public static void main(String argv[]) {
        //ByteSize.parse("");
        //ByteSize.parse("123");
        //ByteSize.parse("123H");
        //ByteSize.parse("123MB");
        //ByteSize.parse("9223372036854775808");
        //ByteSize.parse("9223372036854775807KB");

        ByteSize s = ByteSize.parse("111111111KB");
        System.out.println(s.toBytes());
        try {
            int bytes = NumberUnitUtils.convertNumberToTargetClazz(s.toBytes(), int.class);
            byte[] bs = new byte[bytes];
            System.out.println(bs.length);
        } catch (BizException e) {
            System.out.println(e);
        }

    }


}
