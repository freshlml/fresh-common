package com.fresh.common.utils.reflect;


import com.fresh.common.utils.reflect.anno.PoAnnotation;
import com.fresh.common.utils.reflect.anno.PosAnnotation;
import com.fresh.common.utils.reflect.enums.NothingEnum;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClazzTest {

    public static void main(String argv[]) throws Exception {

        classTest();
        forConsTest();
        forNameTest();
        isInstanceTest();
        isMemberClassTest();
        getNameTest();
        getEnumConstantsTest();
        getClassesTest();
        getSupperClassTest();
        getInterfacesTest();
        getSignersTest();
        annotationTest();
        getTypeParametersTest();

    }

    //根据类型获取Class
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
    }

    //getConstructor方法测试
    private static void forConsTest() throws Exception {
        Constructor<Book2> con1 = Book2.class.getConstructor(new Class<?>[]{Object.class});
        Constructor<Book2> con2 = Book2.class.getConstructor(new Class<?>[]{String.class});
        Constructor<Book2> con3 = Book2.class.getConstructor(new Class<?>[]{int[].class});
        Constructor<Book2> con4 = Book2.class.getConstructor(new Class<?>[]{Object[].class});
        Constructor<Book2> con5 = Book2.class.getConstructor(new Class<?>[]{Book.class});
        Constructor<Book2> con6 = Book2.class.getConstructor(new Class<?>[]{List[].class});
        Constructor<Book2> con7 = Book2.class.getConstructor(new Class<?>[]{Number.class});

        Constructor<?>[] ctor = Yui.class.getDeclaredConstructors();
        Arrays.stream(ctor).forEach(tor -> System.out.println(tor.isSynthetic()));
    }

    //forName方法测试
    private static void forNameTest() throws Exception {
        Class<?> clazz = Class.forName("com.fresh.common.utils.reflect.ClazzTest");
    }

    //isInstance、isAssignableFrom方法测试
    private static void isInstanceTest() throws Exception {
        Class<ClazzTest[]> classArrayClazz = ClazzTest[].class;
        System.out.println(classArrayClazz.isInstance(new ClazzTest[10]));
        System.out.println(Object[].class.isInstance(new ClazzTest[10]));
        System.out.println(int.class.isInstance(1));

        System.out.println(Object[].class.isAssignableFrom(ClazzTest[].class));
    }

    //isMemberClass测试
    private static void isMemberClassTest() throws Exception {
        //Class<Uiop.InnerOp> innerClazz = Uiop.InnerOp.class;
        //System.out.println(innerClazz.isMemberClass());
        //Class<Uiop.InnerStaticOp> innerStaticClazz = Uiop.InnerStaticOp.class;
        //System.out.println(innerStaticClazz.isMemberClass());
    }

    //getName getSimpleName getTypeName getCanonicalName
    private static void getNameTest() {
        Class<ClazzTest[]> classArrayClazz = ClazzTest[].class;
        Class<ClazzTest> interfaceClazz = ClazzTest.class;

        System.out.println(classArrayClazz.getName());
        System.out.println(interfaceClazz.getSimpleName());
        System.out.println(classArrayClazz.getTypeName());
        System.out.println(classArrayClazz.getCanonicalName());
    }

    //getEnumConstants
    private static void getEnumConstantsTest() {
        Class<NothingEnum> enumClazz = NothingEnum.class;
        NothingEnum[] result = enumClazz.getEnumConstants();
    }


    //getClasses
    private static void getClassesTest() {
        Class<?>[] result = ClazzTest.class.getClasses();
    }

    //getSupperClass
    private static void getSupperClassTest() {
        Class<? super ClazzTest> superClasses = ClazzTest.class.getSuperclass();
    }

    //getInterfaces
    private static void getInterfacesTest() {
        Class<?>[] result = ClazzTest.class.getInterfaces();
    }

    //getSigners
    private static void getSignersTest() {
        Object[] result = ClazzTest.class.getSigners();
    }

    //annotation
    private static void annotationTest() {
        PoAnnotation r1 = AnnotationClass.class.getAnnotation(PoAnnotation.class);
        System.out.println(r1);
        PosAnnotation r2 = AnnotationClass.class.getAnnotation(PosAnnotation.class);
        System.out.println(r2);
        PoAnnotation[] r3 = AnnotationClass.class.getAnnotationsByType(PoAnnotation.class);
        System.out.println(r3);

        System.out.println(AnnotationClass.class.isAnnotationPresent(PoAnnotation.class));
        System.out.println(AnnotationClass.class.isAnnotationPresent(PosAnnotation.class));

    }

    //getTypeParameters
    private static void getTypeParametersTest() throws Exception {
        Class<Book> bookClazz = (Class<Book>) Class.forName("com.project.normal.test.reflect.ClazzTest.Book");
        System.out.println(bookClazz);
        TypeVariable<Class<Book>>[] typeVariables = bookClazz.getTypeParameters();

        Book<String> bookString = new Book<>();
        Class<? extends Book> bookStringClazz = bookString.getClass();
        TypeVariable<? extends Class<? extends Book>>[] typeVariables2 = bookStringClazz.getTypeParameters();
    }


    private static class Yui {
        public <T> Yui(T t) {}
    }

    @PosAnnotation(
           {@PoAnnotation(name="nei")}
    )
    @PoAnnotation(name="wai")
    private static class AnnotationClass {

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
