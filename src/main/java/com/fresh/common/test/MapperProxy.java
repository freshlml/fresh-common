package com.fresh.common.test;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperProxy<T> implements InvocationHandler, Serializable {
    private Class<T> proxyInterface;

    public MapperProxy(Class<T> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("前置执行");

        String result = "";
        HereSelect select = method.getAnnotation(HereSelect.class);
        if(select != null) {
            String proxyInterfaceName = proxyInterface.getTypeName();
            String methodName = method.getName(); //proxyInterfaceName + methodName 构成statement的唯一id
            System.out.println(proxyInterfaceName + "." + methodName + ": " + select.value()[0]);
            result = methodName;
        }

        System.out.println("后置执行");

        return result;
    }

}
