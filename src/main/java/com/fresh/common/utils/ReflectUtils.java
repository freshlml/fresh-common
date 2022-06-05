package com.fresh.common.utils;

import com.fresh.common.exception.BizException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ReflectUtils {

    private static final Predicate<Method> isNotBridge = method -> !method.isBridge();
    private static final Predicate<Method> isUserDeclaredMethod = method -> !method.isSynthetic() && method.getDeclaringClass() != Object.class && !method.isBridge();

////
//Constructor
////
    /**
     * 判断是否有public构造器
     * @see ReflectUtils#getConstructor
     * @param clazz
     * @param paramTypes
     * @return
     */
    public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
        return getConstructor(clazz, paramTypes) != null;
    }

    /**
     * packing Class.getConstructor
     * @see Class##getConstructor(Class, Class)
     * 获取Class的public构造器，if NoSuchMethodException return null
     * clazz参数不能为null，如不传paramTypes，或者paramTypes=null，表示获取无参构造器
     *
     * 如果Class Object的构造器参数是TypeVariable,eg: class Leaf<T> { public Leaf(T t){} } ;则paramTypes=Class<?>[]{Object.class} (泛型擦除)
     *                                         eg: class Loop { public <T extends Number> Loop(T t){} } ;则paramTypes=Class<?>[]{Number.class} (泛型擦除，向上转型)
     *
     * 如果Class Object的构造器参数是GenericArrayType,eg: class Loop { public <T> Loop(T[] t){} } ;则paramTypes=Class<?>[]{Object[].class} (泛型变量数组)
     *                                             eg: class Loop { public <T> Loop(List<T>[] t){} } ;则paramTypes=Class<?>[]{List[].class} (泛型类型数组)
     *
     * 如果Class Object的构造器参数是ParameterizedType.eg: clas Loop { public <T> Loop(Loop<T> lt) } ;则paramTypes=Class<?>[]{Loop.class}
     * @param clazz
     * @param paramTypes
     * @return Constructor，or null if NoSuchMethod
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... paramTypes) {
        AssertUtils.ifNull(clazz, () -> "参数clazz不能为空", null);
        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * packing Class.getDeclaredConstructor
     * @see Class#getDeclaredConstructor(Class[])
     * 获取Class的构造器，and makeAccessible,if NoSuchMethodException return null
     * clazz参数不能为null,paramTypes的说明{@link ReflectUtils#getConstructor}
     * @param clazz
     * @param paramTypes
     * @return
     */
    public static Constructor<?> getDeclaredConstructor(Class<?> clazz, Class<?>... paramTypes) {
        AssertUtils.ifNull(clazz, () -> "参数clazz不能为空", null);
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(paramTypes);
            makeAccessible(c);
            return c;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }


////
//Method
////
    /**
     * packing Class.getMethod, {@link Class#getMethod} if NoSuchMethodException return null
     *
     * clazz参数不能为null,methodName参数不能为空,paramTypes不传，或者paramTypes=null,表示获取无参方法
     *
     * 如果Class Object的参数是TypeVariable,eg: genericTT(T t),则paramTypes=Class<?>[]{Object.class} (泛型擦除)
     *
     * 如果Class Object的参数是GenericArrayType,eg genericTT(T[], List<T>[]),则paramTypes=Class<?>[]{Object[].class, List[].class}
     *
     * 如果Class Object的参数是ParameterizedType,eg generic(Loop<T>),则paramTypes=Class<?>[]{Loop.class}
     *
     * 如果此Class Object中生成了bridge method,并且生成的bridge method与声明的method拥有相同的方法声明
     * Class.getMethod会判断return type，优先返回return type more specific的
     * 而一般bridge method的return type 更模糊
     *
     * @param clazz
     * @param methodName
     * @param paramTypes
     * @return
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        AssertUtils.ifNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.ifNull(methodName, () -> "参数methodName不能为空", null);
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * packing Class.getDeclaredMethod,获取Class Object中的method,
     *
     * {@link Class#getDeclaredMethod} if NoSuchMethodException return null
     *
     * clazz参数不能为null,methodName参数不能为空，paramTypes不传，或者paramTypes=null,表示获取无参方法
     *
     * paramTypes参数,bridge method  {@link ReflectUtils#getMethod(Class, String, Class[])}
     * @param clazz
     * @param methodName
     * @param paramTypes
     * @return
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        AssertUtils.ifNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.ifNull(methodName, () -> "参数methodName不能为空", null);
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * check方法签名是否相同
     * @param left
     * @param right
     * @return
     */
    private static boolean checkMethodSignature(Method left, Method right) {
        if(left == null || right == null) return false;
        return left.getName().equals(right.getName()) && Arrays.equals(left.getParameterTypes(), right.getParameterTypes());
    }

    /**
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

    /**
     * Class Object是一个interface，通过Class.getMethods发现interface及其继承结构上的所有方法;采用BFS搜索继承结构中的static方法
     * 同时过滤掉bridge method
     * @param interfaceClazz
     * @return
     */
    public static Method[] findAllDeclaredMethodOnInterfaces(Class<?> interfaceClazz){
        AssertUtils.notNull(interfaceClazz, () -> "参数clazz不能为空", null);
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


    /**
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


    /**
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

    /**
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


    /**
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

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }
//TODO BridgeMethodResolver MethodIntrospector

////
//Field
////
    /**
     * packing Class.getField {@link Class#getField} if NoSuchFieldException return null
     *
     * clazz参数不能为null,fieldName参数不能为空
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.notNull(fieldName, () -> "参数fieldName不能为空", null);

        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * 查找指定field
     * 查找顺序: 1.Class Object的declaredFields中查找，如果找不到，进行第2步
     *     2.在Class Object的interfaces中深度递归查找，如果找不到，进行第3步
     *     3.在Class Object的superclass中深度递归查找
     *     如果找不到，返回null
     * @param clazz 不能为空
     * @param fieldName 不能为空
     * @param fieldType 如果不为空，表示判断类型
     * @return
     */
    public static Field findDeclaredField(Class<?> clazz, String fieldName, Class<?> fieldType) {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.notNull(fieldName, () -> "参数fieldName不能为空", null);

        return findDeclaredField(clazz, field -> field.getName().equals(fieldName) && (fieldType == null || fieldType == field.getType()));
    }

    /**@see ReflectUtils#findDeclaredField*/
    public static Field findDeclaredField(Class<?> clazz, Predicate<Field> predicate) {
        Field[] fields = clazz.getDeclaredFields();
        Optional<Field> found = Arrays.stream(fields).filter(predicate).findFirst();
        if(found.isPresent()) return found.get();

        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> inter : interfaces) {
            Field result = findDeclaredField(inter, predicate);
            if(result != null) return result;
        }
        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null && superClass != Object.class) {
            Field result = findDeclaredField(superClass, predicate);
            if(result != null) return result;
        }

        return null;
    }

    /**
     * 查找所有field
     *  查找范围: 1.Class Object的declaredFields
     *      2.在Class Object的interfaces中深度递归
     *      3.在Class Object的superclass中深度递归
     * @param clazz 不能为空
     * @return
     */
    public static Field[] findAllDeclaredFields(Class<?> clazz) {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);

        List<Field> result = new ArrayList<>();
        findDeclaredFieldConsumer(clazz, result::add);
        return result.toArray(new Field[0]);
    }
    /**@see ReflectUtils#findAllDeclaredFields*/
    public static void findDeclaredFieldConsumer(Class<?> clazz, Consumer<Field> consumer) {
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields).forEach(consumer);

        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> inter : interfaces) {
            findDeclaredFieldConsumer(inter, consumer);
        }
        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null && superClass != Object.class) findDeclaredFieldConsumer(superClass, consumer);

    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public static Map<String, Object> bean2Map(Object bean) {
        if(bean == null) return null;
        Map<String, Object> map = new HashMap<>();

        findDeclaredFieldConsumer(bean.getClass(), field -> {
            try {
                makeAccessible(field);
                map.put(field.getName(), field.get(bean));//浅拷贝
            } catch (IllegalAccessException e) {
                throw new BizException(() -> e.getMessage());
            }
        });
        return map;
    }

}
