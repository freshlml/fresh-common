package com.fresh.common.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyFactory<T> {
    private Class<T> proxyInterface;

    public ProxyFactory(Class<T> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    protected <T> T newProxy(InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(proxyInterface.getClassLoader(), new Class[]{ proxyInterface }, handler);
    }

    public <T> T newProxyMapper() {
        MapperProxy<UserMapperInterface> handler = new MapperProxy(proxyInterface);
        return newProxy(handler);
    }

    public static void main(String argv[]) {
        ProxyFactory<UserMapperInterface> factory = new ProxyFactory<>(UserMapperInterface.class);

        UserMapperInterface proxy = factory.newProxyMapper();

        String result = proxy.getUserName("user");

        System.out.println(result);

        ProxyFactory<InfoMapperInterface> factory2 = new ProxyFactory<>(InfoMapperInterface.class);

        InfoMapperInterface proxy2 = factory2.newProxyMapper();

        String result2 = proxy2.getInfoName("info");

        System.out.println(result2);
    }

}
