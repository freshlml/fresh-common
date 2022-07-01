package com.fresh.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public abstract class NumberUnitUtils {

    public static final Map<Integer, String> CONV = new HashMap<>();
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
    //Number Type Cache
    private static final Set<Class<? extends Number>> NUMBER_TYPE_CACHE;

    static {
        CONV.put(1, "十");
        CONV.put(2, "百");
        CONV.put(3, "K");
        CONV.put(4, "W");

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

    /**
     * 数字值转化
     * @param fromNum
     * @param topNum
     * @param numVal
     * @return
     */
    public static String convertUnitNumber(Integer fromNum, Integer topNum, Long numVal) {
        if(numVal == null || numVal <= 0) return "0";
        Integer p = topNum<0?4:topNum;
        String retVal = numVal + "";
        for(; p>=fromNum; p--) {
            Integer bian = pow10(p);
            if(bian <= numVal) {
                Long zs = numVal / bian;
                retVal = zs + "";
                if(p >= 1) {
                    Long remainder = remainder(numVal % bian, p - 1);
                    if(remainder != -1) {
                        retVal += "." + remainder;
                    }
                    retVal += CONV.get(p);
                }
                break;
            }
        }
        return retVal;
    }
    private static Long remainder(Long numRemain, Integer p) {
        if(numRemain >= pow10(p)) {
            return numRemain / pow10(p);
        } else {
            return -1L;
        }
    }
    private static Integer pow10(int num) {
        return Double.valueOf(Math.pow(10, num)).intValue();
    }


    /**
     * 将Number对象转化为指定的Number子类型
     * 通常情况"小类型"转换成"大类型",no p
     * "大类型"转换成"小类型",如果"大类型"未超过"小类型"的范围,no p
     *eg: NumberUnitUtils.convertNumberToTargetClazz(1, Integer.class, true);  //Integer to Integer
     *    NumberUnitUtils.convertNumberToTargetClazz(1123L, Integer.class, true);  //Long to Integer
     *    NumberUnitUtils.convertNumberToTargetClazz(123, Long.class, true);  //Integer to Long
     *    NumberUnitUtils.convertNumberToTargetClazz(new BigInteger("1234"), Long.class, true);  //BigInteger to Long
     *    NumberUnitUtils.convertNumberToTargetClazz(new BigDecimal("1234.345"), Long.class, true);  //BigDecimal to Long
     * @param number Number对象
     * @param clazz Number子类型的Class
     * @param checkBorder true:对clazz进行边界检查,如果超过边界,抛异常;false:不边界检查,如果number的数值超过clazz类型的边界,将发生截断,导致值不可预料
     * @return
     */
    public static <T extends Number> T convertNumberToTargetClazz(Number number, Class<T> clazz, boolean checkBorder) {
        AssertUtils.ifTrue(number==null, () -> "参数number[Number]不能为空", null);
        AssertUtils.ifTrue(clazz==null, () -> "参数clazz[Class]不能为空", null);

        if(clazz.isInstance(number)) {
            return (T) number;
        } else if(Byte.class == clazz || Byte.TYPE == clazz) {
            if(checkBorder) {
                long l = resolveLongValue(number, clazz);
                if(l < Byte.MIN_VALUE || l > Byte.MAX_VALUE) {
                    AssertUtils.ifTrue(true, () -> "参数number["+number.getClass().getName()+"]的值{"+l+"}大于["+clazz.getName()+"]允许的最大值", null);
                }
            }
            return (T) Byte.valueOf(number.byteValue());
        } else if(Short.class == clazz || Short.TYPE == clazz) {
            if(checkBorder) {
                long l = resolveLongValue(number, clazz);
                if (l < Short.MIN_VALUE || l > Short.MAX_VALUE) {
                    AssertUtils.ifTrue(true, () -> "参数number["+number.getClass().getName()+"]的值{"+l+"}大于["+clazz.getName()+"]允许的最大值", null);
                }
            }
            return (T) Short.valueOf(number.shortValue());
        } else if(Integer.class == clazz || Integer.TYPE == clazz) {
            if(checkBorder) {
                long l = resolveLongValue(number, clazz);
                if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                    AssertUtils.ifTrue(true, () -> "参数number["+number.getClass().getName()+"]的值{"+l+"}大于["+clazz.getName()+"]允许的最大值", null);
                }
            }
            return (T) Integer.valueOf(number.intValue());
        } else if(Long.class == clazz || Long.TYPE == clazz) {
            long l = resolveLongValue(number, clazz);
            return (T) Long.valueOf(l);
        } else if(BigInteger.class == clazz) {
            if(number instanceof  BigDecimal) {
                return (T) ((BigDecimal) number).toBigInteger();
            } else {
                return (T) BigInteger.valueOf(number.longValue());
            }
        } else if(Float.class == clazz || Float.TYPE == clazz) {
            return (T) Float.valueOf(number.floatValue());
        } else if(Double.class == clazz || Double.TYPE == clazz) {
            return (T) Double.valueOf(number.doubleValue());
        } else if(BigDecimal.class == clazz) {
            return (T) new BigDecimal(number.toString());
        } else {
            AssertUtils.ifTrue(true, () -> "参数number["+number.getClass().getName()+"]不能转化为"+clazz.getName(), null);
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

    public static void main(String argv[]) {
        int result1 = NumberUnitUtils.convertNumberToTargetClazz(123, int.class, true);
        int result2 = NumberUnitUtils.parseTextToTargetNumber("0x123", Integer.class);


        System.out.println(1);
    }

    /**
     * 将字符串形式的数值转化为指定类型
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

        if (value.startsWith("-")) {
            index++;
            negative = true;
        }

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

    public static <T extends Number> T convert(Object o, Class<T> clazz) {
        AssertUtils.ifTrue(o==null, () -> "参数o不能为空", null);
        if(o instanceof Number) {
            return convertNumberToTargetClazz((Number)o, clazz, true);
        }
        if(o instanceof String) {
            return parseTextToTargetNumber((String)o, clazz);
        }
        return null;
    }

    public static <T extends Number> T convert(Object o, Class<T> clazz, T defaultValue) {
        AssertUtils.ifTrue(defaultValue==null, () -> "参数defaultValue不能为空", null);
        if(o == null) {
            return defaultValue;
        }
        return convert(o, clazz);
    }

    /**
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
    /**
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



    /**
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
