package com.fresh.core.utils.bytes;

import com.fresh.core.exception.BizException;
import com.fresh.core.utils.AssertUtils;
import com.fresh.core.utils.NumberUnitUtils;
import com.fresh.core.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteSize {

    private static final Pattern PATTERN = Pattern.compile("^([+\\-]?\\d+)([a-zA-Z]{0,2})$");

    private final long bytes;

    public ByteSize(long bytes) {
        this.bytes = bytes;
    }

    /**
     *
     * @param bytes 字节数
     * @return ByteSize
     */
    public static ByteSize ofBytes(long bytes) {
        return new ByteSize(bytes);
    }

    public static ByteSize ofBytes(long mount, ByteUnitEnum unit) {
        return new ByteSize(Math.multiplyExact(mount, unit.getSize()));
    }

    /**
     *
     * @param mount 多少个 KB
     * @throws ArithmeticException if the mount * KB overflows a long
     * @return ByteSize
     */
    public static ByteSize ofKBytes(long mount) {
        return ofBytes(mount, ByteUnitEnum.KB);
    }

    /**
     *
     * @param mount 多少个 MB
     * @throws ArithmeticException if the mount * MB overflows a long
     * @return ByteSize
     */
    public static ByteSize ofMBytes(long mount) {
        return new ByteSize(Math.multiplyExact(mount, ByteUnitEnum.MB.getSize()));
    }

    /**
     *
     * @param mount 多少个 GB
     * @throws ArithmeticException if the mount * GB overflows a long
     * @return ByteSize
     */
    public static ByteSize ofGBytes(long mount) {
        return new ByteSize(Math.multiplyExact(mount, ByteUnitEnum.GB.getSize()));
    }

    /**
     *
     * @param mount 多少个 TB
     * @throws ArithmeticException if the mount * TB overflows a long
     * @return ByteSize
     */
    public static ByteSize ofTBytes(long mount) {
        return new ByteSize(Math.multiplyExact(mount, ByteUnitEnum.TB.getSize()));
    }

    /**
     *
     * @param text the text to parse
     * @return ByteSize
     * @throws BizException          if text can not match PATTERN
     * @throws NullPointerException  if text is null
     * @throws NumberFormatException if the text does not contain a parsable number
     * @throws ArithmeticException   if the result overflows a long
     */
    public static ByteSize parse(CharSequence text) {
        return parse(text, null);
    }

    /**
     *
     * @param text the text to parse
     * @param unit the default ByteUnitEnum
     * @return ByteSize
     * @throws BizException          if text can not match PATTERN
     * @throws NullPointerException  if text is null
     * @throws NumberFormatException if the text does not contain a parsable number
     * @throws ArithmeticException   if the result overflows a long
     */
    public static ByteSize parse(CharSequence text, ByteUnitEnum unit) {
        AssertUtils.notNull(text, "text 文本不能为 null");
        ByteUnitEnum defaultUnit = unit != null ? unit : ByteUnitEnum.B;

        Matcher matcher = PATTERN.matcher(text);
        AssertUtils.isTrue(matcher.matches(),"text 不能匹配格式 pattern");

        String suffix = matcher.group(2);
        ByteUnitEnum unitNow = ByteUnitEnum.convert(suffix);
        AssertUtils.ifTrue( (unitNow == null && !StringUtils.isEmpty(suffix)), "text 文本单位错误");
        if(unitNow == null) unitNow = defaultUnit;

        long amount = Long.parseLong(matcher.group(1));
        return ByteSize.ofBytes(Math.multiplyExact(amount, unitNow.getSize()));  //may overflow
    }

    public long toBytes() {
        return this.bytes;
    }
    public long toKBytes() {
        return this.bytes / ByteUnitEnum.KB.getSize();
    }
    public long toMBytes() {
        return this.bytes / ByteUnitEnum.MB.getSize();
    }
    public long toGBytes() {
        return this.bytes / ByteUnitEnum.GB.getSize();
    }
    public long toTBytes() {
        return this.bytes / ByteUnitEnum.TB.getSize();
    }
    public boolean isNegative() {
        return this.bytes < 0;
    }

    @Override
    public String toString() {
        return this.bytes + "Bytes";
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

    public static void main(String[] argv) {
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
