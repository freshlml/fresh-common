package com.fresh.common.utils.reflect.proxyjdk;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class A implements I {

    @SimpleAnnotation
    @Override
    public void m1() {
        System.out.println("A m1");
    }

    @TempAnnotation
    @Override
    public void m2() {
        System.out.println("A m2");
    }

    @Override
    public String toString() {
        return "A{}";
    }

    public static void main(String[] argv) throws Exception {
        System.out.println(I.class.getMethod("m1"));  //public abstract void com.juice.normal.reflect.proxy.I.m1()
        System.out.println(A.class.getMethod("m1")); //public void com.juice.normal.reflect.proxy.A.m1()

        A a = new A();
        I a_proxy = (I) Proxy.newProxyInstance(A.class.getClassLoader(), new Class[]{I.class}, new Ivh(a));

        System.out.println(a_proxy.getClass()); //class com.sun.proxy.$Proxy0
        System.out.println(a_proxy.getClass().getMethod("m1")); //public final void com.sun.proxy.$Proxy0.m1()

        System.out.println("####################");


        a_proxy.m1();
        a_proxy.m2();


        a_proxy.toString();
        a_proxy.hashCode();
        a_proxy.equals(null);

    }
    static class Ivh implements InvocationHandler {
        private I target;
        public Ivh(I target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //proxy: 代理对象，即a_proxy

            //public abstract void com.juice.normal.reflect.proxy.I.m1()
            //public abstract void com.juice.normal.reflect.proxy.I.m2()
            //public java.lang.String java.lang.Object.toString()
            //public native int java.lang.Object.hashCode()
            //public boolean java.lang.Object.equals(java.lang.Object)
            System.out.println(method);

            if(I.class.equals(method.getDeclaringClass())) {
                System.out.println(method.getName() + " from " + "I.class");
            } else if(Object.class.equals(method.getDeclaringClass())) {
                System.out.println(method.getName() + " from " + "Object.class");
            }
            System.out.println("-----------1-----------");

            Method p_m = proxy.getClass().getMethod(method.getName(), method.getParameterTypes());
            //public final void com.sun.proxy.$Proxy0.m1()
            //public final void com.sun.proxy.$Proxy0.m2()
            //public final java.lang.String com.sun.proxy.$Proxy0.toString()
            //public final int com.sun.proxy.$Proxy0.hashCode()
            //public final boolean com.sun.proxy.$Proxy0.equals(java.lang.Object)
            System.out.println(p_m);
            System.out.println("-----------2-----------");

            Method a_m = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            //public void com.juice.normal.reflect.proxy.A.m1()
            //public void com.juice.normal.reflect.proxy.A.m2()
            //public java.lang.String com.juice.normal.reflect.proxy.A.toString()
            //public native int java.lang.Object.hashCode()
            //public boolean java.lang.Object.equals(java.lang.Object)
            System.out.println(a_m);
            System.out.println("-----------3-----------");

            //null
            //System.out.println(method.getAnnotation(SimpleAnnotation.class));
            //null
            //System.out.println(method.getAnnotation(TempAnnotation.class));
            //null
            //System.out.println(p_m.getAnnotation(SimpleAnnotation.class));
            //null
            //System.out.println(p_m.getAnnotation(TempAnnotation.class));

            Object ret = null;
            SimpleAnnotation t = a_m.getAnnotation(SimpleAnnotation.class);
            if(t != null) {
                System.out.println("@SimpleAnnotation 前置执行");
                ret = method.invoke(target, args);
                System.out.println("@SimpleAnnotation 后置执行");
                System.out.println("-----------end-----------\n");
                return ret;
            }
            TempAnnotation at = a_m.getAnnotation(TempAnnotation.class);
            if(at != null) {
                System.out.println("@TempAnnotation 前置执行");
                ret = method.invoke(target, args);
                System.out.println("@TempAnnotation 后置执行");
                System.out.println("-----------end-----------\n");
                return ret;
            }
            ret = method.invoke(target, args);
            System.out.println("-----------end-----------\n");

            return ret;
        }
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface SimpleAnnotation {}

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface TempAnnotation {}

}
