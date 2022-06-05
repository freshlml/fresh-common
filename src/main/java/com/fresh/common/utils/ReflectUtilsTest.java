package com.fresh.common.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectUtilsTest {

    public static void main(String argv[]) {

        //testGetConstructor();

        //testMethod();

        //findDeclaredMethod();

        findAllDeclaredMethodsBFS();

        findFieldTest();
    }


    private static void findFieldTest() {
        Field f1 = ReflectUtils.getField(Attr.class, "ba");
        Field f2 = ReflectUtils.getField(Attr.class, "t");
        Field[] fs = Attr.class.getFields();

        Field g1 = ReflectUtils.findDeclaredField(Attr.class, "ba", null);
        Field g2 = ReflectUtils.findDeclaredField(Attr.class, "baaa", Battr.class);

        Field[] gfs = ReflectUtils.findAllDeclaredFields(Attr.class);

        System.out.println(1);
    }

    private interface AttrE {
        String t = "tE";
    }
    private interface Attr1 {
        String attr1 = "123";
        Integer attr2  = 123;
        String t = "t1";
    }
    private interface Attr2 extends Attr1 {
        String attr1 = "相同名称，相同类型";
        String attr2 = "相同名称，不同类型";

    }
    private static class Bn {
        public String t;
    }
    private static class Battr<T> extends Bn {
        private T b1;
        public T[] ba;
        public List<T> baa;
        private Battr<T> baaa;
        private String bb;
        private String attr2 = "接口同名域";
        public String baq;
    }
    private static class Attr extends Battr<Integer> implements Attr2,AttrE {
        public String owen;
        public String baq;


    }

    private static void findAllDeclaredMethodsBFS() {
        Method[] m1s = ReflectUtils.findAllDeclaredMethodsBFS(Inter1.class);
        Method[] m2s = ReflectUtils.findAllDeclaredMethodsBFS(Inter2.class);
        Method[] m3s = ReflectUtils.findAllDeclaredMethodsBFS(Op.class);
        Method[] m4s = ReflectUtils.findAllDeclaredMethodsBFS(int.class);
        Method[] m5s = ReflectUtils.findAllDeclaredMethodsBFS(int[].class);

        Method[] u1s = ReflectUtils.findAllOverDeclaredMethodsBFS(Inter1.class);
        Method[] u2s = ReflectUtils.findAllOverDeclaredMethodsBFS(Inter2.class);
        Method[] u3s = ReflectUtils.findAllOverDeclaredMethodsBFS(Op.class);
        Method[] u4s = ReflectUtils.findAllOverDeclaredMethodsBFS(int.class);
        Method[] u5s = ReflectUtils.findAllOverDeclaredMethodsBFS(int[].class);

        System.out.println(1);
    }

    private static void findDeclaredMethod() {

        Method[] ii1 = ReflectUtils.findAllDeclaredMethodOnInterfaces(Inter1.class);
        Method[] ii2 = ReflectUtils.findAllDeclaredMethodOnInterfaces(Inter2.class);

        List<Method> i1 = ReflectUtils.findInstanceMethodsOnInterfaces(Inter1.class);
        List<Method> i2 = ReflectUtils.findInstanceMethodsOnInterfaces(Inter2.class);
        List<Method> i3 = ReflectUtils.findInstanceMethodsOnInterfaces(Op.class);

        Method m1 = ReflectUtils.findDeclaredMethod(Inter1.class, "inter1Inter1", new Class<?>[]{String.class});
        Method m2 = ReflectUtils.findDeclaredMethod(Inter1.class, "inter1Default1");
        Method m3 = ReflectUtils.findDeclaredMethod(Inter1.class, "inter1Static", new Class<?>[]{String.class});

        Method m4 = ReflectUtils.findDeclaredMethod(Inter2.class, "inter1Inter1", new Class<?>[]{String.class});
        Method m5 = ReflectUtils.findDeclaredMethod(Inter2.class, "inter1Inter2");
        Method m6 = ReflectUtils.findDeclaredMethod(Inter2.class, "inter1Default1");
        Method m7 = ReflectUtils.findDeclaredMethod(Inter2.class, "inter1Default2");
        Method m8 = ReflectUtils.findDeclaredMethod(Inter2.class, "inter1Static", new Class<?>[]{String.class});
        Method m9 = ReflectUtils.findDeclaredMethod(Inter2.class, "inter2");

        Method m10 = ReflectUtils.findDeclaredMethod(Op.class, "bg2", new Class<?>[]{String.class});
        Method m11 = ReflectUtils.findDeclaredMethod(Op.class, "inter2");
        Method m12 = ReflectUtils.findDeclaredMethod(Op.class, "inter1Static", new Class<?>[]{String.class});
        Method m13 = ReflectUtils.findDeclaredMethod(Op.class, "inter1Inter2");
        Class<?>[] types = m13.getParameterTypes();

        System.out.println(1);

    }
    private interface Inter1 {
        void inter1Inter1(String str);//abstract方法，子接口可以重写
        void inter1Inter2();//abstract方法，子接口可以重写
        default void inter1Default1() { //default方法，子接口可以重写
            System.out.println("Inter1_Default1");
        }
        default void inter1Default2() { //default方法，子接口可以重写
            System.out.println("Inter1_Default2");
        }
        static void inter1Static(String str) {
            System.out.println("Inter1_static");
        }
        default void inter1() {}
    }
    private interface Inter2<T> extends Inter1 {
        @Override
        void inter1Inter1(String str); //重写父接口abstract方法，仍然声明为abstract方法，保留此
        @Override
        default void inter1Inter2() {} //重写父接口abstract方法，声明为default，保留此
        @Override
        void inter1Default1(); //重写父接口default方法，声明为abstract，保留此
        default void inter1Default2() {} //重写父接口default方法，声明为default，保留此
        static void inter1Static(String str) {
            System.out.println("Inter2_static");
        }
        default void inter2() {}

        T bg(T t);
        <T> T bg2(String str);
    }
    private static class Op implements Inter2<String> {
        @Override
        public void inter1Inter1(String str) {}
        @Override
        public void inter1Default1() {}
        @Override
        public String bg(String s) {
            return s;
        }//生成桥接方法(方法签名不同) public Object bg(Object) {...}
        @Override
        public String bg2(String str) {
            return "123";
        }//生成桥接方法(方法签名相同) public Object bg2(String) {...}
    }


    private static void testMethod() {

        Method m1 = ReflectUtils.getMethod(TestMethod.class, "getG", new Class<?>[]{String.class});
        Method m2 = ReflectUtils.getMethod(TestMethod.class, "genericT");
        Method m3 = ReflectUtils.getMethod(TestMethod.class, "genericTTT", new Class<?>[]{Number.class});
        Method m4 = ReflectUtils.getMethod(TestMethod.class, "get", new Class<?>[]{Object.class});
        Method m5 = ReflectUtils.getMethod(TestMethod.class, "tStatic");
        Method m6 = ReflectUtils.getMethod(TestMethod.class, "baseOver");
        Method m7 = ReflectUtils.getMethod(TestMethod.class, "base");
        Method m8 = ReflectUtils.getMethod(TestMethod.class, "interStatic");

        Method[] l1 = TestMethod.class.getDeclaredMethods();
        Method[] l2 = TestMethod.class.getSuperclass().getDeclaredMethods();

        Method k1 = ReflectUtils.findDeclaredMethod(TestMethod.class, "baseInter");

        Method[] rs = ReflectionUtils.getAllDeclaredMethods(TestMethod.class);
        
        System.out.println(1);
    }
    private static class Base<G> {
        public void base() {System.out.println("base");}
        public void baseOver() { System.out.println("baseOver"); }
        public static void baseStatic() {System.out.println("baseStatic");}

        public G getG(G g) {System.out.println("getG"); return g;}

        public <T extends Number> T genericT() {System.out.println("genericT"); return (T) new Integer(1);}
        public <T> T genericT(Integer i) {System.out.println("genericT"); return (T) "123";}
        public <T> T genericTT(T t) {System.out.println("genericTT"); return t;}
        public <T> void genericTT(T[] t) {System.out.println("genericTT[]");}
        public <T> void genericTT(List<T>[] t) {System.out.println("genericTT[]");}
        public <T extends Number> void genericTTT(T t) {System.out.println("genericTTT");}
        public <T, E extends T> T genericTTT(E t) {System.out.println(); return t;}
    }
    private interface BaseInter {
        void baseInter();
        static void interStatic() {System.out.println("interStatic");}
        default void baseInter2() {}
    }
    private static class TestMethod extends Base<String> implements BaseInter {
        @Override
        public void baseInter() { System.out.println("override baseInter"); }
        @Override
        public void baseInter2() {}
        @Override
        public void baseOver() { System.out.println("override baseOver"); }
        public void t() {}
        public static void tStatic() {}

        //泛型方法
        public <E> E get(E e) {System.out.println(e);return e;}

        //重写父类泛型方法
        @Override
        public String getG(String s) {
            System.out.println("override getG");
            return s;
        }


        //重写父类泛型方法
        @Override
        public Number genericT() {
            System.out.println("override genericT");
            return new Integer(1);
        }
        @Override
        public String genericT(Integer i) {
            System.out.println("override genericT");
            return "123";
        }

        //重写泛型擦除后的方法
        @Override
        public void genericTTT(Number t) {System.out.println("override genericTTT");}

    }

    private static void testGetConstructor() {

        Constructor<?> c1 = ReflectUtils.getConstructor(Loop.class);
        Constructor<?> c2 = ReflectUtils.getConstructor(Loop.class, null);
        Constructor<?> c3 = ReflectUtils.getConstructor(Loop.class, new Class<?>[]{String.class});
        Constructor<?> c4 = ReflectUtils.getConstructor(Loop.class, new Class<?>[]{int[].class});
        Constructor<?> c5 = ReflectUtils.getConstructor(Loop.class, new Class<?>[]{Object.class});
        Constructor<?> c6 = ReflectUtils.getConstructor(Loop.class, new Class<?>[]{Number.class});
        Constructor<?> c7 = ReflectUtils.getConstructor(Loop.class, new Class<?>[]{Object[].class});
        Constructor<?> c8 = ReflectUtils.getConstructor(Loop.class, new Class<?>[]{List[].class});
        Constructor<?> c9 = ReflectUtils.getConstructor(Loop.class, new Class<?>[]{List.class});

        Constructor<?>[] cs = Loop.class.getConstructors();
        
        System.out.println(1);

    }
    private static class LoopBase<E> {
        public LoopBase(E e) {}
        public LoopBase() {}
    }
    private static class Loop extends LoopBase<String> {

        public Loop(String var) {super(var);}
        public Loop(int[] vars) {}
        public <T> Loop(T var) {}
        public <T extends Number> Loop(T var) {}
        public <T> Loop(T[] array) {}
        public <T> Loop(List<T>[] listArray) {}
        public Loop(List<? extends Number> bk) {}

    }


}
