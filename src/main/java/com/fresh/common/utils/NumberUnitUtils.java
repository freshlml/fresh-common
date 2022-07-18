package com.fresh.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public abstract class NumberUnitUtils {

    private static final Map<Integer, String> CONV = new HashMap<>();
    private static final List<Integer> defaultSkips = new ArrayList<>();
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    //Number Type Cache
    private static final Set<Class<? extends Number>> NUMBER_TYPE_CACHE;

    static {
        //CONV.put(-6, "微");      //index=-6, 10^-6
        //CONV.put(-3, "毫");      //index=-3, 10^-3
        //CONV.put(-2, "厘");      //index=-2, 10^-2
        //CONV.put(-1, "分");      //index=-1, 10^-1
        CONV.put(0, "");       //index=0, 10^0
        CONV.put(1, "十");       //index=1, 10^1
        CONV.put(2, "百");       //index=2, 10^2
        CONV.put(3, "K");       //index=3,  10^3
        CONV.put(4, "W");       //index=4,  10^4
        CONV.put(5, "十万");      //index=5,  10^5
        CONV.put(6, "百万");      //index=6,  10^6
        CONV.put(7, "千万");      //index=7,  10^7
        CONV.put(8, "亿");       //index=8,  10^8
        CONV.put(9, "十亿");      //index=9,  10^9
        CONV.put(10, "百亿");     //index=10,  10^10
        CONV.put(11, "千亿");     //index=11,  10^11
        CONV.put(12, "万亿");     //index=12,  10^12
        CONV.put(13, "十万亿");    //index=13,  10^13
        CONV.put(14, "百万亿");    //index=14,  10^14
        CONV.put(15, "千万亿");    //index=15,  10^15
        CONV.put(16, "亿亿");     //index=16,  10^16

        defaultSkips.add(1);
        defaultSkips.add(2);
        defaultSkips.add(5);
        defaultSkips.add(6);
        defaultSkips.add(7);
        defaultSkips.add(9);
        defaultSkips.add(10);
        defaultSkips.add(11);
        defaultSkips.add(13);
        defaultSkips.add(14);
        defaultSkips.add(15);


        Set<Class<? extends Number>> numberTypes = new HashSet<>(8);
        numberTypes.add(Byte.class);
        numberTypes.add(Short.class);
        numberTypes.add(Integer.class);
        numberTypes.add(Long.class);
        numberTypes.add(BigInteger.class);
        numberTypes.add(Float.class);
        numberTypes.add(Double.class);
        numberTypes.add(BigDecimal.class);
        NUMBER_TYPE_CACHE = Collections.unmodifiableSet(numberTypes);
    }

    public static String convertUnitNumber(Integer fromNum, Integer topNum, Long numVal) {
        return convertUnitNumber(fromNum, topNum, numVal, defaultSkips, 12);
    }
    //[fromNum, topNum]
    public static String convertUnitNumber(Integer fromNum, Integer topNum, Long numVal, List<Integer> skips, int remainderIgnoreFrom) {
        if(numVal == null || numVal <= 0) return "0";

        Integer p = topNum;
        String retVal = numVal + "";
        for(; p>=fromNum; p--) {
            if(skips.contains(p) || !CONV.containsKey(p)) continue;

            Long bian = pow10(p);
            if(bian <= numVal) {
                Long zs = numVal / bian;
                retVal = zs + "";

                if(p > 0 && p < remainderIgnoreFrom) {
                    Long remain = remainder(numVal % bian, p - 1);
                    if (remain != -1) {
                        retVal += "." + remain;
                    }
                }

                retVal += CONV.get(p);
                break;
            }
        }
        return retVal;
    }

    private static Long remainder(Long numRemain, Integer p) {
        Long pow = pow10(p);
        if(numRemain >= pow) {
            return numRemain / pow;
        } else {
            return -1L;
        }
    }
    private static Long pow10(int num) {
        return Double.valueOf(Math.pow(10, num)).longValue();
    }

    /*
     *Number类型之间相互转换
     *继承Number的子类型(标准库)有: Byte,Short,Integer,Long,BigInteger,Float,Double,BigDecimal
     *
     *
     *整数补码的截断与补值 (整数的强制类型转换，eg:byte i = (short) 128)
     * 0:      0000 0000                0000 0000 | 0000 0000
     * 1:      0000 0001                0000 0000 | 0000 0001
     * 127:    0111 1111                0000 0000 | 0111 1111
     *
     * -1:     1111 1111                1111 1111 | 1111 1111
     * -126:   1000 0010                1111 1111 | 1000 0010
     * -127:   1000 0001                1111 1111 | 1000 0001
     * -128: 1 1000 0000                1111 1111 | 1000 0000
     *1.大 -> 小， 补码截断，当数值未超过"小类型"的边界时，可以得到正确的数值
     *2.小 -> 大， 正数-补0;负数-补1，可以得到正确的数值
     *
     *
     *number转换成整数(Byte,Short,Integer,Long)
     * 1.number是整数，eg Long#intValue
     *   当number数值未超过target的边界时，得到正确的数值。当number数值超过target的边界时，抛异常
     *
     * 2.BigInteger转换成整数: BigInteger#byteValue,#shortValue,#intValue,#longValue
     *   当数值未超界时，可以得到正确的数值。当数值超界时，抛异常
     * 3.BigDecimal转换成整数: BigDecimal#toBigInteger
     *   得到精确的整数部分
     *
     * 4.浮点数强制转换成整数: 浮点数的存储 {@link com.fresh.common.flt.FloatTest}
     *   取该浮点数在浮点数表中对应值的整数部分（不一定精确）
     *
     *
     *number转换成BigInteger
     * 1.BigDecimal转换成BigInteger: BigDecimal#toBigInteger
     * 2.整数转换成BigInteger:   BigInteger完全能容纳
     * 3.浮点数转换成BigInteger: 取该浮点数在浮点数表中对应值的整数部分（不一定精确），取出的整数部分如果超过long,将发生截断
     *
     *
     *number转换成BigDecimal
     * 1.BigInteger转换成BigDecimal，小数部分为0
     * 2.整数转换成BigDecimal，小数部分为0
     * 3.浮点数转换成BigDecimal，取该浮点数在浮点数表中对应值（不一定精确）构造BigDecimal，非精确
     *
     *
     *number转换成浮点数
     * 1.number是浮点数，大->小且超界undefined
     * 2.整数转换成浮点数: Float#valueOf, Long的最大值不超过Float
     * 3.BigInteger转换成浮点数: 超界undefined
     * 4.BigDecimal转换成浮点数: 超界undefined
     *
     *
     * @param number Number类型的源对象
     * @param targetClazz 目标类型
     * @return 转换后的值
     */
    public static <T extends Number> T convertNumberToTargetClazz(Number number, Class<T> targetClazz) {
        AssertUtils.ifTrue(number==null, () -> "参数number[Number]不能为空", null);
        AssertUtils.ifTrue(targetClazz==null, () -> "参数clazz[Class]不能为空", null);

        if(targetClazz.isInstance(number)) {
            return (T) number;
        } else if(Byte.class == targetClazz || Byte.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            AssertUtils.isTrue(l >= Byte.MIN_VALUE && l <= Byte.MAX_VALUE, "参数number["+number.getClass().getName()+"]的值{"+l+"}大于["+targetClazz.getName()+"]允许的最大值");
            return (T) Byte.valueOf(number.byteValue());
        } else if(Short.class == targetClazz || Short.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            AssertUtils.isTrue(l >= Short.MIN_VALUE && l <= Short.MAX_VALUE, "参数number["+number.getClass().getName()+"]的值{"+l+"}大于["+targetClazz.getName()+"]允许的最大值");
            return (T) Short.valueOf(number.shortValue());
        } else if(Integer.class == targetClazz || Integer.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            AssertUtils.isTrue(l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE, "参数number["+number.getClass().getName()+"]的值{"+l+"}大于["+targetClazz.getName()+"]允许的最大值");
            return (T) Integer.valueOf(number.intValue());
        } else if(Long.class == targetClazz || Long.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            return (T) Long.valueOf(l);
        } else if(BigInteger.class == targetClazz) {
            if(number instanceof  BigDecimal) {
                return (T) ((BigDecimal) number).toBigInteger();
            } else {
                return (T) BigInteger.valueOf(number.longValue());
            }
        } else if(Float.class == targetClazz || Float.TYPE == targetClazz) {
            return (T) Float.valueOf(number.floatValue());
        } else if(Double.class == targetClazz || Double.TYPE == targetClazz) {
            return (T) Double.valueOf(number.doubleValue());
        } else if(BigDecimal.class == targetClazz) {
            return (T) new BigDecimal(number.toString());
        } else {
            AssertUtils.ifTrue(true, () -> "参数number["+number.getClass().getName()+"]不能转化为"+targetClazz.getName(), null);
        }
        return null;
    }

    private static long resolveLongValue(Number number, Class<? extends Number> clazz) {
        BigInteger bigInteger  = null;
        if(number instanceof BigDecimal) {
            bigInteger = ((BigDecimal) number).toBigInteger();
        } else if(number instanceof BigInteger) {
            bigInteger = (BigInteger)number;
        }
        if (bigInteger != null && (bigInteger.compareTo(LONG_MIN) < 0 || bigInteger.compareTo(LONG_MAX) > 0)) {
            final String ngiStr = bigInteger.toString();
            AssertUtils.ifTrue(true, () -> "参数number["+number.getClass().getName()+"]的数值{"+ngiStr+"}大于参数clazz["+clazz.getName()+"]允许的最大值", null);
        }
        return number.longValue();
    }

    /*
     *将字符串形式的数值转化为指定Number
     *继承Number的子类型(标准库)有: Byte,Short,Integer,Long,BigInteger,Float,Double,BigDecimal
     *16进制整数前缀: 0x,0X,#
     *
     *转换成整数
     * text表示的数值超界或者格式不能解析时，NumberFormatException
     *转换成BigInteger，BigDecimal
     * text格式不能解析时，NumberFormatException
     *转换成浮点数
     * text表示的数值超界时undefined, text格式不能解析时，NumberFormatException
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Number> T parseTextToTargetNumber(String text, Class<T> clazz) {
        AssertUtils.ifTrue(text==null, () -> "text[String]不能为空", null);
        AssertUtils.ifTrue(clazz==null, () -> "参数clazz[Class]不能为空", null);

        String trimedText = StringUtils.trimAllWhitespace(text);
        if(Byte.class == clazz || Byte.TYPE == clazz) {
            return (T) (StringUtils.isHexNumber(trimedText) ? Byte.decode(trimedText) : Byte.valueOf(trimedText));
        } else if (Short.class == clazz || Short.TYPE == clazz) {
            return (T) (StringUtils.isHexNumber(trimedText) ? Short.decode(trimedText) : Short.valueOf(trimedText));
        } else if (Integer.class == clazz || Integer.TYPE == clazz) {
            return (T) (StringUtils.isHexNumber(trimedText) ? Integer.decode(trimedText) : Integer.valueOf(trimedText));
        } else if (Long.class == clazz || Long.TYPE == clazz) {
            return (T) (StringUtils.isHexNumber(trimedText) ? Long.decode(trimedText) : Long.valueOf(trimedText));
        } else if (BigInteger.class == clazz) {
            return (T) (StringUtils.isHexNumber(trimedText) ? decodeBigInteger(trimedText) : new BigInteger(trimedText));
        } else if (Float.class == clazz || Float.TYPE == clazz) {
            return (T) Float.valueOf(trimedText);
        } else if (Double.class == clazz || Double.TYPE == clazz) {
            return (T) Double.valueOf(trimedText);
        } else if (BigDecimal.class == clazz || Number.class == clazz) {
            return (T) new BigDecimal(trimedText);
        } else {
            AssertUtils.ifTrue(true, () -> "参数text["+text+"]不能解析成"+clazz.getName(), null);
        }
        return null;
    }

    private static BigInteger decodeBigInteger(String value) {
        boolean negative = false;
        int radix = 10;
        int index = 0;

        char firstChar = value.charAt(0);
        if (firstChar == '-') {
            negative = true;
            index++;
        } else if (firstChar == '+')
            index++;

        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            radix = 16;
            index += 2;
        }
        else if (value.startsWith("#", index)) {
            radix = 16;
            index++;
        }
        else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }
        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }


    public static <T extends Number> T convertToNumber(Object o, Class<T> clazz) {
        //AssertUtils.notNull(o, "参数o不能为空");

        if(o instanceof Number) {
            return convertNumberToTargetClazz((Number)o, clazz);
        }
        if(o instanceof String) {
            return parseTextToTargetNumber((String)o, clazz);
        }
        return null;
    }

    public static <T extends Number> T convertToNumber(Object o, Class<T> clazz, T defaultValue) {
        T value = convertToNumber(o, clazz);

        if (value != null) return value;
        else return defaultValue;
    }

    /*
     *
     * @param o
     * @throws NumberFormatException
     * @return
     */
    @Deprecated
    public static Long convert2Long(Object o) {
        if(o == null) throw new NumberFormatException("不能为null");
        if(o instanceof Number) return ((Number) o).longValue();
        return Long.parseLong(String.valueOf(o));
    }
    /*
     *
     * @param o
     * @throws NumberFormatException
     * @return
     */
    @Deprecated
    public static Long convert2Long(Object o, Long nullDefault) {
        if(o == null) return nullDefault;
        if(o instanceof Number) return ((Number) o).longValue();
        return Long.parseLong(String.valueOf(o));
    }

    public static boolean convert2Bool(Object o) {
        if(o == null) return false;
        if (o.equals(Boolean.FALSE) || o instanceof String && ((String)o).equalsIgnoreCase("false")) {
            return false;
        }
        if (o.equals(Boolean.TRUE) || o instanceof String && ((String)o).equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }



    /*
     * 向上取最接近的2^
     * @param n
     * @return
     */
    public static final int closestPower(int n) {
        if(n < 0) return 1;
        if(n >= MAXIMUM_CAPACITY) return MAXIMUM_CAPACITY;
        int c = n - 1; //n已经是 2^m次方的情况
        c |= c >>> 1;
        c |= c >>> 2;
        c |= c >>> 4;
        c |= c >>> 8;
        c |= c >>> 16;  //最多移16位的原因，与MAXIMUM_CAPACITY最大值相关
        return (c < 0) ? 1 : (c >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : c + 1;
    }

}
