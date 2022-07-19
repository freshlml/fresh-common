package com.fresh.common.utils.reflect.anno;

import java.lang.annotation.*;

public class AnnotationTest {


    public static void main(String argv[]) {
        testAnnotationInterface();

    }


    //测试java.lang.Annotation接口中的方法
    public static void testAnnotationInterface() {
        //通过@interface定义的注解继承Annotation
        Class<?>[] simple_inters = SimpleAnnotation.class.getInterfaces();  //Annotation.class
        Class<? super SimpleAnnotation> simple_super = SimpleAnnotation.class.getSuperclass(); //null

        //注解的实例对象是一个动态代理类，该动态代理实现SimpleAnnotation
        //调用annotationType方法(Annotation接口中定义的方法) 而不是 getClass方法
        SimpleAnnotation a_simple_annotation = A.class.getAnnotation(SimpleAnnotation.class);          //SimpleAnnotation的实例对象
        Class<? extends SimpleAnnotation> annotation_instance_class = a_simple_annotation.getClass();  //Class<Proxy>
        Class<? extends Annotation> annotation_instance_annotationType = a_simple_annotation.annotationType(); //Class<SimpleAnnotation>
        if(annotation_instance_class == SimpleAnnotation.class) {
            System.out.println(1);
        }
        if(annotation_instance_annotationType == SimpleAnnotation.class) {
            System.out.println(2);
        }

        //Annotation接口中equals等方法
        SimpleAnnotation b_simple_annotation = B.class.getAnnotation(SimpleAnnotation.class);
        System.out.println(a_simple_annotation.equals(b_simple_annotation)); //false


        System.out.println("----------testAnnotationInterface--------------");
    }
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface SimpleAnnotation {
        String value() default "";
    }
    @SimpleAnnotation
    private static class A {}
    @SimpleAnnotation("2")
    private static class B {}

}
