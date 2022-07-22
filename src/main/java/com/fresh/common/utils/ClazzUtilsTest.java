package com.fresh.common.utils;


import com.fresh.common.component.clazz.ClazzComponentResolver;
import com.fresh.common.component.Component;
import com.fresh.common.enums.FreshForTestEnum;
import com.fresh.common.result.JsonResult;

import java.util.List;

/**
 * test for ClazzUtils
 */
public class ClazzUtilsTest {

    public static void main(String argv[]) throws Exception {

        testForName();

        testIsAssignableFrom();

        testClazzTree();

    }


    public static void testForName() throws Exception {
        Class<?> intClass = ClazzUtils.forName("int", null);
        System.out.println("int: " + intClass);
        System.out.println("int.class == intClass: " + (intClass == int.class));
        Class<?> intArrayClass = ClazzUtils.forName("int[]", null);
        System.out.println("int[]: " + intArrayClass);
        System.out.println("int[].class == intArrayClass: " + (intArrayClass==int[].class));
        Class<?> objArray1 = ClazzUtils.forName("[Lcom.fresh.common.result.JsonResult;", null);
        System.out.println("objArray1: " + objArray1);
        System.out.println("obj[].class ==  objArray1: " + (objArray1 == JsonResult[].class));
        Class<?> objArray2 = ClazzUtils.forName("com.fresh.common.result.JsonResult[]", null);
        System.out.println("objArray2: " + objArray2);
        System.out.println("obj[].class ==  objArray2: " + (objArray1 == JsonResult[].class));
        Class<?> clazz = ClazzUtils.forName("com.fresh.common.utils.TestForInnerClazz", null);
        System.out.println(clazz);
        Class<?> innerClazz = ClazzUtils.forName("com.fresh.common.utils.TestForInnerClazz$PubStaticInnerClazz", null);
        System.out.println(innerClazz);

        System.out.println("---------------------testForName---------------------");
    }


    private static void testIsAssignableFrom() {
        //primitive
        System.out.println("int.class assignableFrom int.class: " + int.class.isAssignableFrom(int.class));
        System.out.println("int.class assignableFrom Integer.class: " + int.class.isAssignableFrom(Integer.class));
        System.out.println("Integer.class assignableFrom int.class: " + Integer.class.isAssignableFrom(int.class));

        System.out.println(ClazzUtils.isAssignableFrom(int.class, Integer.class));
        System.out.println(ClazzUtils.isAssignableFrom(Integer.class, int.class));


        //array(primitive)
        System.out.println("int[] assignableFrom int[]: " + int[].class.isAssignableFrom(int[].class));
        System.out.println("Integer[] assignableFrom int[]: " + Integer[].class.isAssignableFrom(int[].class));
        System.out.println("int[] assignableFrom Integer[]: " + int[].class.isAssignableFrom(Integer[].class));
        System.out.println("Integer[][][] assignableFrom Integer[][][]: " + Integer[][][].class.isAssignableFrom(Integer[][][].class));

        System.out.println(ClazzUtils.isAssignableFrom(int[].class, int[].class));
        System.out.println(ClazzUtils.isAssignableFrom(int[].class, Integer[].class));
        System.out.println(ClazzUtils.isAssignableFrom(Integer[].class, int[].class));
        System.out.println(ClazzUtils.isAssignableFrom(Integer[][][].class, int[][][].class));
        System.out.println(ClazzUtils.isAssignableFrom(Integer[][].class, int[][][].class));

        System.out.println("---------------------testIsAssignableFrom---------------------");
    }



    private static void testClazzTree() {

        Component<Class<?>> result = ClazzUtils.clazzTree(A.class);
        Component<Class<?>> result2 = ClazzUtils.clazzTree(int.class);
        Component<Class<?>> result3 = ClazzUtils.clazzTree(int[].class);
        Component<Class<?>> result4 = ClazzUtils.clazzTree(A[].class);

        Class<?> node = result.getEntity();
        List<Component<Class<?>>> childs = result.getAllChild();

        List<Class<?>> supper1 = ClazzUtils.getAllSuperClass(A.class);
        List<Class<?>> supper2 = ClazzUtils.getAllSuperClass(FreshForTestEnum.class);
        List<Class<?>> supper3 = ClazzUtils.getAllSuperClass(int.class);
        List<Class<?>> supper4 = ClazzUtils.getAllSuperClass(ClazzComponentResolver.class);
        List<Class<?>> supper5 = ClazzUtils.getAllSuperClass(int[].class);
        List<Class<?>> supper6 = ClazzUtils.getAllSuperClass(A[].class);

        List<Class<?>> inter1 = ClazzUtils.getAllInterfaces(A.class);
        List<Class<?>> inter2 = ClazzUtils.getAllInterfaces(FreshForTestEnum.class);
        List<Class<?>> inter3 = ClazzUtils.getAllInterfaces(int.class);
        List<Class<?>> inter4 = ClazzUtils.getAllInterfaces(ClazzComponentResolver.class);
        List<Class<?>> inter5 = ClazzUtils.getAllInterfaces(int[].class);
        List<Class<?>> inter6 = ClazzUtils.getAllInterfaces(A[].class);


        System.out.println("---------------------testClazzTree---------------------");

    }

    public interface D1 {}
    public interface C2 {}
    public interface C1 extends C2 {}
    public static class B213 {}
    public static class B21 extends B213{}
    public interface B223{}
    public interface B22 extends B223 {}
    public static class B1 extends B21 implements B22{}
    public class A extends B1 implements C1, D1 {}

}
