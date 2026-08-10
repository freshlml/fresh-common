package com.fresh.core.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ReflectUtils {

    private ReflectUtils() {}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Constructor
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * <p>封装 {@link Class#getConstructor(Class[])} </p>
     *
     * <p>查找 public 构造器。当 Class#getConstructor 抛出 NoSuchMethodException 时，return null.</p>
     *
     * <p>If this Class object represents an inner class declared in a non-static context, the
     * formal parameter types include the explicit enclosing instance as the first parameter.</p>
     *
     * <p>声明时：编译时类型。getConstructor 方法参数传递：使用运行时类型</p>
     * <ul>
     *     <li>如果 Class Object 的构造器参数是 TypeVariable
     *         <ul>
     *             <li>class Leaf<T> { Leaf(T t){} } ; 则 paramTypes = Class<?>[]{ Object.class } (类型擦除)</li>
     *             <li>class Loop { <T extends Number> Loop(T t){} } ; 则 paramTypes = Class<?>[]{ Number.class } (类型擦除)</li>
     *         </ul>
     *     </li>
     *     <li>如果 Class Object 的构造器参数是 GenericArrayType
     *         <ul>
     *             <li>class Loop { <T> Loop(T[] t){} } ; 则 paramTypes = Class<?>[]{ Object[].class } (类型擦除)</li>
     *             <li>class Loop { <T> Loop(List<T>[] t){} } ; 则 paramTypes = Class<?>[]{ List[].class } (类型擦除)</li>
     *         </ul>
     *     </li>
     *     <li>如果 Class Object 的构造器参数是 ParameterizedType
     *         <ul>
     *             <li>class Loop { <T> Loop(Loop<T> lt) } ; 则 paramTypes = Class<?>[]{ Loop.class } (类型擦除)</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param clazz class, not null
     * @param paramTypes 参数列表，如果不传 paramTypes 或者传 null 或者传 empty array，表示获取无参构造器
     * @return Constructor or null
     * @throws SecurityException propagates the SecurityException of Class#getConstructor(...)
     * @throws NullPointerException if the specified clazz is null
     */
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... paramTypes) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");

        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


    /**
     * <p>封装 {@link Class#getDeclaredConstructor(Class[])}</p>
     *
     * <p>查找 public、protected、package、private 构造器。当 Class#getConstructor 抛出 NoSuchMethodException 时，return null.</p>
     *
     * <p>If this Class object represents an inner class declared in a non-static context, the
     * formal parameter types include the explicit enclosing instance as the first parameter.</p>
     *
     * @param clazz class, not null
     * @param paramTypes 参数列表，如果不传 paramTypes 或者传 null 或者传 empty array，表示获取无参构造器
     * @return Constructor or null
     * @throws SecurityException propagates the SecurityException of Class#getDeclaredConstructor(...)
     * @throws NullPointerException if the specified clazz is null
     */
    public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... paramTypes) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");

        try {
            Constructor<T> c = clazz.getDeclaredConstructor(paramTypes);
            makeAccessible(c);
            return c;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


    /**
     * <p>封装 {@link Constructor#newInstance(Object...)}</p>
     *
     * <p>构造实例对象</p>
     *
     * <p>If the constructor's declaring class is an inner class in a non-static
     * context, the first argument to the constructor needs to be the enclosing instance</p>
     *
     * @param constructor 构造器
     * @param initargs 构造器参数，如果不传或者传 null 或者传 empty array，表示无参构造器
     * @param <T> 泛型参数
     * @return 实例对象
     * @throws InstantiationException       实例化失败，如构造器的declaring class is abstract等问题
     * @throws InvocationTargetException    构造器执行抛出的异常，封装成InvocationTargetException抛出
     * @throws IllegalArgumentException     参数数量不匹配，类型不匹配等参数问题
     * @throws NullPointerException         if the specified constructor is null
     * @throws ExceptionInInitializerError  if the initialization provoked by this method fails
     */
    public static <T> T newInstance(Constructor<T> constructor, Object ... initargs)
            throws InstantiationException, InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        Assert.notNull(constructor, "constructor 参数不能为空");

        try {
            return constructor.newInstance(initargs);
        } catch (IllegalAccessException e) {
            makeAccessible(constructor);
            return constructor.newInstance(initargs);
        }
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Method
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * <p>封装 {@link Class#getMethod}</p>
     *
     * <p>查找 public method, 任何接口中的 static 方法不在查找范围中。如果 Class.getMethod 触发 NoSuchMethodException, return null</p>
     *
     * <p>查找逻辑: 先根深度优先</p>
     *
     * <ol>
     *     <li>在 Class Object 中查找，如果找不到，进行第 2 步</li>
     *     <li>在 superclass 中递归，如果找不到，进行第 3 步</li>
     *     <li>在 superinterface 中递归，如果找不到，throw NoSuchMethodException</li>
     * </ol>
     *
     * <p>If more than one such method is found in C, and one of these methods has a return type that is more specific than any of the others, that method is reflected;
     * otherwise one of the methods is chosen arbitrarily. Note that JVM could declare multiple methods with the same signature(bridge method)</p>
     *
     * <p>声明时：编译时类型。getConstructor 方法参数传递：使用运行时类型</p>
     * <ul>
     *     <li>如果 Class Object 的构造器参数是 TypeVariable
     *         <ul>
     *             <li>class Leaf<T> { Leaf(T t){} } ; 则 paramTypes = Class<?>[]{ Object.class } (类型擦除)</li>
     *             <li>class Loop { <T extends Number> Loop(T t){} } ; 则 paramTypes = Class<?>[]{ Number.class } (类型擦除)</li>
     *         </ul>
     *     </li>
     *     <li>如果 Class Object 的构造器参数是 GenericArrayType
     *         <ul>
     *             <li>class Loop { <T> Loop(T[] t){} } ; 则 paramTypes = Class<?>[]{ Object[].class } (类型擦除)</li>
     *             <li>class Loop { <T> Loop(List<T>[] t){} } ; 则 paramTypes = Class<?>[]{ List[].class } (类型擦除)</li>
     *         </ul>
     *     </li>
     *     <li>如果 Class Object 的构造器参数是 ParameterizedType
     *         <ul>
     *             <li>class Loop { <T> Loop(Loop<T> lt) } ; 则 paramTypes = Class<?>[]{ Loop.class } (类型擦除)</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param clazz clazz，不能为空
     * @param methodName methodName，不能为空
     * @param paramTypes 参数列表，如果不传 paramTypes 或者传 null 或者传 empty array，表示无参
     * @return Method or null
     * @throws SecurityException            propagates the SecurityException of Class#getMethod(...)
     * @throws NullPointerException         if the specified clazz or method is null
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");
        Assert.notNull(methodName, "参数 methodName 不能为空");

        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * <p>封装 {@link Class#getDeclaredMethod(String, Class[])}</p>
     *
     * <p>查找 public、private、protected、package method，包括 static 方法。如果 Class.getDeclaredMethod 触发 NoSuchMethodException，return null</p>
     *
     * <p>只在 Class Object 声明的 method 中找</p>
     *
     * <p>If more than one such method is found in C, and one of these methods has a return type that is more specific than any of the others, that method is reflected;
     * otherwise one of the methods is chosen arbitrarily. Note that JVM could declare multiple methods with the same signature(bridge method)</p>
     *
     * @param clazz Class，不能为空
     * @param methodName methodName，不能为空
     * @param paramTypes 参数列表，如果不传 paramTypes 或者传 null 或者传 empty array，表示无参
     * @return Method or null
     * @throws SecurityException            propagates the SecurityException of Class#getDeclaredMethod(...)
     * @throws NullPointerException         if the specified clazz or method is null
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");
        Assert.notNull(methodName, "参数 methodName 不能为空");

        try {
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            makeAccessible(method);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }


    /**
     * <p>封装 {@link Method#invoke(Object, Object...)}</p>
     *
     * <p>Invokes this Method, on the specified object with the specified parameters</p>
     *
     * <p>If the underlying method is static, then the specified obj argument is ignored. It could be null</p>
     *
     * <p>If the number of formal parameters required by the underlying method is 0, the supplied args array may be of length 0 or null</p>
     *
     * <p>If the underlying method is an instance method, it is invoked using dynamic method lookup</p>
     *
     * <p>If Method is not static method and the specified obj is null, do nothing return null</p>
     *
     * @param method Method，不能为空
     * @param obj    Object
     * @param args   参数列表，如果不传或者传 null 或者传 empty array，表示无参
     * @return 方法执行结果 or null
     * @throws InvocationTargetException    Method 执行抛出的异常，封装成 InvocationTargetException 后抛出
     * @throws IllegalArgumentException     if the Method is an instance method and the param obj.class is not assignable to Method的declaring class or 参数不匹配
     * @throws ExceptionInInitializerError  if the initialization provoked by this method fails
     * @throws NullPointerException         if the specified method is null
     */
    public static Object invoke(Method method, Object obj, Object... args)
            throws InvocationTargetException, IllegalArgumentException, ExceptionInInitializerError, IllegalAccessException {
        Assert.notNull(method, "参数 method 不能为空");

        if(!Modifier.isStatic(method.getModifiers()) && obj == null) {
            return null;
        }

        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            makeAccessible(method);
            return method.invoke(obj, args);
        }
    }

    /*
     * 如果 left 与 right 有相同的方法签名，返回 true
     */
    private static boolean isSameSignature(Method left, Method right) {
        if(left == null || right == null) return false;
        return left.getName().equals(right.getName()) &&
                Arrays.equals(left.getParameterTypes(), right.getParameterTypes());
    }

    /*
     * Return the specified method if it is not bridge method, otherwise return 与该桥接方法有相同方法签名并且 more specific return type 的"被桥接方法"或者 null
     */
    private static Method findBridgedMethodSignature(Method method) {
        Assert.notNull(method, "参数 method 不能为空");

        if(!method.isBridge()) return method;

        Method[] methods = method.getDeclaringClass().getDeclaredMethods();
        for(Method md : methods) {
            if(!md.equals(method) && isSameSignature(md, method) && ClazzUtils.isAssignableFrom(method.getReturnType(), md.getReturnType()))
                return md;
        }

        return null;
    }

    /*
     * Return the specified method if it is not bridge method, otherwise return 该桥接方法的"被桥接方法"或者 null
     */
    private static Method findBridgedMethod(Method method) {
        Assert.notNull(method, "参数 method 不能为空");

        if(!method.isBridge()) return method;

        Method[] methods = method.getDeclaringClass().getDeclaredMethods();
        for(Method md : methods) {
            if(isBridgeMethod(method, md))
                return md;
        }

        return null;
    }

    /*
     * 判断 bridgeMethod 是否是 bridgedMethod 的桥接方法
     */
    private static boolean isBridgeMethod(Method bridgeMethod, Method bridgedMethod) {
        return bridgeMethod.getDeclaringClass() == bridgedMethod.getDeclaringClass() &&
               !bridgeMethod.equals(bridgedMethod) &&
               bridgeMethod.isBridge() &&
               !bridgedMethod.isBridge() &&
               bridgeMethod.getName().equals(bridgedMethod.getName()) &&
               ClazzUtils.isAssignableFrom(bridgeMethod.getReturnType(), bridgedMethod.getReturnType()) &&
               isParameterAssignableFrom(bridgeMethod, bridgedMethod);
    }

    private static boolean isParameterAssignableFrom(Method left, Method right) {
        if(left == null || right == null) return false;
        if(left.getParameterCount() != right.getParameterCount()) return false;

        Class<?>[] leftParams = left.getParameterTypes();
        Class<?>[] rightParams = right.getParameterTypes();
        for(int i=0; i < left.getParameterCount(); i++) {
            if(!ClazzUtils.isAssignableFrom(leftParams[i], rightParams[i]))
                return false;
        }

        return true;
    }


    /*
     * Return the specified method if it is bridge method, otherwise return 与该方法的"桥接方法"或者 null 如果该方法没有桥接方法
     */
    private static Method findBridgeMethod(Method method) {
        Assert.notNull(method, "参数 method 不能为空");

        if(method.isBridge()) return method;

        Method[] methods = method.getDeclaringClass().getDeclaredMethods();
        for(Method bridgeCandidate : methods) {
            if(isBridgeMethod(bridgeCandidate, method))
                return bridgeCandidate;
        }

        return null;
    }


    /**
     * <p>find declared method semantics: Class Object 及其继承结构形成了一颗树，对该树进行的先根深度优先搜索，搜索 Class 的 declared method</p>
     *
     * <p>根据 MethodRecursiveProcessor 的不同实现，可以做到 find first match, collect all, collect all but exclude some, collect all and detect override, and crash 等功能</p>
     *
     * <p>crash 功能: 如果当前节点 crash 返回 true, 则当前节点及其之上的继承结构被忽略，既可用在 find first 逻辑中，也可用在 collect all 逻辑中</p>
     *
     * @param clazz Class, 不能为空
     * @param methodProcessor MethodRecursiveProcessor, 不能为空
     * @param depth 递归深度，0 表示第一层
     * @return Method or null
     * @throws SecurityException  propagates the SecurityException of Class#getDeclaredMethods()
     */
    public static Method findDeclaredMethodSemantics(Class<?> clazz, MethodRecursiveProcessor methodProcessor, int depth) throws SecurityException {
        if(methodProcessor.crash(clazz, depth)) return null;

        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods) {
            Method result = methodProcessor.handler(method, depth);
            if(result != null) return result;
        }
        if(methodProcessor.afterCrash(clazz, methods)) return null;

        Class<?> superClazz = clazz.getSuperclass();
        if(superClazz != null) {
            Method result = findDeclaredMethodSemantics(superClazz, methodProcessor, depth + 1);
            if(result != null) return result;
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> inter : interfaces) {
            Method result = findDeclaredMethodSemantics(inter, methodProcessor, depth + 1);
            if(result != null) return result;
        }

        return null;
    }

    public interface MethodRecursiveProcessor {
        Method handler(Method method, int depth);
        default List<Method> results() {
            return new ArrayList<>();
        }
        default boolean crash(Class<?> clazz, int depth) {
            return false;
        }
        default boolean afterCrash(Class<?> clazz, Method[] methods) {
            return false;
        }
    }

    public static class MatchFirstMethodProcessor implements MethodRecursiveProcessor {
        private final Predicate<Method> predicate;

        public MatchFirstMethodProcessor(Predicate<Method> predicate) {
            Assert.notNull(predicate, "参数 predicate 不能为空");
            this.predicate = predicate;
        }

        /*//returnType参数用于区分相同方法签名的场景
        public static Predicate<Method> defaultMatcher(String methodName, Class<?> returnType, Class<?>... paramTypes) {
            Assert.notNull(methodName, "参数 methodName 不能为空");

            return method -> method.getName().equals(methodName) &&
                   ((paramTypes == null && method.getParameterCount()==0) ||
                    (paramTypes != null && Arrays.equals(method.getParameterTypes(), paramTypes))) &&
                   (returnType == null || returnType == method.getReturnType());
        }*/
        public static Predicate<Method> defaultMatcher(String methodName, Class<?>... paramTypes) {
            Assert.notNull(methodName, "参数 methodName 不能为空");

            return method -> method.getName().equals(methodName) &&
                    ((paramTypes == null && method.getParameterCount() == 0) ||
                            (paramTypes != null && Arrays.equals(method.getParameterTypes(), paramTypes)));
        }

        @Override
        public Method handler(Method method, int depth) {
            if(predicate.test(method)) {
                if(!method.isBridge()) return method;
                else {
                    //找与该 bridge method 有相同签名 and more specific return type 的 bridged method
                    Method bridgedMethod = ReflectUtils.findBridgedMethodSignature(method);
                    if(bridgedMethod == null) return method;

                    return bridgedMethod;
                }
            }

            return null;
        }
    }

    public static class CollectsMethodProcessor implements MethodRecursiveProcessor {
        private final List<Method> collects = new ArrayList<>();
        private Consumer<Method> consumer;
        private Predicate<Method> exclude;

        public CollectsMethodProcessor() {}
        public CollectsMethodProcessor(Consumer<Method> consumer, Predicate<Method> exclude) {
            this.consumer = consumer;
            this.exclude = exclude;
        }

        public static Predicate<Method> defaultExclude() {
            return Method::isBridge;
        }

        @Override
        public Method handler(Method method, int depth) {
            if(exclude != null && exclude.test(method)) return null;

            if(consumer != null) consumer.accept(method);
            collects.add(method);

            return null;
        }

        @Override
        public List<Method> results() {
            return collects;
        }

        protected Predicate<Method> getExclude() {
            return this.exclude;
        }

        protected Consumer<Method> getConsumer() {
            return this.consumer;
        }
    }

    public static final class DetectOverrideCollectsMethodProcessor extends CollectsMethodProcessor {

        public DetectOverrideCollectsMethodProcessor() {}
        public DetectOverrideCollectsMethodProcessor(Consumer<Method> consumer) {
            super(consumer, null);
        }

        private boolean overrideHandler(Method method) {
            if(!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers())) return true;

            boolean shouldAdd = true;
            Method removeBridgeMethod = null;
            for(Method exists : results()) {

                if(isSameSignature(exists, method) &&
                   ClazzUtils.isAssignableFrom(method.getReturnType(), exists.getReturnType()) &&
                   (exists.getDeclaringClass() != method.getDeclaringClass() && ClazzUtils.isAssignableFrom(method.getDeclaringClass(), exists.getDeclaringClass()))) {

                    if(exists.isBridge()) {
                        removeBridgeMethod = exists;
                    }

                    shouldAdd = false;
                }

            }

            if(removeBridgeMethod != null) results().remove(removeBridgeMethod);
            return shouldAdd;
        }

        @Override
        public Method handler(Method method, int depth) {
            boolean shouldAdd = overrideHandler(method);
            if(!shouldAdd) return null;

            return super.handler(method, depth);
        }

        @Override
        public final boolean crash(Class<?> clazz, int depth) {
            return clazz == Object.class;
        }

    }

    public static final class MethodPriorityCollectsMethodProcessor extends CollectsMethodProcessor {

        private final Class<?> originalClazz;
        private boolean findOne = false;

        public MethodPriorityCollectsMethodProcessor(Class<?> originalClazz, Consumer<Method> consumer, String methodName, Class<?>... paramTypes) {
            super(consumer, (Method method) -> method.isBridge() || !(method.getName().equals(methodName) &&
                                                                     ((paramTypes == null && method.getParameterCount() == 0) ||
                                                                      (paramTypes != null && Arrays.equals(method.getParameterTypes(), paramTypes)))));
            this.originalClazz = originalClazz;
        }

        @Override
        public Method handler(Method method, int depth) {
            if(getExclude() != null && getExclude().test(method)) return null;

            if(getConsumer() != null) getConsumer().accept(method);

            if(method.getDeclaringClass() == originalClazz) return method;
            if(!method.getDeclaringClass().isInterface() && !Modifier.isAbstract(method.getModifiers())) return method;

            results().add(method);
            this.findOne = true;

            return null;
        }

        @Override
        public boolean afterCrash(Class<?> clazz, Method[] methods) {
            if(findOne) {
                findOne = false;
                return true;
            }
            return false;
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Field
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * <p>封装 {@link Class#getField(String)}</p>
     *
     * <p>查找 public field，包括 static 字段。如果 Class.getField 触发 NoSuchFieldException，return null</p>
     *
     * <p>查找逻辑: 先根深度优先</p>
     *
     * <ol>
     *     <li>在 Class Object 中查找，如果找不到，进行第 2 步</li>
     *     <li>在 superinterface 中递归，如果找不到，进行第 3 步</li>
     *     <li>在 superclass 中递归，如果找不到，return null</li>
     * </ol>
     *
     * @param clazz class，不能为空
     * @param fieldName fieldName, 不能为空
     * @return Field or null
     * @throws SecurityException     propagates the SecurityException of Class#getField(...)
     * @throws NullPointerException  if the specified clazz or field is null
     */
    public static Field getField(Class<?> clazz, String fieldName) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");
        Assert.notNull(fieldName, "参数 fieldName 不能为空");

        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }


    /**
     * <p>{@link Class#getDeclaredField(String)}</p>
     *
     * <p>查找 public, private, protected, package field，包括 static 字段。如果 Class.getDeclaredField 触发 NoSuchFieldException，return null</p>
     *
     * <p>只在 Class Object 声明的 field 中找</p>
     *
     * @param clazz class，不能为空
     * @param fieldName fieldName，不能为空
     * @return Field or null
     * @throws SecurityException     propagates the SecurityException of Class#getDeclaredField(...)
     * @throws NullPointerException  if the specified clazz or field is null
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");
        Assert.notNull(fieldName, "参数 fieldName 不能为空");

        try {
            Field field = clazz.getDeclaredField(fieldName);
            makeAccessible(field);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }


    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }


    /**
     * <p>封装 {@link Field#get(Object)}</p>
     *
     * <p>If this field is a static field, the obj argument is ignored; it may be null</p>
     *
     * <p>If the field has a primitive type, the value is wrapped in an object before being returned</p>
     *
     * <p>If Field is instance field and the specified obj is null, do nothing return null</p>
     *
     * @param field Field, 不能为空
     * @param obj   实例对象
     * @return Field 的值 or null when Field is instance field and param obj is null
     * @throws IllegalArgumentException     if the param obj.class is not assignable to Field 的 declaring class
     * @throws ExceptionInInitializerError  if the initialization provoked by this method fails
     * @throws NullPointerException         if the specified field is null
     */
    public static Object get(Field field, Object obj)
            throws IllegalArgumentException, ExceptionInInitializerError, IllegalAccessException {
        Assert.notNull(field, "参数 field 不能为空");

        if(!Modifier.isStatic(field.getModifiers()) && obj == null) {
            return null;
        }

        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            makeAccessible(field);
            return field.get(obj);
        }

    }

    /**
     * <p>封装 {@link Field#set(Object, Object)}</p>
     *
     * <p>如果该 Field 是 static，参数 obj 会被忽略</p>
     *
     * <p>If Field is instance field and the specified obj is null, do nothing return null</p>
     *
     * <p>If Field is static and final, do nothing</p>
     *
     * @param field Field
     * @param obj   实例对象
     * @param value value
     * @throws IllegalArgumentException     if the specified object is not an instance of the class or interface declaring this field
     *                                      or if, after possible "unwrapping", the new value cannot be converted to the type of this
     *                                      field by an "identity or wide conversion" (runtime)
     * @throws ExceptionInInitializerError  if the initialization provoked by this method fails
     * @throws NullPointerException         if the specified field is null
     */
    public static void set(Field field, Object obj, Object value)
            throws IllegalArgumentException, ExceptionInInitializerError, IllegalAccessException {
        Assert.notNull(field, "参数 field 不能为空");

        if(!Modifier.isStatic(field.getModifiers()) && obj == null) {
            return ;
        }
        if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
            return ;
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            makeAccessible(field);
            field.set(obj, value);
        }

    }


    /**
     * <p>查找 public、private、protected、package field，包括 static 字段。如果找不到，返回 null</p>
     *
     * <p>在整个继承结构中查找：先根深度优先(和 {@link ReflectUtils#getField(Class, String)} 查找逻辑一致)</p>
     *
     * <ol>
     *     <li>在 Class Object 中查找，如果找不到，进行第 2 步</li>
     *     <li>在 superinterface 中递归，如果找不到，进行第 3 步</li>
     *     <li>在 superclass 中递归，如果找不到，return null</li>
     * </ol>
     *
     * @param clazz Class, 不能为空
     * @param fieldName fieldName, 不能为空
     * @return Field or null if not find
     * @throws SecurityException     propagates the SecurityException of Class#getDeclaredField(...)
     * @throws NullPointerException  if the specified clazz or field is null
     */
    public static Field findDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");
        Assert.notNull(fieldName, "fieldName 不能为空");

        Field field = findDeclaredFieldHelp(clazz, fieldName);
        if(field != null) makeAccessible(field);
        return field;
    }

    private static Field findDeclaredFieldHelp(Class<?> clazz, String fieldName)
            throws SecurityException, NullPointerException {
        if(clazz == null) return null;

        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            //do nothing
        }

        for(Class<?> inter : clazz.getInterfaces()) {
            Field result = findDeclaredFieldHelp(inter, fieldName);
            if(result != null) return result;
        }

        return findDeclaredFieldHelp(clazz.getSuperclass(), fieldName);

    }

    /**
     * <p>查找 public、private、protected、package field，包括 static 字段。如果找不到，返回 null</p>
     *
     * <p>在整个继承结构中查找：先根深度优先(和 {@link ReflectUtils#getField(Class, String)} 查找逻辑一致)</p>
     *
     * <ol>
     *     <li>在 Class Object 中查找，如果找不到，进行第 2 步</li>
     *     <li>在 superinterface 中递归，如果找不到，进行第 3 步</li>
     *     <li>在 superclass 中递归，如果找不到，return null</li>
     * </ol>
     *
     * @param clazz Class, 不能为空
     * @param fieldName fieldName, 不能为空
     * @param fieldType fieldType
     * @return Field or null if not find
     * @throws SecurityException     propagates the SecurityException of Class#getDeclaredField(...)
     * @throws NullPointerException  if the specified clazz or field is null
     */
    public static Field findDeclaredField(Class<?> clazz, String fieldName, Class<?> fieldType) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");
        Assert.notNull(fieldName, "参数 fieldName 不能为空");

        Field result = findDeclaredFieldPredicate(clazz, field -> field.getName().equals(fieldName) && (fieldType == null || fieldType == field.getType()));
        if(result != null) makeAccessible(result);
        return result;
    }

    /**
     * <p>查找 public、private、protected、package field，包括 static 字段。如果找不到，返回 null</p>
     *
     * <p>在整个继承结构中查找：先根深度优先(和 {@link ReflectUtils#getField(Class, String)} 查找逻辑一致)</p>
     *
     * <ol>
     *     <li>在 Class Object 中查找，如果找不到，进行第 2 步</li>
     *     <li>在 superinterface 中递归，如果找不到，进行第 3 步</li>
     *     <li>在 superclass 中递归，如果找不到，return null</li>
     * </ol>
     *
     * @param clazz Class, 不能为空
     * @param predicate Predicate，不能为空
     * @return Field or null if not find
     * @throws SecurityException     propagates the SecurityException of Class#getDeclaredField(...)
     * @throws NullPointerException if the specified clazz or predicate is null
     */
    public static Field findDeclaredFieldPredicate(Class<?> clazz, Predicate<Field> predicate) throws SecurityException {
        Field[] fields = clazz.getDeclaredFields();
        Optional<Field> found = Arrays.stream(fields).filter(predicate).findFirst();
        if(found.isPresent()) return found.get();

        for(Class<?> inter : clazz.getInterfaces()) {
            Field result = findDeclaredFieldPredicate(inter, predicate);
            if(result != null) return result;
        }

        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null) {
            Field result = findDeclaredFieldPredicate(superClass, predicate);
            if(result != null) return result;
        }

        return null;
    }

    /**
     * <p>查找 public、private、protected、package field，包括 static 字段</p>
     *
     * <p>在整个继承结构中查找：先根深度优先(和 {@link ReflectUtils#getField(Class, String)} 查找逻辑一致)</p>
     *
     * <ol>
     *     <li>在 Class Object 中查找所有 declared field，应用 Consumer</li>
     *     <li>在 superinterface 中递归</li>
     *     <li>在 superclass 中递归</li>
     * </ol>
     *
     * @param clazz Class, 不能为空
     * @return Field 数组
     * @throws SecurityException     propagates the SecurityException of Class#getDeclaredFields(...)
     * @throws NullPointerException  if the specified clazz is null
     */
    public static Field[] findDeclaredFields(Class<?> clazz) throws SecurityException {
        Assert.notNull(clazz, "参数 clazz 不能为空");

        List<Field> result = new ArrayList<>();
        findDeclaredFieldConsumer(clazz, result::add);
        return result.toArray(new Field[0]);
    }

    /**
     * <p>查找 public、private、protected、package field，包括 static 字段</p>
     *
     * <p>在整个继承结构中查找：先根深度优先(和 {@link ReflectUtils#getField(Class, String)} 查找逻辑一致)</p>
     *
     * <ol>
     *     <li>在 Class Object 中查找所有 declared field，应用 Consumer</li>
     *     <li>在 superinterface 中递归</li>
     *     <li>在 superclass 中递归</li>
     * </ol>
     *
     * @param clazz Class, 不能为空
     * @param consumer Consumer，不能为空
     * @throws SecurityException     propagates the SecurityException of Class#getDeclaredFields(...)
     * @throws NullPointerException  if the specified clazz or consumer is null
     */
    public static void findDeclaredFieldConsumer(Class<?> clazz, Consumer<Field> consumer) throws SecurityException {
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields).forEach(consumer);

        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> inter : interfaces) {
            findDeclaredFieldConsumer(inter, consumer);
        }

        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null) {
            findDeclaredFieldConsumer(superClass, consumer);
        }

    }

    /**
     * <p>find declared field semantics: Class Object 及其继承结构形成了一颗树，对该树进行的先根深度优先搜索，搜索 Class 的 declared field</p>
     *
     * <p>根据 FieldRecursiveProcessor 的不同实现，可以做到 find first match, collect all, collect all but exclude some, and crash 等功能</p>
     *
     * <p>crash 功能: 如果当前节点 crash 返回 true, 则当前节点及其之上的继承结构被忽略，既可用在 find first 逻辑中，也可用在 collect all 逻辑中</p>
     *
     * @param clazz Class, 不能为空
     * @param recursiveProcessor FieldRecursiveProcessor, 不能为空
     * @param depth 递归深度，0 表示第一层
     * @return Method or null
     * @throws SecurityException propagates Class#getDeclaredFields的SecurityException
     */
    public static Field findDeclaredFieldSemantics(Class<?> clazz, FieldRecursiveProcessor recursiveProcessor, int depth) throws SecurityException {
        if(recursiveProcessor.crash(clazz, depth)) return null;

        recursiveProcessor.checkSamePackage(clazz, depth);

        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            Field result = recursiveProcessor.handle(field, depth);
            if(result != null) return result;

            if(recursiveProcessor.breaking(clazz, fields, field)) break;
        }
        if(recursiveProcessor.afterCrash(clazz, depth, fields)) return null;

        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> inter : interfaces) {
            Field result = findDeclaredFieldSemantics(inter, recursiveProcessor, depth + 1);
            if(result != null) return result;
        }

        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null) {
            Field result = findDeclaredFieldSemantics(superClass, recursiveProcessor, depth + 1);
            if(result != null) return result;
        }

        return null;
    }


    public interface FieldRecursiveProcessor {
        Field handle(Field field, int depth);
        default List<Field> results() { return new ArrayList<>(); }
        default boolean crash(Class<?> clazz, int depth) {return false;}

        default boolean breaking(Class<?> clazz, Field[] fields, Field field) {return false;}
        default boolean afterCrash(Class<?> clazz, int depth, Field[] fields) {return false;}

        default void checkSamePackage(Class<?> clazz, int depth) {}
    }

    public abstract static class AbstractFieldRecursiveProcessor implements FieldRecursiveProcessor {
        private final Predicate<Field> predicate;
        private boolean breaking = false;
        private boolean afterCrashing = false;
        protected boolean samePackage = true;
        private String prevPackageName = null;

        public AbstractFieldRecursiveProcessor(Predicate<Field> predicate) {
            Assert.notNull(predicate, "参数 predicate 不能为空");
            this.predicate = predicate;
        }

        @Override
        public void checkSamePackage(Class<?> clazz, int depth) {
            if(!clazz.isInterface() && samePackage) {
                if(prevPackageName == null) {  //if(depth == 0) {
                    prevPackageName = clazz.getPackage().getName();
                } else if(!clazz.getPackage().getName().equals(prevPackageName)) {
                    samePackage = false;
                    //prevPackageName = "";
                } else {
                    prevPackageName = clazz.getPackage().getName();
                }
            }
        }

        @Override
        public Field handle(Field field, int depth) {
            boolean checkedResult = check(field, depth);

            return handlerInternal(field, depth, checkedResult);
        }

        protected boolean check(Field field, int depth) {
            return predicate.test(field);
        }

        protected abstract Field handlerInternal(Field field, int depth, boolean checkedResult);

        @Override
        public boolean breaking(Class<?> clazz, Field[] fields, Field field) {
            if(breaking) {
                breaking = false;   //reset
                return true;
            }
            return false;
        }

        protected void setBreaking(boolean breaking) {
            this.breaking = breaking;
        }

        @Override
        public boolean afterCrash(Class<?> clazz, int depth, Field[] fields) {
            if(afterCrashing /*|| (fields == null || fields.length == 0)*/) {
                afterCrashing = false;    //reset
                return true;
            }
            return false;
        }

        protected void setAfterCrashing(boolean afterCrashing) {
            this.afterCrashing = afterCrashing;
        }
    }

    public static class MatchingFirstFieldProcessor extends AbstractFieldRecursiveProcessor {

        public MatchingFirstFieldProcessor(Predicate<Field> predicate) {
            super(predicate);
        }

        @Override
        protected Field handlerInternal(Field field, int depth, boolean checkedResult) {
            return checkedResult ? field : null;
        }
    }

    @Deprecated
    public static class MatchFirstFieldProcessor implements FieldRecursiveProcessor {
        private final Predicate<Field> predicate;

        public MatchFirstFieldProcessor(Predicate<Field> predicate) {
            Assert.notNull(predicate, "参数 predicate 不能为空");
            this.predicate = predicate;
        }
        @Override
        public Field handle(Field field, int depth) {
            if(predicate.test(field)) {
                return field;
            }

            return null;
        }
    }

    public static class CollectFieldsProcessor extends AbstractFieldRecursiveProcessor {
        private final List<Field> collects = new ArrayList<>();
        private final Function<Field, Field> function;

        public CollectFieldsProcessor(Predicate<Field> predicate, Function<Field, Field> function) {
            super(predicate);
            this.function = function;
        }

        @Override
        protected Field handlerInternal(Field field, int depth, boolean checkedResult) {
            if(!checkedResult) return null;

            if(function != null) field = function.apply(field);

            if(ending(field, depth)) return field;

            collects.add(field);
            determineBreaking(field, depth);
            determineAfterCrashing(field, depth);

            return null;
        }

        protected boolean ending(Field field, int depth) {
            return false;
        }
        protected void determineBreaking(Field field, int depth) {
            //setBreaking(false);
        }
        protected void determineAfterCrashing(Field field, int depth) {
            //setAfterCrashing(false);
        }

        @Override
        public List<Field> results() {
            return collects;
        }

    }

    @Deprecated
    public static class CollectsFieldProcessor implements FieldRecursiveProcessor {
        private final List<Field> collects = new ArrayList<>();
        private Consumer<Field> consumer;
        private Predicate<Field> exclude;

        public CollectsFieldProcessor() {}
        public CollectsFieldProcessor(Consumer<Field> consumer, Predicate<Field> exclude) {
            this.consumer = consumer;
            this.exclude = exclude;
        }

        @Override
        public Field handle(Field field, int depth) {
            if(exclude != null && exclude.test(field)) return null;

            if(consumer != null) consumer.accept(field);
            collects.add(field);

            return null;
        }

        @Override
        public List<Field> results() {
            return collects;
        }

        protected Consumer<Field> getConsumer() {return this.consumer;}

        protected Predicate<Field> getExclude() { return this.exclude; }
    }

    public static class DepthCrashCollectFieldsProcessor extends CollectFieldsProcessor {
        private final int crashDepth;

        public DepthCrashCollectFieldsProcessor(Predicate<Field> predicate, Function<Field, Field> function, int crashDepth) {
            super(predicate, function);
            this.crashDepth = crashDepth;
        }
        @Override
        public boolean crash(Class<?> clazz, int depth) {
            return depth >= crashDepth;
        }

    }

    @Deprecated
    public static class DepthCrashCollectsFieldProcessor extends CollectsFieldProcessor {
        private final int crashDepth;

        public DepthCrashCollectsFieldProcessor(int crashDepth) {
            super();
            this.crashDepth = crashDepth;
        }
        public DepthCrashCollectsFieldProcessor(int crashDepth, Consumer<Field> consumer, Predicate<Field> exclude) {
            super(consumer, null);
            this.crashDepth = crashDepth;
        }

        @Override
        public boolean crash(Class<?> clazz, int depth) {
            return depth >= crashDepth;
        }
    }

    public static class FieldPriorityCollectFieldsProcessor extends CollectFieldsProcessor {

        public FieldPriorityCollectFieldsProcessor(Function<Field, Field> function, String name) {
            super(field -> field.getName().equals(name), function);
        }

        @Override
        protected boolean check(Field field, int depth) {
            if(!super.check(field, depth)) return false;

            int modifiers = field.getModifiers();
            return depth == 0 || (!Modifier.isPrivate(modifiers) && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers) || samePackage));
        }

        @Override
        protected boolean ending(Field field, int depth) {
            return depth==0;
        }

        @Override
        protected void determineBreaking(Field field, int depth) {
            setBreaking(true);
        }

        @Override
        protected void determineAfterCrashing(Field field, int depth) {
            setAfterCrashing(true);
        }
    }


    public static void findDeclaredFieldStructures(Class<?> clazz, RecursiveProcessor<Field> processor, int depth) {
        //assert clazz != null
        //assert processor != null

        if(processor.before(clazz, depth)) return;

        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            if(processor.handler(field)) break;
        }

        if(processor.after(clazz, depth)) return;

        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> inter : interfaces) {
            findDeclaredFieldStructures(inter, processor, depth + 1);

            if(processor.backtrack(clazz, depth)) return;
        }

        Class<?> superclass = clazz.getSuperclass();
        if(superclass != null) {
            findDeclaredFieldStructures(superclass, processor, depth + 1);

            processor.backtrack(clazz, depth);
        }
    }

    public interface RecursiveProcessor<T> {
        boolean before(Class<?> clazz, int depth);
        boolean handler(T t);
        boolean after(Class<?> clazz, int depth);
        boolean backtrack(Class<?> clazz, int depth);
        List<T> results();
    }

    public static abstract class AbstractRecursiveProcessor<T> implements RecursiveProcessor<T> {
        protected final List<T> results = new ArrayList<>();
        protected final Predicate<T> predicate;
        protected final Consumer<T> consumer;

        public AbstractRecursiveProcessor(Predicate<T> predicate, Consumer<T> consumer) {
            this.predicate = predicate;
            this.consumer = consumer;
        }

        @Override
        public List<T> results() {
            return results;
        }
    }

    public static class FirstMatchingRecursiveProcessor<T> extends AbstractRecursiveProcessor<T> {

        public FirstMatchingRecursiveProcessor(Predicate<T> predicate, Consumer<T> consumer) {
            super(predicate, consumer);
        }

        @Override
        public boolean before(Class<?> clazz, int depth) {
            return false;
        }

        @Override
        public boolean handler(T t) {
            if(predicate.test(t)) {
                results.add(t);
                return true;
            }
            return false;
        }

        @Override
        public boolean after(Class<?> clazz, int depth) {
            return !results.isEmpty();
        }

        @Override
        public boolean backtrack(Class<?> clazz, int depth) {
            return after(clazz, depth);
        }
    }

    public static class CollectsRecursiveProcessor<T> extends AbstractRecursiveProcessor<T> {

        public CollectsRecursiveProcessor(Predicate<T> exclude, Consumer<T> consumer) {
            super(exclude, consumer);
        }

        @Override
        public boolean before(Class<?> clazz, int depth) {
            return false;
        }

        @Override
        public boolean handler(T t) {
            if(predicate == null || !predicate.test(t)) {  //!exclude
                results.add(t);
            }
            return false;
        }

        @Override
        public boolean after(Class<?> clazz, int depth) {
            return false;
        }

        @Override
        public boolean backtrack(Class<?> clazz, int depth) {
            return false;
        }
    }


}
