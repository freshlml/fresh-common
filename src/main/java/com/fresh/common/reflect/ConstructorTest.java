package com.fresh.common.reflect;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;

public class ConstructorTest {

    public static void main(String argv[]) throws Exception {

        testParameterAnnotations();
    }

    private static class Cons {

        public Cons(@Annotation1 @Annotation2 @Annotation2 int i) {

        }

    }
    private static void testParameterAnnotations() throws NoSuchMethodException {
        Constructor<Cons> cons = Cons.class.getConstructor(int.class);
        Annotation[][] parameterAnnos = cons.getParameterAnnotations();


        System.out.println("---------testParameterAnnotations--------");
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE,ElementType.PARAMETER})
    @interface Annotation1 {
        String value() default "1";
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE,ElementType.PARAMETER})
    @Repeatable(Annotation2Container.class)
    @interface Annotation2 {
        String value() default "2";
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE,ElementType.PARAMETER})
    @interface Annotation2Container {
        Annotation2[] value();
    }


}
