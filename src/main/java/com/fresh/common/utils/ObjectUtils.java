package com.fresh.common.utils;


import java.util.Arrays;
import java.util.Objects;

public abstract class ObjectUtils {

    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;

    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";


    /**
     * Returns true if the two specified object is equal.
     * The specified object may be array.
     * The two specified object is equal, if any of the following:
     * <ul>
     *     <li>1. same reference(相同的引用)</li>
     *     <li>2. both are null</li>
     *     <li>3. o1.equals(o2) has a true return-value</li>
     *     <li>4. arrayEquals(o1, o2) has a true return-value</li>
     * </ul>
     * @see ObjectUtils#arrayEquals(Object, Object) 
     * @param o1 one specified object
     * @param o2 another specified object
     * @return true if the two specified object is equal
     */
    public static boolean objEquals(Object o1, Object o2) {
        /*if(Objects.equals(o1, o2)) return true;
        return arrayEquals(o1, o2);*/
        if(o1 == o2) return true;
        if(o1 == null || o2 == null) return false;
        if(o1.equals(o2)) return true;
        if(o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    /**
     * Returns true if the two specified array object is equal.
     * If any of the two specified object is not array, return false.
     * The two specified array object is equal, if any of the following:
     * <ul>
     *     <li>Arrays.equals(o1, o2) has a true return-value</li>
     * </ul>
     * @param o1 one specified object
     * @param o2 another specified object
     * @return true if the two specified array object is equal
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


    /**
     * Returns hash code value of the specified object.
     * If the specified object is null, return 0, else
     * if the specified object is not an array, return o.hashCode(), else
     * return objHashCode(o)
     *
     * @param o the specified object
     * @return  hash code value of the specified object
     */
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

    //Arrays#hashCode(boolean[])
    public static int objHashCode(boolean[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(boolean b : o) {
            hash = MULTIPLIER * hash + Boolean.hashCode(b);
        }
        return hash;
    }
    //Arrays#hashCode(byte[])
    public static int objHashCode(byte[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(byte b : o) {
            hash = MULTIPLIER * hash + b;
        }
        return hash;
    }
    //Arrays#hashCode(char[])
    public static int objHashCode(char[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(char c : o) {
            hash = MULTIPLIER * hash + c;
        }
        return hash;
    }
    //Arrays#hashCode(short[])
    public static int objHashCode(short[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(short s : o) {
            hash = MULTIPLIER * hash + Short.hashCode(s);
        }
        return hash;
    }
    //Arrays#hashCode(int[])
    public static int objHashCode(int[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(int i : o) {
            hash = MULTIPLIER * hash + Integer.hashCode(i);
        }
        return hash;
    }
    //Arrays#hashCode(long[])
    public static int objHashCode(long[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(long l : o) {
            hash = MULTIPLIER * hash + Long.hashCode(l);
        }
        return hash;
    }
    //Arrays#hashCode(float[])
    public static int objHashCode(float[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(float f : o) {
            hash = MULTIPLIER * hash + Float.hashCode(f);
        }
        return hash;
    }
    //Arrays#hashCode(double[])
    public static int objHashCode(double[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(double d : o) {
            hash = MULTIPLIER * hash + Double.hashCode(d);
        }
        return hash;
    }
    //Arrays#hashCode(Object[])
    public static int objHashCode(Object[] o) {
        if(o == null) return 0;
        int hash = INITIAL_HASH;
        for(Object l : o) {
            hash = MULTIPLIER * hash + (l == null ? 0 : l.hashCode());
        }
        return hash;
    }



    public static String objToString(Object obj) {
        if (obj == null) {
            return NULL_STRING;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Object[]) {
            return Arrays.toString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        String str = obj.toString();
        return (str != null ? str : EMPTY_STRING);
    }





}
