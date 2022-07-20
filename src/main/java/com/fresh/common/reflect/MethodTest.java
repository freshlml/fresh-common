package com.fresh.common.reflect;

public class MethodTest {

    public static void main(String argv[]) {


        Class<MethodTest> mc = MethodTest.class;
        System.out.println(mc);

        Class<? extends Class> one = mc.getClass();
        System.out.println(one);

        Class<? extends Class> two = one.getClass();
        System.out.println(two);

        System.out.println(Class.class);
    }


}
