package com.fresh.common.utils;

import com.fresh.common.exception.BizException;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ReflectUtils {


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Constructor
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * packing Class.getConstructor {@link Class#getConstructor(Class[])}, 获取public构造器.
     * if Class#getConstructor 触发 NoSuchMethodException return null
     * if Class#getConstructor 触发 SecurityException 原样抛出
     *
     * clazz参数不能为null，如不传paramTypes或者paramTypes=null或者传递empty Class<?>[]，表示获取无参构造器
     *
     * 如果Class Object是成员内部类，局部内部类和匿名内部类，paramTypes第一个参数是其enclosing instance
     *
     * 带泛型信息的参数传递方法:
     * 如果Class Object的构造器参数是TypeVariable,eg: class Leaf<T> { public Leaf(T t){} } ;则paramTypes=Class<?>[]{Object.class} (泛型擦除)
     *                                        eg: class Loop { public <T extends Number> Loop(T t){} } ;则paramTypes=Class<?>[]{Number.class} (泛型擦除，向上转型)
     *
     * 如果Class Object的构造器参数是GenericArrayType,eg: class Loop { public <T> Loop(T[] t){} } ;则paramTypes=Class<?>[]{Object[].class} (泛型变量数组)
     *                                             eg: class Loop { public <T> Loop(List<T>[] t){} } ;则paramTypes=Class<?>[]{List[].class} (泛型类型数组)
     *
     * 如果Class Object的构造器参数是ParameterizedType.eg: class Loop { public <T> Loop(Loop<T> lt) } ;则paramTypes=Class<?>[]{Loop.class}
     *
     * @param clazz class
     * @param paramTypes 参数
     * @return Constructor or null
     * @throws SecurityException propagates Class#getConstructor的SecurityException
     */
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... paramTypes) throws SecurityException {
        AssertUtils.notNull(clazz, "参数clazz不能为空");

        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


    /**
     * packing Class.getDeclaredConstructor {@link Class#getDeclaredConstructor(Class[])}, 查找Class Object的public protected package private constructor,
     * 并且setAccessible(true) if require
     * if Class#getDeclaredConstructor 触发 NoSuchMethodException return null
     * if Class#getDeclaredConstructor 触发 SecurityException 直接抛出
     *
     * clazz参数不能为null，如不传paramTypes或者paramTypes=null或者传递empty Class<?>[]，表示获取无参构造器
     *
     * 如果Class Object是成员内部类，局部内部类和匿名内部类，paramTypes第一个参数是其enclosing instance
     *
     * @param clazz class
     * @param paramTypes 参数
     * @return Constructor or null
     * @throws SecurityException propagates Class#getDeclaredConstructor的SecurityException
     */
    public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... paramTypes) throws SecurityException {
        AssertUtils.notNull(clazz, "参数clazz不能为空");

        try {
            Constructor<T> c = clazz.getDeclaredConstructor(paramTypes);
            makeAccessible(c);
            return c;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


    /**
     * packing Constructor#newInstance {@link Constructor#newInstance(Object...)}, 构造实例对象
     *
     * 如果此Constructor的declaring class是局部内部类，匿名内部类，成员内部类，第一参数是其declaring class的enclosing instance
     *
     * @param constructor 构造器
     * @param initargs 构造器参数
     * @param <T> 泛型参数
     * @return 实例对象
     * @throws InstantiationException       实例化失败，如构造器的declaring class is abstract等问题
     * @throws InvocationTargetException    构造器执行抛出的异常，封装成InvocationTargetException抛出
     * @throws IllegalArgumentException     参数数量不匹配，类型不匹配等参数问题
     * @throws BizException                 after setAccessible, still can not accessible
     */
    public static <T> T newInstance(Constructor<T> constructor, Object ... initargs)
            throws InstantiationException, InvocationTargetException, IllegalArgumentException, BizException {
        AssertUtils.notNull(constructor, "constructor参数不能为空");

        try {
            return constructor.newInstance(initargs);
        } catch (IllegalAccessException e) {
            makeAccessible(constructor);
            try {
                return constructor.newInstance(initargs);
            } catch (IllegalAccessException ne) {
                throw new BizException(() -> "after setAccessible, still can not accessible");
            }
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
     * packing Class.getMethod, {@link Class#getMethod}, 查找public method，任何接口中的static方法不在查找范围中
     * Class.getMethod查找逻辑: 先根深度优先
     * 1.在Class Object中查找，如果找不到，进行第2步
     * 2.在superclass中递归，如果找不到，进行第3步
     * 3.在superinterface中递归，如果找不到，throw NoSuchMethodException
     *
     * 如果Clazz是Object的子类，能够查找到Object中toString，hashCode，wait，notify，getClass；查找不到Object中equals, clone, finalize
     *
     * 如果存在相同签名的桥接方法,bridged method的返回值将more specific，返回more specific的；如果存在桥接方法但参数类型不同，根据参数类型即可区分
     *
     * if Class.getMethod 触发 NoSuchMethodException return null
     * if Class.getMethod 触发 SecurityException  原样返回
     *
     * paramTypes不传，或者paramTypes=null,表示获取无参方法
     *
     * 带泛型信息的参数传递方法:
     * 如果Class Object的参数是TypeVariable,eg: genericTT(T t),则paramTypes=Class<?>[]{Object.class}
     *
     * 如果Class Object的参数是GenericArrayType,eg genericTT(T[], List<T>[]),则paramTypes=Class<?>[]{Object[].class, List[].class}
     *
     * 如果Class Object的参数是ParameterizedType,eg generic(Loop<T>),则paramTypes=Class<?>[]{Loop.class}
     *
     *
     * @param clazz clazz，不能为空
     * @param methodName methodName，不能为空
     * @param paramTypes 参数
     * @return Method or null
     * @throws SecurityException propagate Class#getMethod的SecurityException
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        AssertUtils.notNull(clazz, "参数clazz不能为空");
        AssertUtils.notNull(methodName, "参数methodName不能为空");

        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * packing Class.getDeclaredMethod {@link Class#getDeclaredMethod(String, Class[])}, 查找public,private,protected,package method，包括static方法
     * 并且setAccessible(true) if require
     * 只在Class Object中查找
     *
     * 如果存在相同签名的桥接方法,bridged method的返回值将more specific，返回more specific的；如果存在桥接方法但参数类型不同，根据参数类型即可区分
     *
     * if Class.getDeclaredMethod 触发 NoSuchMethodException return null
     * if Class.getDeclaredMethod 触发 SecurityException 原样抛出
     *
     * 带泛型信息的参数传递方法和ReflectUtils.getMethod一致
     *
     * @param clazz Class，不能为空
     * @param methodName methodName，不能为空
     * @param paramTypes 参数，paramTypes不传，或者paramTypes=null,表示获取无参方法
     * @return Method or null
     * @throws SecurityException propagate Class#getDeclarredMethod的SecurityException
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        AssertUtils.notNull(clazz, "参数clazz不能为空");
        AssertUtils.notNull(methodName, "参数methodName不能为空");

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
     * packing Method#invoke {@link Method#invoke(Object, Object...)}, 执行方法
     *
     * 如果Method是static method, obj被忽略
     *
     * 如果Method不是static method, 满足动态代理机制
     *
     * 如果Method不是static method and param obj is null, do nothing return null
     *
     * @param method Method，不能为空
     * @param obj    Object
     * @param args   参数
     * @return 方法执行结果 or null
     * @throws InvocationTargetException  Method执行抛出的异常，封装成InvocationTargetException后抛出
     * @throws IllegalArgumentException   if the Method is an instance method and the param obj.class is not assignable to Method的declaring class or 参数不匹配
     * @throws ExceptionInInitializerError if the initialization provoked by this method fails
     */
    public static Object invoke(Method method, Object obj, Object... args)
            throws InvocationTargetException, IllegalArgumentException, ExceptionInInitializerError {
        AssertUtils.notNull(method, "参数method不能为空");

        if(!Modifier.isStatic(method.getModifiers()) && obj == null) {
            return null;
        }

        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            makeAccessible(method);
            try {
                return method.invoke(obj, args);
            } catch (IllegalAccessException ne) {
                throw new BizException(() -> "after setAccessible, still can not accessible");
            } catch (InvocationTargetException ne) {
                throw ne;
            }
        } catch (InvocationTargetException e) {
            throw e;
        }
        
    }

    /*
     * 如果param left与param right有相同的方法签名，返回true
     */
    private static boolean isSameSignature(Method left, Method right) {
        if(left == null || right == null) return false;
        if(left.getName().equals(right.getName()) && Arrays.equals(left.getParameterTypes(), right.getParameterTypes())) return true;

        return false;
    }

    /*
     * 返回param bridgeMethod itself if 它不是桥接方法 or 与该桥接方法有相同方法签名and more specific return type的bridged method or null
     */
    private static Method findBridgedMethodSignature(Method bridgeMethod) {
        AssertUtils.notNull(bridgeMethod, "参数bridgeMethod不能为空");

        if(!bridgeMethod.isBridge()) return bridgeMethod;

        Method[] methods = bridgeMethod.getDeclaringClass().getDeclaredMethods();
        for(Method method : methods) {
            if(!method.equals(bridgeMethod) &&
                isSameSignature(method, bridgeMethod) &&
                ClazzUtils.isAssignableFrom(bridgeMethod.getReturnType(), method.getReturnType())) return method;
        }

        return null;
    }

    /*
     * 返回param bridgeMethod itself if 它不是桥接方法 or bridged method or null(?)
     */
    private static Method findBridgedMethod(Method bridgeMethod) {
        AssertUtils.notNull(bridgeMethod, "参数bridgeMethod不能为空");

        if(!bridgeMethod.isBridge()) return bridgeMethod;

        Method[] methods = bridgeMethod.getDeclaringClass().getDeclaredMethods();
        for(Method method : methods) {
            if(isBridgeMethod(bridgeMethod, method)) return method;
        }

        return null;
    }

    /*
     * 判断param bridgeMethod是否是param method的桥接方法
     */
    private static boolean isBridgeMethod(Method bridgeMethod, Method method) {
        return bridgeMethod.getDeclaringClass() == method.getDeclaringClass() &&
               !bridgeMethod.equals(method) &&
               bridgeMethod.isBridge() &&
               !method.isBridge() &&
               ClazzUtils.isAssignableFrom(bridgeMethod.getReturnType(), method.getReturnType()) &&
               isParameterAssignableFrom(bridgeMethod, method);
    }

    private static boolean isParameterAssignableFrom(Method left, Method right) {
        if(left == null || right == null) return false;
        if(left.getParameterCount() != right.getParameterCount()) return false;

        Class<?>[] leftParams = left.getParameterTypes();
        Class<?>[] rightParams = right.getParameterTypes();
        for(int i=0; i < left.getParameterCount(); i++) {
            if(!ClazzUtils.isAssignableFrom(leftParams[i], rightParams[i])) return false;
        }

        return true;
    }


    /*
     * 返回method itself if method是桥接方法 or method的桥接方法 or null if method 没有桥接方法
     */
    private static Method findBridgeMethod(Method method) {
        AssertUtils.notNull(method, "参数method不能为空");

        if(method.isBridge()) return method;

        Method[] methods = method.getDeclaringClass().getDeclaredMethods();
        for(Method bridgeCandidate : methods) {
            if(isBridgeMethod(bridgeCandidate, method)) return bridgeCandidate;
        }

        return null;
    }


    /**
     * find declared method semantics: Class Object及其继承结构形成了一颗树，对该树进行的先根深度优先搜索，搜索Class的declared method
     * 根据MethodRecursiveProcessor的不同实现，可以做到例如find first match, collect all, collect all but exclude some, collect all and detect override, and crash等动能
     * crash功能: 如果当前节点crash返回true,则当前节点及其之上的继承结构被忽略，既可用在find first逻辑中，也可用在collect all逻辑中
     *
     * @param clazz Class, 不能为空
     * @param methodProcessor MethodRecursiveProcessor, 不能为空
     * @param depth 递归深度，0表示第一层
     * @return Method or null
     * @throws SecurityException propagates Class#getDeclaredFields的SecurityException
     */
    public static Method findDeclaredMethodSemantics(Class<?> clazz, MethodRecursiveProcessor methodProcessor, int depth) throws SecurityException {
        if(methodProcessor.crash(clazz, depth)) return null;

        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods) {
            Method result = methodProcessor.handler(method, depth);
            if(result != null) return result;
        }

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

    interface MethodRecursiveProcessor {
        Method handler(Method method, int depth);
        default List<Method> results() { return new ArrayList<>(); }
        default boolean crash(Class<?> clazz, int depth) {return false;}
    }

    public static class MatchFirstMethodProcessor implements MethodRecursiveProcessor {
        private final Predicate<Method> predicate;

        public MatchFirstMethodProcessor(Predicate<Method> predicate) {
            AssertUtils.notNull(predicate, "参数predicate不能为空");
            this.predicate = predicate;
        }

        /*//returnType参数用于区分相同方法签名的场景
        public static Predicate<Method> defaultMatcher(String methodName, Class<?> returnType, Class<?>... paramTypes) {
            AssertUtils.notNull(methodName, "参数methodName不能为空");

            return method -> method.getName().equals(methodName) &&
                   ((paramTypes == null && method.getParameterCount()==0) ||
                    (paramTypes != null && Arrays.equals(method.getParameterTypes(), paramTypes))) &&
                   (returnType == null || returnType == method.getReturnType());
        }*/
        public static Predicate<Method> defaultMatcher(String methodName, Class<?>... paramTypes) {
            AssertUtils.notNull(methodName, "参数methodName不能为空");

            return method -> method.getName().equals(methodName) &&
                    ((paramTypes == null && method.getParameterCount()==0) ||
                            (paramTypes != null && Arrays.equals(method.getParameterTypes(), paramTypes)));
        }

        @Override
        public Method handler(Method method, int depth) {
            if(predicate.test(method)) {
                if(!method.isBridge()) return method;
                else {
                    //找与该bridge method有相同签名 and more specific return type的bridged method
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
            return method -> method.isBridge();
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



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Field
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * packing Class.getField {@link Class#getField(String)}
     * Class.getField查找逻辑: 先根深度优先
     *  1.在Class Object中找，如果找不到，进行第2步
     *  2.在superinterface中递归，如果找不到，进行第3步
     *  3.在superclass中递归，如果找不到，throw NoSuchFieldException
     *
     * if Class.getField 触发 NoSuchFieldException return null
     * if Class.getField 触发 SecurityException 原样抛出
     *
     * @param clazz class，不能为空
     * @param fieldName fieldName, 不能为空
     * @return Field or null
     * @throws SecurityException propagates Class#getField的SecurityException
     */
    public static Field getField(Class<?> clazz, String fieldName) throws SecurityException {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.notNull(fieldName, () -> "参数fieldName不能为空", null);

        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }


    /**
     * packing Class.getDeclaredField {@link Class#getDeclaredField(String)}
     * 查找public,private,protected,package field，包括static字段，setAccessible(true) if require
     * 只在Class Object中找
     *
     * if Class.getDeclaredField触发 NoSuchFieldException return null
     * if Class.getDeclaredField触发 SecurityException 原样抛出
     *
     * @param clazz class，不能为空
     * @param fieldName fieldName，不能为空
     * @return Field or null
     * @throws SecurityException propagates Class#getField的SecurityException
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.notNull(fieldName, () -> "参数fieldName不能为空", null);

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
     * packing Field#get {@link Field#get(Object)} 获取Field的值
     * 如果Field是static，参数obj会被忽略
     *
     * @param field Field, 不能为空
     * @param obj   实例对象
     * @return Field的值 or null when Field is instance field and param obj is null
     * @throws IllegalArgumentException     if the param obj.class is not assignable to Field的declaring class
     * @throws ExceptionInInitializerError  if the initialization provoked by this method fails
     * @throws BizException                 after setAccessible, still can not accessible
     */
    public static Object get(Field field, Object obj)
            throws IllegalArgumentException, ExceptionInInitializerError, BizException {
        AssertUtils.notNull(field, "参数field不能为空");

        if(!Modifier.isStatic(field.getModifiers()) && obj == null) {
            return null;
        }

        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            makeAccessible(field);
            try {
                return field.get(obj);
            } catch (IllegalAccessException ne) {
                throw new BizException(() -> "after setAccessible, still can not accessible");
            }
        }

    }

    /**
     * packing Field#set {@link Field#set(Object, Object)}, 为Field设置值
     * 如果该Field是static，参数obj会被忽略
     *
     * 如果Field is instance field and param obj is null, do nothing
     * 如果Field is static and final, do nothing
     *
     * @param field Field
     * @param obj   实例对象
     * @param value value
     * @throws IllegalArgumentException     if Field is instance filed and param obj.class is not assignable to Filed的declaring class 或者 参数类型转化失败
     * @throws ExceptionInInitializerError  if the initialization provoked by this method fails
     * @throws BizException                 after setAccessible, still can not accessible
     */
    public static void set(Field field, Object obj, Object value)
            throws IllegalArgumentException, ExceptionInInitializerError, BizException {
        AssertUtils.notNull(field, "参数field不能为空");

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
            try {
                field.set(obj, value);
            } catch (IllegalAccessException ne) {
                throw new BizException(() -> "after setAccessible, still can not accessible");
            }
        }

    }


    /**
     * 融合Class#getField的查找逻辑和declared语义, {@link ReflectUtils#findDeclaredFieldHelp(Class, String)}
     * setAccessible if require
     *
     * @param clazz Class, 不能为空
     * @param fieldName fieldName, 不能为空
     * @return Field or null if not find
     * @throws SecurityException propagates Class#getDeclaredField的SecurityException
     */
    public static Field findDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
        AssertUtils.notNull(clazz, "参数clazz不能为空");
        AssertUtils.notNull(fieldName, "fieldName不能为空");

        Field field = findDeclaredFieldHelp(clazz, fieldName);
        if(field != null) makeAccessible(field);
        return field;
    }
    /**
     * 查找public private protected package field，包括static字段
     * 查找逻辑: 先根深度优先(和Class#getField查找逻辑一致)
     *  1.在Class Object中找，如果找不到，进行第2步
     *  2.在superinterface中递归，如果找不到，进行第3步
     *  3.在superclass中递归，如果找不到，return null
     *
     * @param clazz Class
     * @param fieldName fieldName，不应为空
     * @return Field or null if not find
     * @throws SecurityException propagates Class#getDeclaredField的SecurityException
     * @throws NullPointerException if fieldName is null
     */
    private static Field findDeclaredFieldHelp(Class<?> clazz, String fieldName)
            throws SecurityException, NullPointerException {
        if(clazz == null) return null;

        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            //continue
        }

        for(Class<?> inter : clazz.getInterfaces()) {
            Field result = findDeclaredFieldHelp(inter, fieldName);
            if(result != null) return result;
        }

        return findDeclaredFieldHelp(clazz.getSuperclass(), fieldName);

    }

    /**
     * the other realization for findDeclaredField
     * 融合Class#getField的查找逻辑和declared语义, {@link ReflectUtils#findDeclaredFieldPredicate(Class, Predicate)}
     * 查找public private protected package field，包括static字段
     * setAccessible if require
     *
     * @param clazz Class,不能为空
     * @param fieldName fieldName,不能为空
     * @param fieldType fieldType
     * @return Field or null if not found
     * @throws SecurityException propagates Class#getDeclaredField的SecurityException
     */
    public static Field findDeclaredField(Class<?> clazz, String fieldName, Class<?> fieldType) throws SecurityException {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);
        AssertUtils.notNull(fieldName, () -> "参数fieldName不能为空", null);

        Field result = findDeclaredFieldPredicate(clazz, field -> field.getName().equals(fieldName) && (fieldType == null || fieldType == field.getType()));
        if(result != null) makeAccessible(result);
        return result;
    }

    /**
     * 查找逻辑: 先根深度优先(和Class#getField查找逻辑一致)
     *  1.在Class Object中找，如果找不到，进行第2步
     *  2.在superinterface中递归，如果找不到，进行第3步
     *  3.在superclass中递归，如果找不到，return null
     *
     * @param clazz Class，不能为空
     * @param predicate Predicate，不能为空
     * @return Field or null
     * @throws SecurityException propagates Class#getDeclaredField的SecurityException
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
     * 融合Class#getField的查找逻辑和declared语义 {@link ReflectUtils#findDeclaredFieldConsumer(Class, Consumer)}
     * 查找所有public private protected package field，包括static字段
     * 返回结果的按查找路径分块有序
     *
     * @param clazz Class, 不能为空
     * @return Field数组
     * @throws SecurityException propagates Class#getDeclaredFields的SecurityException
     */
    public static Field[] findDeclaredFields(Class<?> clazz) throws SecurityException {
        AssertUtils.notNull(clazz, () -> "参数clazz不能为空", null);

        List<Field> result = new ArrayList<>();
        findDeclaredFieldConsumer(clazz, result::add);
        return result.toArray(new Field[0]);
    }

    /**
     * 查找逻辑: 先根深度优先(和Class#getField查找逻辑一致)
     *  1.在Class Object中查找所有的declared field，对每一个declared field，应用Consumer#accept
     *  2.在superinterface中递归
     *  3.在superclass中递归
     *
     * @param clazz Class，不能为空
     * @param consumer Consumer，不能为空
     * @throws SecurityException propagates Class#getDeclaredFields的SecurityException
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
     * find declared field semantics: Class Object及其继承结构形成了一颗树，对该树进行的先根深度优先搜索，搜索Class的declared field
     * 根据FieldRecursiveProcessor的不同实现，可以做到例如find first match, collect all, collect all but exclude some, and crash等功能
     * crash功能: 如果当前节点crash返回true,则当前节点及其之上的继承结构被忽略，既可用在find first逻辑中，也可用在collect all逻辑中
     *
     * @param clazz Class, 不能为空
     * @param recursiveProcessor FieldRecursiveProcessor, 不能为空
     * @param depth 递归深度，0表示第一层
     * @return Field or null
     * @throws SecurityException propagates Class#getDeclaredFields的SecurityException
     */
    public static Field findDeclaredFieldSemantics(Class<?> clazz, FieldRecursiveProcessor recursiveProcessor, int depth) throws SecurityException {
        if(recursiveProcessor.crash(clazz, depth)) return null;

        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            Field result = recursiveProcessor.handler(field, depth);
            if(result != null) return result;
        }

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


    interface FieldRecursiveProcessor {
        Field handler(Field field, int depth);
        default List<Field> results() { return new ArrayList<>(); }
        default boolean crash(Class<?> clazz, int depth) {return false;}
    }

    public static class MatchFirstFieldProcessor implements FieldRecursiveProcessor {
        private final Predicate<Field> predicate;

        public MatchFirstFieldProcessor(Predicate<Field> predicate) {
            AssertUtils.notNull(predicate, "参数predicate不能为空");
            this.predicate = predicate;
        }
        @Override
        public Field handler(Field field, int depth) {
            if(predicate.test(field)) {
                return field;
            }

            return null;
        }
    }

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
        public Field handler(Field field, int depth) {
            if(exclude != null && exclude.test(field)) return null;

            if(consumer != null) consumer.accept(field);
            collects.add(field);

            return null;
        }

        @Override
        public List<Field> results() {
            return collects;
        }

    }

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




}
