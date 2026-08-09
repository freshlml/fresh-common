package com.fresh.core.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public final class NumberUnitUtils {

    private NumberUnitUtils() {}

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

    /**
     * <p>数值（Byte, Short, Integer, Long, Float, Double, BigInteger, BigDecimal）之间相互类型转换.</p>
     *
     * <ul>
     *   <li>目标类型是整数（Byte, Short, Integer, Long）
     *      <ul>
     *          <li>如果源类型是整数类型或 BigInteger，当数值未超界时，可以得到正确的数值；否则，抛异常</li>
     *          <li>如果原类型是浮点类型或 BigDecimal，先将小数部分和小数点去掉得到整数部分，当数值未超界时，可以得到正确的数值；否则，抛异常</li>
     *      </ul>
     *   </li>
     *   <li>目标类型是浮点数（Float, Double）
     *       <ul>
     *           <li>当数值超过浮点数的表示范围时，结果为 ∞</li>
     *       </ul>
     *   </li>
     * </ul>
     *
     * @param number the source number
     * @param targetClazz the clazz of the target type
     * @return target number
     * @param <T> the target type
     * @throws NullPointerException if the source number or the target clazz is null
     * @throws IllegalArgumentException 如果目标类型是整数且原数值超过该整数类型的表示范围时抛出此异常
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T convertNumberToTargetClazz(Number number, Class<T> targetClazz) {
        Assert.notNull(number, "参数 number 不能为空");
        Assert.notNull(targetClazz, "参数 targetClazz 不能为空");

        if(targetClazz.isInstance(number)) {
            return (T) number;
        } else if(Byte.class == targetClazz || Byte.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            Assert.isTrue(l >= Byte.MIN_VALUE && l <= Byte.MAX_VALUE, "参数 number[" + number.getClass().getName() + "]的值{" + l + "}超过[" + targetClazz.getName() + "]的表示范围");
            return (T) Byte.valueOf(number.byteValue());
        } else if(Short.class == targetClazz || Short.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            Assert.isTrue(l >= Short.MIN_VALUE && l <= Short.MAX_VALUE, "参数 number[" + number.getClass().getName() + "]的值{" + l + "}超过[" + targetClazz.getName() + "]的表示范围");
            return (T) Short.valueOf(number.shortValue());
        } else if(Integer.class == targetClazz || Integer.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            Assert.isTrue(l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE, "参数 number[" + number.getClass().getName() + "]的值{" + l + "}超过[" + targetClazz.getName() + "]的表示范围");
            return (T) Integer.valueOf(number.intValue());
        } else if(Long.class == targetClazz || Long.TYPE == targetClazz) {
            long l = resolveLongValue(number, targetClazz);
            return (T) Long.valueOf(l);
        } else if(BigInteger.class == targetClazz) {
            if(number instanceof BigDecimal) {
                return (T) ((BigDecimal) number).toBigInteger();
            } else {
                return (T) BigInteger.valueOf(number.longValue());
            }
        } else if(Float.class == targetClazz || Float.TYPE == targetClazz) {
            return (T) Float.valueOf(number.floatValue());
        } else if(Double.class == targetClazz || Double.TYPE == targetClazz) {
            return (T) Double.valueOf(number.doubleValue());
        } else if(BigDecimal.class == targetClazz) {
            if(number instanceof Float) {
                return (T) new BigDecimal((Float) number);
            } else if(number instanceof Double) {
                return (T) new BigDecimal((Double) number);
            } else if(number instanceof BigInteger) {
                return (T) new BigDecimal((BigInteger) number);
            } else {
                return (T) new BigDecimal(number.longValue());
            }
        } else {
            throw new IllegalStateException("不支持的类型[" + targetClazz.getName() + "]");
        }
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
            throw new IllegalArgumentException("参数 number[" + number.getClass().getName() + "]的数值{" + ngiStr + "}超过参数 clazz[" + clazz.getName() + "]的表示范围");
        }
        return number.longValue();
    }

    /**
     * 整数字面量（字符串形式）转化为整数数值类型。
     *
     * @param text the specified text
     * @param clazz 目标类型 Clazz
     * @return 转化得到的数值
     * @param <T> 目标类型
     * @throws NullPointerException if the specified text or clazz is null
     * @throws NumberFormatException if the text can not convert to a number
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseTextToTargetNumber(String text, Class<T> clazz) {
        Assert.notNull(text, "参数 text 不能为空");
        Assert.notNull(clazz, "参数 clazz 不能为空");

        String trimedText = StringUtils.trimAllWhitespace(text);

        if(Byte.class == clazz || Byte.TYPE == clazz) {
            return (T) Byte.decode(trimedText);
        } else if (Short.class == clazz || Short.TYPE == clazz) {
            return (T) Short.decode(trimedText);
        } else if (Integer.class == clazz || Integer.TYPE == clazz) {
            return (T) Integer.decode(trimedText);
        } else if (Long.class == clazz || Long.TYPE == clazz) {
            return (T) Long.decode(trimedText);
        } else if (BigInteger.class == clazz) {
            return (T) decodeBigInteger(trimedText);
        } else if (Float.class == clazz || Float.TYPE == clazz) {
            return (T) Float.valueOf(trimedText);
        } else if (Double.class == clazz || Double.TYPE == clazz) {
            return (T) Double.valueOf(trimedText);
        } else if (BigDecimal.class == clazz || Number.class == clazz) {
            //todo. 当前实现，只处理十进制数，其他进制数抛 NumberFormatException
            return (T) new BigDecimal(trimedText);
        } else {
            throw new IllegalStateException("不支持的类型[" + clazz.getName() + "]");
        }
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

    /**
     * 将对象转化为数值类型。
     *
     * @param o 对象
     * @param clazz 数值类型 clazz
     * @return 转化得到的数值
     * @param <T> 数值类型
     * @throws NullPointerException  if the specified object or clazz is null
     * @throws IllegalArgumentException 如果目标类型是整数且原数值超过该整数类型的表示范围时抛出此异常
     * @throws NumberFormatException 如果对象是字符串类型且该字符串不能表示为数值
     */
    public static <T extends Number> T convertToNumber(Object o, Class<T> clazz) {
        if(o instanceof Number) {
            return convertNumberToTargetClazz((Number) o, clazz);
        } else if(o instanceof String) {
            return parseTextToTargetNumber((String) o, clazz);
        } else {
            throw new IllegalStateException("不支持的类型[" + o.getClass() + "]");
        }
    }

    /*
     *
     * @param o
     * @throws NumberFormatException
     * @return
     */
    @Deprecated
    public static Long convert2Long(Object o) {
        if(o == null) throw new NumberFormatException("不能为 null");
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
    public static int closestPower(int n) {
        if(n < 0) return 1;
        if(n >= MAXIMUM_CAPACITY) return MAXIMUM_CAPACITY;
        int c = n - 1; //n 已经是 2^m 次方的情况
        c |= c >>> 1;
        c |= c >>> 2;
        c |= c >>> 4;
        c |= c >>> 8;
        c |= c >>> 16;  //最多移 16 位的原因，与 MAXIMUM_CAPACITY 最大值相关
        return (c < 0) ? 1 : (c >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : c + 1;
    }

}
