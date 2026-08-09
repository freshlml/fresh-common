package com.fresh.core.utils;


import com.fresh.core.component.clazz.ClazzComponentResolver;
import com.fresh.core.component.Component;
import com.fresh.core.enums.FreshForTestEnum;
import com.fresh.core.result.JsonResult;

import java.util.List;

/**
 * test for ClazzUtils
 */
public final class ClazzUtilsTest {

    private ClazzUtilsTest() {}

    public static void main(String[] argv) throws Exception {

        forName_test();

        testIsAssignableFrom();

        testClazzTree();

    }


    public static void forName_test() throws Exception {
        System.out.println(ClazzUtils.forName("com.fresh.core.utils.ClazzUtilsTest", true, null));

        System.out.println(ClazzUtils.forName("[I", true, null));
        System.out.println(ClazzUtils.forName("[[I", true, null));
        System.out.println(ClazzUtils.forName("[Lcom.fresh.core.utils.ClazzUtilsTest;", true, null));
        System.out.println(ClazzUtils.forName("[[Lcom.fresh.core.utils.ClazzUtilsTest;", true, null));

        Class<?> intClass = ClazzUtils.forName("int", true, null);
        System.out.println("int: " + intClass);
        System.out.println("int.class == intClass: " + (intClass == int.class));

        Class<?> intArrayClass = ClazzUtils.forName("int[]", true, null);
        System.out.println("int[]: " + intArrayClass);
        System.out.println("int[].class == intArrayClass: " + (intArrayClass == int[].class));

        Class<?> objArrayClass = ClazzUtils.forName("com.fresh.core.utils.ClazzUtilsTest[]", true, null);
        System.out.println("objArrayClass: " + objArrayClass);

        Class<?> clazz = ClazzUtils.forName("com.fresh.core.utils.ClazzUtilsTest[][]", true, null);
        System.out.println(clazz);

        System.out.println("---------------------forName_test---------------------");
    }


    private static void testIsAssignableFrom() {
        //primitive
        System.out.println(long.class.isAssignableFrom(int.class));     //false
        System.out.println(Integer.class.isAssignableFrom(int.class));  //false
        System.out.println(int.class.isAssignableFrom(Integer.class));  //false

        System.out.println(ClazzUtils.isAssignableFrom(long.class, int.class));  //false
        System.out.println(ClazzUtils.isAssignableFrom(int.class, Integer.class));  //true
        System.out.println(ClazzUtils.isAssignableFrom(Integer.class, int.class));  //true

        //primitive array
        System.out.println(int[].class.isAssignableFrom(int[].class));           //true
        System.out.println(long[].class.isAssignableFrom(int[].class));          //false
        System.out.println(Integer[].class.isAssignableFrom(int[].class));       //false
        System.out.println(int[].class.isAssignableFrom(Integer[].class));       //false

        System.out.println(ClazzUtils.isAssignableFrom(int[].class, int[].class));      //true
        System.out.println(ClazzUtils.isAssignableFrom(long[].class, int[].class));     //false
        System.out.println(ClazzUtils.isAssignableFrom(int[].class, Integer[].class));  //false    去掉注释 true
        System.out.println(ClazzUtils.isAssignableFrom(Integer[].class, int[].class));  //false    去掉注释 true

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
    public static class B21 extends B213 {}
    public interface B223 {}
    public interface B22 extends B223 {}
    public static class B1 extends B21 implements B22 {}
    public class A extends B1 implements C1, D1 {}

}
