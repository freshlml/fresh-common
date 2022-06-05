package com.fresh.common.utils;

import java.util.Arrays;

public abstract class ObjectUtils {

    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;
    /**
     * 比较 declared class(如primitive包装类型, String, 自定义class), 数组(primitive数组, declared class数组) 的相等性
     * @param o1
     * @param o2
     * @return
     */
    public static boolean objEquals(Object o1, Object o2) {
        if(o1 == o2) return true;
        if(o1 == null || o2 == null) return false;
        if(o1.equals(o2)) return true;
        if(o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    /**
     * 判断 primitive数组, declared class数组 的相等性
     * @param o1
     * @param o2
     * @return
     */
    public static boolean arrayEquals(Object o1, Object o2) {
        if(boolean[].class == o1.getClass() && boolean[].class == o2.getClass()) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if(byte[].class == o1.getClass() && byte[].class == o2.getClass()) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if(char[].class == o1.getClass() && char[].class == o2.getClass()) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if(float[].class == o1.getClass() && float[].class == o2.getClass()) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if(double[].class == o1.getClass() && double[].class == o2.getClass()) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if(short[].class == o1.getClass() && short[].class == o2.getClass()) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        if(int[].class == o1.getClass() && int[].class == o2.getClass()) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if(long[].class == o1.getClass() && long[].class == o2.getClass()) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if(Object[].class.isInstance(o1) && Object[].class.isInstance(o2)) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }

        return false;
    }

    public static int objHashCode(boolean[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(boolean b : o) {
            hash = MULTIPLIER * hash + Boolean.hashCode(b);
        }
        return hash;
    }
    public static int objHashCode(byte[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(byte b : o) {
            hash = MULTIPLIER * hash + b;
        }
        return hash;
    }
    public static int objHashCode(char[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(char c : o) {
            hash = MULTIPLIER * hash + c;
        }
        return hash;
    }
    public static int objHashCode(float[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(float f : o) {
            hash = MULTIPLIER * hash + Float.hashCode(f);
        }
        return hash;
    }
    public static int objHashCode(double[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(double d : o) {
            hash = MULTIPLIER * hash + Double.hashCode(d);
        }
        return hash;
    }
    public static int objHashCode(short[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(short s : o) {
            hash = MULTIPLIER * hash + Short.hashCode(s);
        }
        return hash;
    }
    public static int objHashCode(int[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(int i : o) {
            hash = MULTIPLIER * hash + Integer.hashCode(i);
        }
        return hash;
    }
    public static int objHashCode(long[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(long l : o) {
            hash = MULTIPLIER * hash + Long.hashCode(l);
        }
        return hash;
    }
    public static int objHashCode(Object[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(Object l : o) {
            hash = MULTIPLIER * hash + l.hashCode();
        }
        return hash;
    }
    //计算Object的hashCode
    public static int objHashCode(Object o) {
        if (o == null) return 0;
        if(o.getClass().isArray()) {
            if(boolean[].class == o) {
                return objHashCode((boolean[]) o);
            }
            if(byte[].class == o) {
                return objHashCode((byte[]) o);
            }
            if(char[].class == o) {
                return objHashCode((char[]) o);
            }
            if(float[].class == o) {
                return objHashCode((float[]) o);
            }
            if(double[].class == o) {
                return objHashCode((double[]) o);
            }
            if(short[].class == o) {
                return objHashCode((short[]) o);
            }
            if(int[].class == o) {
                return objHashCode((int[]) o);
            }
            if(long[].class == o) {
                return objHashCode((long[]) o);
            }
            if(Object[].class.isInstance(o) ) {
                return objHashCode((Object[]) o);
            }
        }
        return o.hashCode();
    }


}
