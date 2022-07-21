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

        //superinterface, 先根深度递归, 查找路径: GetFieldTest,Field_A,Field_A_Super,Field_B; Field_Super; Object
        Field recursive_field = GetFieldTest.class.getField("recursive");
        Field depth_field = GetFieldTest.class.getField("depth");

        //相同名称field
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





    private static class Book2 {

        public Book2(String var) {}
        public Book2(int[] vars) {}
        public <T> Book2(T var) {}
        public <T extends Number> Book2(T var) {}
        public <T> Book2(T[] array) {}
        public <T> Book2(List<T>[] listArray) {}
        public <T> Book2(Book<T> ts) {}
        public Book2(List<? extends Number> bk) {}

        public <T> T say() {
            return (T) "abc";
        }
        public <T> T say(T t) {
            return t;
        }
        public <T> T[] see() {
            //T[] t = new T[1];
            return null;
        }
        public <T> List<T>[] see(List<T>[] param) {
            //ArrayList<T>[] a = new ArrayList<T>[1];
            return param;
        }
        public <T> Book<T> eat() {
            return (Book<T>) new Book<String>();
        }
        public <T> Book<T> eat(Book<T> book) {
            return book;
        }
        public Book<? extends Number> getEat() {
            return new Book<Integer>();
        }


    }

    private static class Book<T> {
        T t;  //TypeVariable，泛型变量类型
        T[] array;//GenericArrayType，泛型数组类型;TypeVariable
        List<T>[] listArray;//GenericArrayType，泛型数组类型;ParameterizedType,TypeVariable
        Book<T> list;//ParameterizedType，泛型类型;TypeVariable
        List<? extends Number> bb; //ParameterizedType，泛型类型;WildcardType
        List<String> strList; //ParameterizedType，泛型类型;Class<String>
        List<? extends List<T>> ll; //ParameterizedType，泛型类型;WildcardType，ParamerizedType，TypeVariable

        public Book() {
        }

        public Book(T t, T[] array, List<T>[] listArray, Book<T> list, List<? extends Number> bb, List<String> strList, List<? extends List<T>> ll) {
            this.t = t;
            this.array = array;
            this.listArray = listArray;
            this.list = list;
            this.bb = bb;
            this.strList = strList;
            this.ll = ll;
        }

        public static void main(String argv[]) throws Exception {
            List<String>[] genericArray = (ArrayList<String>[]) Array.newInstance(ArrayList.class, 2);
            new Book<String>("123", new String[1], genericArray, new Book<String>(), new ArrayList<Integer>(), new ArrayList<String>(), new ArrayList<ArrayList<String>>());

            System.out.println(1);
        }

    }

}
