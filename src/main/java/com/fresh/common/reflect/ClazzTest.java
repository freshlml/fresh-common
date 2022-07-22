package com.fresh.common.reflect;


import com.fresh.common.reflect.enums.NothingEnum;

import javax.annotation.PostConstruct;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class ClazzTest {

    public static void main(String argv[]) throws Exception {

        classTest();
        forNameTest();
        newInstanceTest();
        isInstanceTest();
        superTest();
        forTest();

        testGetField();
        testGetMethod();

        testInnerClazz();

    }

    //获取Class
    private static void classTest() {
        //declared class, enum, interface, annotation, array, primitive
        Class<ClazzTest> declaredClassClazz = ClazzTest.class;
        Class<NothingEnum> enumClazz = NothingEnum.class;
        Class<PostConstruct> annotationClazz = PostConstruct.class;
        Class<int[]> intArrayClazz = int[].class;
        Class<ClazzTest[]> classArrayClazz = ClazzTest[].class;
        Class<Boolean> primitiveSeqClazz = Boolean.class;
        Class<Boolean> primitiveClazz = boolean.class;
        Class<Void> voidClass = void.class;
        //error List<String>.class;
        Class<List> listClass = List.class;  //带泛型的类型共用一个Class

        System.out.println("---------classTest-----------\n");
    }

    private static class n_a {}
    //Class#forName方法测试
    private static void forNameTest() throws Exception {
        //使用Class#forName时，程序应该捕获并处理ClassNotFoundException
        try {
            Class<?> notFount = Class.forName("nothing");
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
        }

        Class<?> clazz = Class.forName("com.fresh.common.reflect.ClazzTest");
        //成员内部类，使用$分隔符
        Class<?> n_clazz = Class.forName("com.fresh.common.reflect.ClazzTest$n_a");

        
        System.out.println("---------forNameTest------------\n");
    }

    private static class B {
        private B() {}
    }
    private static void newInstanceTest() {

        try {
            B b = B.class.newInstance();
            System.out.println(b);
        } catch (InstantiationException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("-----------newInstanceTest----------\n");
    }


    //Class#isInstance、Class#isAssignableFrom方法测试
    private static void isInstanceTest() {

        boolean enum_isIns = Enum.class.isInstance(NothingEnum.ONE);

        //数组的isInstance
        boolean array_isIns1 = ClazzTest[][][].class.isInstance(new ClazzTest[10][10][100]);
        boolean array_isIns2 = Object[][].class.isInstance(new ClazzTest[10][10]);

        boolean array_isIns3 = int[].class.isInstance(new int[10]);
        boolean array_isIns4 = int[].class.isInstance(new Integer[10]); //false ,缺陷
        boolean array_isIns5 = Integer[].class.isInstance(new Integer[10]);
        boolean array_isIns6 = Integer[].class.isInstance(new int[10]); //false ,缺陷

        //primitive type的isInstance
        boolean int_isIns = int.class.isInstance(1);  //false ,缺陷


        List<String>[] list = (List<String>[]) Array.newInstance(ArrayList.class, 2);
        System.out.println(List[].class.isInstance(list));

        System.out.println("----------isInstance-----------\n");

        //isAssignableFrom的缺陷
        boolean int_isAssignable = int.class.isAssignableFrom(Integer.class);  //false
        boolean int_isAssignable2 = Integer.class.isAssignableFrom(int.class); //false

        //数组的isAssignable
        boolean array_isAssignable = Object[][].class.isAssignableFrom(ClazzTest[][].class);
        boolean array_isAssignable2 = int[].class.isAssignableFrom(Integer[].class); //false
        boolean array_isAssignable3 = Integer[].class.isAssignableFrom(int[].class); //false

        System.out.println("------------isAssignableFrom------------\n");
    }



    private static class C {}
    private static class D<T> extends C {}
    private static class E extends D<String> {}

    interface F {}
    interface G extends F {}
    interface H {}
    private static class II implements H, G {}
    //测试superclass,superinterface相关方法
    private static void superTest() {

        ParameterizedType super_generic_E = (ParameterizedType) E.class.getGenericSuperclass();
        Class<D> class_D = (Class<D>) super_generic_E.getRawType();
        Class<? super D> super_class_D = class_D.getSuperclass();
        Class<C> class_C = (Class<C>) super_class_D;


        Class<?>[] ii_interfaces = II.class.getInterfaces();

        System.out.println("--------superTest-------------\n");
    }

    private static class n_b {}
    //工具方法测试
    private static void forTest() {
        class local_n_c {}

        System.out.println(int.class.getName());
        System.out.println(long[].class.getName());
        System.out.println(ClazzTest[].class.getName());
        System.out.println(ClazzTest.class.getName());
        System.out.println(n_b.class.getName());
        System.out.println(local_n_c.class.getName());
        System.out.println("----------getName----------\n");

        System.out.println(int.class.getSimpleName());
        System.out.println(long[].class.getSimpleName());
        System.out.println(ClazzTest[].class.getSimpleName());
        System.out.println(ClazzTest.class.getSimpleName());
        System.out.println(n_b.class.getSimpleName());
        System.out.println(local_n_c.class.getSimpleName());
        System.out.println("----------getSimpleName------\n");

        System.out.println(int.class.getTypeName());
        System.out.println(long[].class.getTypeName());
        System.out.println(ClazzTest[].class.getTypeName());
        System.out.println(ClazzTest.class.getTypeName());
        System.out.println(n_b.class.getTypeName());
        System.out.println(local_n_c.class.getTypeName());
        System.out.println("----------getTypeName------\n");
        //note: 可以用getTypeName和ClazzUtils#forName配合

        System.out.println(int.class.getCanonicalName());
        System.out.println(long[].class.getCanonicalName());
        System.out.println(ClazzTest[].class.getCanonicalName());
        System.out.println(ClazzTest.class.getCanonicalName());
        System.out.println(n_b.class.getCanonicalName());
        System.out.println(local_n_c.class.getCanonicalName());
        System.out.println("---------getCanonicalName-------\n");

    }


    interface Field_B {
        String depth = "b";

        String same_name_bl = "b";
    }
    interface Filed_A_Super {
        public static String recursive = "a_super";

        String depth = "a_super";

        String same_name_bl = "a_super";
    }
    interface Field_A extends Filed_A_Super {
        String recursive = "a";

        String same_name_bl = "a";
    }
    private static class Field_Super {
        public String same_name_bl;
    }
    private static class GetFieldTest extends Field_Super implements Field_A, Field_B {

        public static String static_bl = "static_bl";
        public String non_static_bl;

        public String same_name_bl;


        private String private_bl;
        private static String static_private_bl;
    }
    private static void testGetField() throws NoSuchFieldException {

        //static field
        Field static_bl = GetFieldTest.class.getField("static_bl");
        Field non_static_bl = GetFieldTest.class.getField("non_static_bl");

        //superinterface, 先根深度递归, 查找路径: GetFieldTest,Field_A,Field_A_Super,Field_B
        Field recursive_field = GetFieldTest.class.getField("recursive");
        Field depth_field = GetFieldTest.class.getField("depth");

        //field的查找路径: GetFieldTest,Field_A,Field_A_Super,Field_B; Field_Super; Object
        Field same_name_field = GetFieldTest.class.getField("same_name_bl");


        //所有field
        Field[] all_fields = GetFieldTest.class.getFields();


        //declared语义
        try {
            Field null_recursive = GetFieldTest.class.getDeclaredField("recursive");
        } catch (NoSuchFieldException e) {
            //can not find in super
        }
        Field private_bl_field = GetFieldTest.class.getDeclaredField("private_bl");
        Field static_private_bl_field = GetFieldTest.class.getDeclaredField("static_private_bl");

        Field[] all_declared_field = GetFieldTest.class.getDeclaredFields();


        System.out.println("-----------testGetField-----------\n");

    }

    interface Method_B {

    }
    interface Method_A_Super {
        public default void method_search() {}
    }
    interface Method_A extends Method_A_Super {

    }
    interface I {
        public static void i_static_method() {}

        public default void method_search() {}
    }
    private static class Method_Super implements I {
        public static void super_static_method() {}

        public void method_search() {}
    }
    private static class TestGetMethod extends Method_Super implements Method_A, Method_B {
        public static void static_method() {}
        public void non_static_method() {}

        public void method_search() {}

        private void private_method() {}
        private static void static_private_method() {}
    }
    private static void testGetMethod() throws NoSuchMethodException {

        //static方法
        Method static_method = TestGetMethod.class.getMethod("static_method");
        Method non_static_method = TestGetMethod.class.getMethod("non_static_method");
        Method super_static_method = TestGetMethod.class.getMethod("super_static_method");
        try {
            Method interface_static_method = TestGetMethod.class.getMethod("i_static_method");
        } catch (NoSuchMethodException e) {
            //can not find 接口中的static方法
        }

        //查找路径: TestGetMethod,Method_Super,Object,I,Method_A,Method_A_Super,Method_B
        Method method_search = TestGetMethod.class.getMethod("method_search");
        
        //支持在Object中查找toString，hashCode，wait，notify，getClass
        Method to_String = TestGetMethod.class.getMethod("toString");
        Method hash_code = TestGetMethod.class.getMethod("hashCode");
        Method wait = TestGetMethod.class.getMethod("wait");
        Method notify = TestGetMethod.class.getMethod("notify");
        Method getClass = TestGetMethod.class.getMethod("getClass");
        //can not find equals, clone, finalize
        //Method equals = TestGetMethod.class.getMethod("equals");
        //Method clone = TestGetMethod.class.getMethod("clone");
        //Method finalize = TestGetMethod.class.getMethod("finalize");


        //getMethods
        Method[] methods = TestGetMethod.class.getMethods();


        //declared语义
        Method private_method = TestGetMethod.class.getDeclaredMethod("private_method");
        Method static_private_method = TestGetMethod.class.getDeclaredMethod("static_private_method");
        try {
            Method i_static_method = TestGetMethod.class.getDeclaredMethod("i_static_method");
        } catch (NoSuchMethodException e) {
            //can not find 接口中的static方法
        }

        Method[] declared_methods = TestGetMethod.class.getDeclaredMethods();


        System.out.println("-----------testGetMethod-------------\n");
    }


    private static abstract class AnonymousTest {public abstract void m();}
    private static class TestForInnerClazz {

        public AnonymousTest anonymousTest = new AnonymousTest() {
            @Override
            public void m() {
                System.out.println("--------var start");
                System.out.println(this.getClass().getEnclosingMethod());
                System.out.println(this.getClass().getEnclosingClass());
                System.out.println("--------var end");
            }
        };


        {
            class local_inner_1 {}
            AnonymousTest anonymousTest = new AnonymousTest() {
                @Override
                public void m() {}
            };

            System.out.println("--------static start");
            System.out.println(local_inner_1.class.getEnclosingMethod());
            System.out.println(local_inner_1.class.getEnclosingClass());

            System.out.println(anonymousTest.getClass().getEnclosingMethod());
            System.out.println(anonymousTest.getClass().getEnclosingClass());
            System.out.println("--------static end");
        }
        public void method() {
            class local_inner_2 {}
            AnonymousTest anonymousTest = new AnonymousTest() {
                @Override
                public void m() {}
            };

            System.out.println("--------method start");
            System.out.println(local_inner_2.class.getEnclosingMethod());
            System.out.println(local_inner_2.class.getEnclosingClass());

            System.out.println(anonymousTest.getClass().getEnclosingMethod());
            System.out.println(anonymousTest.getClass().getEnclosingClass());
            System.out.println("--------method end");
        }


        public class NonStaticMemberInner {}

        public static class StaticMemberInner {}
    }
    private static void testInnerClazz() {

        TestForInnerClazz testForInnerClazz = new TestForInnerClazz();
        testForInnerClazz.anonymousTest.m();
        testForInnerClazz.method();

        Class<TestForInnerClazz.NonStaticMemberInner> nonStaticMemberInnerClazz = TestForInnerClazz.NonStaticMemberInner.class;
        System.out.println(nonStaticMemberInnerClazz.getDeclaringClass());
        System.out.println(nonStaticMemberInnerClazz.getEnclosingClass());

        Class<TestForInnerClazz.StaticMemberInner> staticMemberInnerClazz = TestForInnerClazz.StaticMemberInner.class;
        System.out.println(staticMemberInnerClazz.getDeclaringClass());
        System.out.println(staticMemberInnerClazz.getEnclosingClass());


        System.out.println("----------testInnerClazz------------\n");
    }

}
