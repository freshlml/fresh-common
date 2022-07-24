package com.fresh.common.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectUtils_raw {

    private static final Predicate<Method> isNotBridge = method -> !method.isBridge();
    //private static final Predicate<Method> isUserDeclaredMethod = method -> !method.isSynthetic() && method.getDeclaringClass() != Object.class && !method.isBridge();


    /*
     * check方法签名是否相同
     */
    private static boolean checkMethodSignature(Method left, Method right) {
        if(left == null || right == null) return false;
        return left.getName().equals(right.getName()) && Arrays.equals(left.getParameterTypes(), right.getParameterTypes());
    }

    /*
     * 查找指定method, return null if not find
     * clazz参数不能为空，methodName参数不能为空
     *
     * 如果Class Object是一个interface，该interface及其继承结构上的所有方法
     * {@link ReflectUtils#findAllDeclaredMethodOnInterfaces}
     *
     * 如果Class Object是array,primitive，返回null
     *
     * 否则, 在当前Class Object的declaredMethods中找;在当前Class Object实现的接口及其继承结构中找default,static方法;getSuperClass重复上述过程
     *
     * 如果是bridge method，默认不在查找范围中
     * @param clazz
     * @param methodName
     * @param paramTypes
     * @return
     */
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        AssertUtils.ifNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.ifNull(methodName, () -> "参数methodName不能为空", null);

        Class<?> currentClazz = clazz;
        while(currentClazz != null && currentClazz != Object.class) {
            Method[] results;
            if(currentClazz.isInterface()) {
                results = findAllDeclaredMethodOnInterfaces(currentClazz);
            } else {
                Method[] declaredMethods = currentClazz.getDeclaredMethods();
                List<Method> instanceMethods = findInstanceMethodsOnInterfaces(currentClazz);
                results = resultHelp(declaredMethods, instanceMethods);
            }
            Optional<Method> methodResult = Arrays.stream(results)
                    .filter(method -> isNotBridge.test(method) && methodName.equalsIgnoreCase(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes())))
                    .findFirst();
            if(methodResult.isPresent()) return methodResult.get();

            currentClazz = currentClazz.getSuperclass();
        }

        return null;
    }
    private static Method[] resultHelp(Method[] declaredMethods, List<Method> instanceMethods) {
        Method[] results;
        if (instanceMethods != null && instanceMethods.size() > 0) {
            results = new Method[declaredMethods.length + instanceMethods.size()];
            System.arraycopy(declaredMethods, 0, results, 0, declaredMethods.length);
            int index = declaredMethods.length;
            for (int i = 0; i < instanceMethods.size(); i++) {
                results[index] = instanceMethods.get(i);
                index++;
            }
        } else {
            results = declaredMethods;
        }
        return results;
    }

    /*
     * Class Object是一个interface，通过Class.getMethods发现interface及其继承结构上的所有方法;采用BFS搜索继承结构中的static方法
     * 同时过滤掉bridge method
     * @param interfaceClazz interface Class
     * @return Method数组
     */
    private static Method[] findAllDeclaredMethodOnInterfaces(Class<?> interfaceClazz){
        AssertUtils.notNull(interfaceClazz, "参数clazz不能为空");

        List<Method> rawResult = new ArrayList<>();
        List<Method> result = new ArrayList<>();

        if(interfaceClazz.isInterface()) {
            Method[] methods = interfaceClazz.getMethods();
            rawResult.addAll(Arrays.asList(methods));
            findStaticMethodOnInterfacesBFS(interfaceClazz, rawResult);
            doWithMethods(rawResult, result::add, isNotBridge);
        }
        return result.toArray(new Method[0]);
    }

    private static void findStaticMethodOnInterfacesBFS(Class<?> clazz, List<Method> result) {
        Queue<Class<?>> queueList = new LinkedList<>();
        queueList.offer(clazz);
        boolean first = true;
        while(!queueList.isEmpty()) {
            Class<?> currentNode = queueList.poll();
            if(!first) {
                List<Method> staticMethods = Arrays.stream(currentNode.getDeclaredMethods()).filter(method -> Modifier.isStatic(method.getModifiers())).collect(Collectors.toList());
                result.addAll(staticMethods);
            } else { first = false; }

            Class<?>[] interfaces = currentNode.getInterfaces();
            Arrays.stream(interfaces).forEach(queueList::offer);
        }
    }


    /*
     * 如果Class Object是一个interface,返回该Class Object和其继承结构的default,static方法
     *
     * 否则，在其getInterfaces()中查找default，static方法
     *
     * 默认会过滤掉bridge method
     * @param clazz clazz参数不能为空
     * @return
     */
    public static List<Method> findInstanceMethodsOnInterfaces(Class<?> clazz) {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);
        List<Method> result = new ArrayList<>();
        findInstanceMethodsOnInterfaces(clazz, result);
        return result;
    }
    private static List<Method> findInstanceMethodsOnInterfaces(Class<?> clazz, List<Method> result) {
        if(clazz.isInterface()) {
            List<Method> rawResult = new ArrayList<>();
            Method[] methods = clazz.getMethods();
            rawResult.addAll(Arrays.asList(methods));
            findStaticMethodOnInterfacesBFS(clazz, rawResult);
            result.addAll(rawResult.stream().filter(method -> isNotBridge.test(method) && !Modifier.isAbstract(method.getModifiers())).collect(Collectors.toList()));
        } else {
            Class<?>[] interfaces = clazz.getInterfaces();
            for(Class<?> inter : interfaces) {
                findInstanceMethodsOnInterfaces(inter, result);
            }
        }
        return result;
    }


    /*
     * 查找所有的method
     * 参数clazz不能为空
     *
     * 如果Class Object是array,primitive，返回 数组[0]
     *
     * 否则: Class Object广度遍历继承树,对每一个Class Object调用getDeclaredMethods
     *
     * 默认过滤掉bridge method
     * @param clazz
     * @return
     */
    public static Method[] findAllDeclaredMethodsBFS(Class<?> clazz) {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);

        List<Method> result = new ArrayList<>();
        findDeclaredMethodsBFS(clazz, result::add);
        List<Method> methods = result.stream().filter(isNotBridge).collect(Collectors.toList());
        return methods.toArray(new Method[0]);
    }
    private static void findDeclaredMethodsBFS(Class<?> clazz, Consumer<Method> consumer) {
        Queue<Class<?>> queueList = new LinkedList<>();
        queueList.offer(clazz);

        while(!queueList.isEmpty()) {
            Class<?> currentNode = queueList.poll();
            Method[] methods = currentNode.getDeclaredMethods();
            Arrays.stream(methods).forEach(method -> consumer.accept(method));

            Class<?> superClass = currentNode.getSuperclass();
            if(superClass != null && superClass != Object.class) queueList.offer(superClass);
            Class<?>[] interfaces = currentNode.getInterfaces();
            Arrays.stream(interfaces).forEach(queueList::offer);
        }
    }

    /*
     * 查找所有的method
     * 参数clazz不能为空
     *
     * 如果Class Object是array,primitive，返回 数组[0]
     *
     * 否则: Class Object广度遍历继承树,对每一个Class Object调用getDeclaredMethods
     * 如果方法签名已存在，则不重复添加
     *
     * 默认过滤掉bridge method
     * @param clazz
     * @return
     */
    public static Method[] findAllOverDeclaredMethodsBFS(Class<?> clazz) {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);

        final List<Method> result = new ArrayList<>();
        findDeclaredMethodsBFS(clazz, method -> {
            Method bridgeMethod = null;
            boolean flag = false;
            for(Method existingMethod : result) {
                if (checkMethodSignature(existingMethod, method)) {
                    if (existingMethod.getReturnType() != method.getReturnType() &&
                            ClazzUtils.isAssignableFrom(existingMethod.getReturnType(), method.getReturnType())) {
                        bridgeMethod = existingMethod;
                    } else {
                        flag = true;
                    }
                    break;
                }
            }
            if(bridgeMethod != null) {
                result.remove(bridgeMethod);
            }
            if(!flag) {
                result.add(method);
            }
        });
        List<Method> methods = result.stream().filter(isNotBridge).collect(Collectors.toList());
        return methods.toArray(new Method[0]);
    }


    /*
     * methodPredicate,methodCallback
     * throws 抛出methodCallback执行时的任何异常
     * @param methods
     * @param methodCallback
     * @param methodFilter
     */
    public static void doWithMethods(List<Method> methods, Consumer<Method> methodCallback, Predicate<Method> methodFilter) {
        for(Method method : methods) {
            if(methodFilter != null && !methodFilter.test(method)) continue;
            if(methodCallback != null) {
                try {
                    methodCallback.accept(method);
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }


//TODO BridgeMethodResolver MethodIntrospector


}
