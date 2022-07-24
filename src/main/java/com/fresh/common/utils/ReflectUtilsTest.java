package com.fresh.common.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtilsTest {

    public static void main(String argv[]) throws Exception {


        testFindFieldSemantics();

        testFindMethodSemantics();

    }


    interface A {
        String a = "A";

        String name = "A";
    }
    interface B {
        String b = "B";

        String name = "B";
    }
    interface C {
        String c = "C";

        String name = "C";
    }
    interface D {
        String d = "D";

        String name = "D";
    }
    interface Inter1 extends A, B {
        String inter1 = "Inter1";

        String name = "Inter1";
    }
    interface Inter2 extends C, D {
        String inter2 = "Inter2";

        String name = "Inter2";
    }

    private static class Sup1 {
        private String sup1 = "Sup1";

        private String name;
    }
    private static class Cls extends Sup1 implements Inter1, Inter2 {
        private String cls = "Cls";

        //private String name;
    }
    //搜索路径: Cls, Inter1, A, B, Inter2, C, D, Super1
    private static void testFindFieldSemantics() {

        //match first field
        ReflectUtils.MatchFirstFieldProcessor matchFirstProcessor = new ReflectUtils.MatchFirstFieldProcessor(field -> field.getName().equals("name"));
        Field result = ReflectUtils.findDeclaredFieldSemantics(Cls.class, matchFirstProcessor, 0);
        System.out.println(result);
        System.out.println("------------1-------------\n");

        //collect all
        ReflectUtils.CollectsFieldProcessor collectAll = new ReflectUtils.CollectsFieldProcessor();
        result = ReflectUtils.findDeclaredFieldSemantics(Cls.class, collectAll, 0);
        System.out.println(result);
        System.out.println(collectAll.results());
        System.out.println("------------2-------------\n");

        //collect all but exclude
        ReflectUtils.CollectsFieldProcessor collectAllButExclude = new ReflectUtils.CollectsFieldProcessor(null, field -> field.getDeclaringClass().isInterface());
        result = ReflectUtils.findDeclaredFieldSemantics(Cls.class, collectAllButExclude, 0);
        System.out.println(result);
        System.out.println(collectAllButExclude.results());
        System.out.println("------------3-------------\n");

        //collect all and crash depth >= 2
        ReflectUtils.DepthCrashCollectsFieldProcessor depthCrashProcessor = new ReflectUtils.DepthCrashCollectsFieldProcessor(2);
        result = ReflectUtils.findDeclaredFieldSemantics(Cls.class, depthCrashProcessor, 0);
        System.out.println(result);
        System.out.println(depthCrashProcessor.results());
        System.out.println("------------4-------------\n");


        //collect all and crash Inter2.class节点
        ReflectUtils.CollectsFieldProcessor selfCrashProcessor = new ReflectUtils.CollectsFieldProcessor() {
            @Override
            public boolean crash(Class<?> clazz, int depth) {
                if(clazz == Inter2.class) return true;
                return false;
            }
        };
        result = ReflectUtils.findDeclaredFieldSemantics(Cls.class, selfCrashProcessor, 0);
        System.out.println(result);
        System.out.println(selfCrashProcessor.results());
        System.out.println("------------5-------------\n");

        //find first match and crash Inter1.class节点
        ReflectUtils.MatchFirstFieldProcessor selfCrashProcessor2 = new ReflectUtils.MatchFirstFieldProcessor(field -> field.getName().equals("name")) {
            @Override
            public boolean crash(Class<?> clazz, int depth) {
                if(clazz == Inter1.class) return true;
                return false;
            }
        };
        result = ReflectUtils.findDeclaredFieldSemantics(Cls.class, selfCrashProcessor2, 0);
        System.out.println(result);

        System.out.println("--------------testFindFieldSemantics-----------\n");

    }


    interface Lnter2 {
        String m(String s);

        default String lnter2() {return null;}
    }
    interface H {
        String m(String s);

        default String h() {return null;}
    }
    interface J {
        String m(String s);

        default String j() {return null;}
    }
    interface Lnter1<G> extends H, J {
        String m(String s);

        void one1(G g);                      //public void one1(Object)

        default void lnter1(String s) {}
    }
    interface G {
        //String m(String s);
    }
    interface F {
        //String m(String s);

        default void f() {}
    }
    private static abstract class Muper2 {
        //public String m(String s) {return null;}

        void muper2() {}
    }
    private static abstract class Muper1 extends Muper2 implements F, G {
        //public String m(String s) {return null;}

        public <T> T two5(T t) {return null;}             //public Object two5(Object)

        String muper1() {return null;}
    }
    private static abstract class Mls extends Muper1 implements Lnter1<String>, Lnter2 {
        //public String m(String s) {return null;}

        public String two5(Object t) {return null;}    //public Object two5(Object t)

        public void one1(String g) {return;}           //public void one1(Object g)


    }
    //搜索路径 Mls, Muper1, Muper2, Object, F, G, Lnter1, H, J, Lnter2
    public static void testFindMethodSemantics() {

        //match first method
        ReflectUtils.MatchFirstMethodProcessor matchFirstProcessor = new ReflectUtils.MatchFirstMethodProcessor(ReflectUtils.MatchFirstMethodProcessor.defaultMatcher("m", String.class));
        Method result = ReflectUtils.findDeclaredMethodSemantics(Mls.class, matchFirstProcessor, 0);
        System.out.println(result);
        System.out.println("-----------1------------\n");

        //match first method,bridge场景1
        ReflectUtils.MatchFirstMethodProcessor matchFirstProcessor2 = new ReflectUtils.MatchFirstMethodProcessor(ReflectUtils.MatchFirstMethodProcessor.defaultMatcher("two5", Object.class));
        result = ReflectUtils.findDeclaredMethodSemantics(Mls.class, matchFirstProcessor2, 0);
        System.out.println(result);
        System.out.println("-----------2------------\n");

        //match first method,bridge场景2
        ReflectUtils.MatchFirstMethodProcessor matchFirstProcessor3 = new ReflectUtils.MatchFirstMethodProcessor(ReflectUtils.MatchFirstMethodProcessor.defaultMatcher("one1", Object.class));
        result = ReflectUtils.findDeclaredMethodSemantics(Mls.class, matchFirstProcessor3, 0);
        System.out.println(result);
        System.out.println("-----------3------------\n");

        //match first method and crash
        ReflectUtils.MatchFirstMethodProcessor matchFirstProcessor4 = new ReflectUtils.MatchFirstMethodProcessor(ReflectUtils.MatchFirstMethodProcessor.defaultMatcher("m", String.class)) {
            @Override
            public boolean crash(Class<?> clazz, int depth) {
                return clazz == Lnter1.class;
            }
        };
        result = ReflectUtils.findDeclaredMethodSemantics(Mls.class, matchFirstProcessor4, 0);
        System.out.println(result);
        System.out.println("-----------4------------\n");


        //collect all
        ReflectUtils.CollectsMethodProcessor collectAllProcessor = new ReflectUtils.CollectsMethodProcessor();
        result = ReflectUtils.findDeclaredMethodSemantics(Mls.class, collectAllProcessor, 0);
        System.out.println(result);
        System.out.println(collectAllProcessor.results());
        System.out.println("-----------5------------\n");

        //collect all but exclude bridge method and crash Object
        ReflectUtils.CollectsMethodProcessor collectAllProcessor2 = new ReflectUtils.CollectsMethodProcessor(null, ReflectUtils.CollectsMethodProcessor.defaultExclude()) {
            @Override
            public boolean crash(Class<?> clazz, int depth) {
                return clazz == Object.class;
            }
        };
        result = ReflectUtils.findDeclaredMethodSemantics(Mls.class, collectAllProcessor2, 0);
        System.out.println(result);
        System.out.println(collectAllProcessor2.results());
        System.out.println("-----------6------------\n");


        //collect all and detect override
        ReflectUtils.DetectOverrideCollectsMethodProcessor detectOverrideProcessor = new ReflectUtils.DetectOverrideCollectsMethodProcessor();
        result = ReflectUtils.findDeclaredMethodSemantics(Mls.class, detectOverrideProcessor, 0);
        System.out.println(result);
        System.out.println(detectOverrideProcessor.results());
        System.out.println("-----------7------------\n");
    }


}
