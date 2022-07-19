package com.fresh.common.utils.reflect.anno;

import java.lang.annotation.*;
import java.util.Arrays;

public class ForTestAnnotatedElement {

    public static void main(String argv[]) {

        testAnnotatedElement();
    }


    private static void testAnnotatedElement() {

        Class<Child> childClass = Child.class;
        Class<Super> superClass = Super.class;

        System.out.println(childClass.getDeclaredAnnotation(Annotation5.class)); //null
        System.out.println(childClass.getDeclaredAnnotation(Annotation2.class)); //null

        Annotation[] declaredAnnotations = childClass.getDeclaredAnnotations();
        //Annotation1("child 1");
        //Annotation2Container([Annotation2("child 2 before"), Annotation2("child 2 after")])
        //Annotation3("child 3")
        Arrays.stream(declaredAnnotations).forEach(an -> System.out.println(an));
        System.out.println("------------1----------");


        System.out.println(childClass.getAnnotation(Annotation2.class)); //null
        //Annotation1(value=child 1)
        System.out.println(childClass.getAnnotation(Annotation1.class));
        //Annotation2Container([Annotation2("child 2 before"), Annotation2("child 2 after")])
        System.out.println(childClass.getAnnotation(Annotation2Container.class));
        //Annotation3("child 3")
        System.out.println(childClass.getAnnotation(Annotation3.class));
        //Annotation5("super 5")
        System.out.println(childClass.getAnnotation(Annotation5.class));
        System.out.println("------------2----------");

        Annotation[] annotations = childClass.getAnnotations();
        //Annotation1(value=child 1)
        //Annotation2Container([Annotation2("child 2 before"), Annotation2("child 2 after")])
        //Annotation5("super 5")
        //Annotation3("child 3")
        Arrays.stream(annotations).forEach(an -> System.out.println(an));
        System.out.println("------------3----------");

        Annotation1[] one = childClass.getDeclaredAnnotationsByType(Annotation1.class);
        //1, Annotation1(value=child 1)
        System.out.println(one.length + ", " + one[0]);
        Annotation2Container[] two = childClass.getDeclaredAnnotationsByType(Annotation2Container.class);
        //1, Annotation2Container([Annotation2("child 2 before"), Annotation2("child 2 after")])
        System.out.println(two.length + ", " + two[0]);
        Annotation3[] three = childClass.getDeclaredAnnotationsByType(Annotation3.class);
        //1, Annotation3("child 3")
        System.out.println(three.length + ", " + three[0]);
        System.out.println("------------4----------");

        Annotation2[] declaredAnnotationsByType = childClass.getDeclaredAnnotationsByType(Annotation2.class);
        //Annotation2(value=child 2 before)
        //Annotation2(value=child 2 after)
        Arrays.stream(declaredAnnotationsByType).forEach(an -> System.out.println(an));
        System.out.println("------------5----------");

        Annotation5[] five = childClass.getAnnotationsByType(Annotation5.class);
        //Annotation5("super 5")
        System.out.println(five.length + ", " + five[0]);

        Annotation2[] annotationsByType = childClass.getAnnotationsByType(Annotation2.class);
        //Annotation2(value=child 2 before)
        //Annotation2(value=child 2 after)
        Arrays.stream(annotationsByType).forEach(an -> System.out.println(an));

        System.out.println("------------6----------");

    }

    @Annotation1("super 1")
    @Annotation2("super 2 before")
    @Annotation2("super 2 after")
    @Annotation5("super 5")
    private static class Super{}

    @Annotation1("child 1")
    @Annotation2("child 2 before")
    @Annotation2("child 2 after")
    @Annotation3("child 3")
    private static class Child extends Super{}

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Annotation1 {
        String value() default "1";
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(Annotation2Container.class)
    @interface Annotation2 {
        String value() default "2";
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Annotation2Container {
        Annotation2[] value();
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Annotation3 {
        String value() default "3";
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Annotation5 {
        String value() default "5";
    }

}
