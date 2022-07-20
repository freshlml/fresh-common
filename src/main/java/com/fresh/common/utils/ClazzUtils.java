package com.fresh.common.utils;

import com.fresh.common.component.*;
import com.fresh.common.component.clazz.ClazzComponentResolver;
import com.fresh.common.component.clazz.ClazzComposite;
import com.fresh.common.component.clazz.ClazzLeaf;
import com.fresh.common.component.clazz.DefaultClazzComponentResolver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.*;

@Slf4j
public abstract class ClazzUtils {

    /** 数组后缀 */
    private static final String ARRAYS_SUFFIX = "[]";
    /** 非primitive的array class的name的前缀 */
    private static final String NON_PRIMITIVE_ARRAYS_PREFIX = "[L";
    /** 非primitive的array class的name的后缀 */
    private static final String NON_PRIMITIVE_ARRAYS_SUFFIX = ";";
    /** 内部类分隔符 */
    private static final String INNER_CLASS_SEP = "$";
    /**path分隔符*/
    private static final String PATH_SEP = "/";
    /**package分隔符*/
    private static final String PACKAGE_SEP = ".";

    /**
     * primitive 缓存
     * eg: boolean->boolean.class;
     *     int->int.class; byte->byte.class; short->short.class; long->long.class
     *     char->char.class;
     *     double->double.class; float->float.class
     *     void->void.class
     */
    private static final Map<String, Class<?>> primitiveTypeCache = new HashMap<>(32);
    /**
     * primitive 与 包装类型 缓存
     * eg: boolean.class->Boolean.class
     *     int.class->Integer.class; byte.class->Byte.class; short.class->Short.class; long.class->Long.class
     *     char.class->Character.class;
     *     double.class->Double.class; float.class->Float.class
     *     void.class->Void.class
     */
    private static final Map<Class<?>, Class<?>> primitive2WrapCache = new HashMap<>(32);
    /**
     * 包装类型 与 primitive 缓存
     * eg: Boolean.class->boolean.class
     *     Integer.class->int.class; Byte.class; Short.class->short.class; Long.class->long.class
     *     Character.class->char.class
     *     Double.class->double.class; Float.class->float.class
     *     Void.class->void.class
     */
    private static final Map<Class<?>, Class<?>> wrap2PrimitiveCache = new HashMap<>(32);


    static {
        primitiveTypeCache.put("boolean", boolean.class);
        primitiveTypeCache.put("int", int.class);
        primitiveTypeCache.put("byte", byte.class);
        primitiveTypeCache.put("short", short.class);
        primitiveTypeCache.put("long", long.class);
        primitiveTypeCache.put("char", char.class);
        primitiveTypeCache.put("double", double.class);
        primitiveTypeCache.put("float", float.class);
        primitiveTypeCache.put("void", void.class);

        primitive2WrapCache.put(boolean.class, Boolean.class);
        primitive2WrapCache.put(int.class, Integer.class);
        primitive2WrapCache.put(byte.class, Byte.class);
        primitive2WrapCache.put(short.class, Short.class);
        primitive2WrapCache.put(long.class, Long.class);
        primitive2WrapCache.put(char.class, Character.class);
        primitive2WrapCache.put(float.class, Float.class);
        primitive2WrapCache.put(double.class, Double.class);
        primitive2WrapCache.put(void.class, Void.class);

        wrap2PrimitiveCache.put(Boolean.class, boolean.class);
        wrap2PrimitiveCache.put(Integer.class, int.class);
        wrap2PrimitiveCache.put(Byte.class, byte.class);
        wrap2PrimitiveCache.put(Short.class, short.class);
        wrap2PrimitiveCache.put(Long.class, long.class);
        wrap2PrimitiveCache.put(Character.class, char.class);
        wrap2PrimitiveCache.put(Float.class, float.class);
        wrap2PrimitiveCache.put(Double.class, double.class);
        wrap2PrimitiveCache.put(Void.class, void.class);
    }

    /**
     * thread context ClassLoader; load ClazzUtils's ClassLoader; System ClassLoader; null
     * @return ClassLoader
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // do nothing
        }
        if (cl == null) {
            cl = ClazzUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    //do nothing
                }
            }
        }
        return cl;
    }

    /**
     * 设置Thread的ClassLoader
     * @param classLoader ClassLoader
     * @return thread原来的ClassLoader或者null if not set
     */
    public static ClassLoader resetThreadContextClassLoader(ClassLoader classLoader) {
        Thread currentThread = Thread.currentThread();
        ClassLoader threadClassLoader = currentThread.getContextClassLoader();
        if(classLoader != null && !classLoader.equals(threadClassLoader)) {//by equals
            currentThread.setContextClassLoader(classLoader);
            return threadClassLoader;
        }
        return null;
    }

    /**
     * enhance Class.forName(...).
     * 如果是primitive, eg: className=int
     * 如果是array, eg: className=int[]; className=java.lang.Integer[]; className=com.sc.common.vo.JsonResult[]; className=[Lcom.sc.common.vo.JsonResult;
     * 如果是declared class,enum,interface,annotation,Class#forName
     *
     * @see Class#forName(String, boolean, ClassLoader)
     * @see ClazzUtilsTest
     * @param className className
     * @param classLoader ClassLoader
     * @exception ClassNotFoundException Class#forName的ClassNotFoundException
     * @return Class or null if can not find
     */
    public static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
        AssertUtils.notNull(className, "参数className不能为null");

        if(isStringPrimitive(className)) {
            return resolvePrimitive(className);
        }
        if(className.endsWith(ARRAYS_SUFFIX)) {
            String qualifiedComponentClass = className.substring(0, className.length() - ARRAYS_SUFFIX.length());
            Class<?> componentClass = forName(qualifiedComponentClass, classLoader);
            return Array.newInstance(componentClass, 0).getClass();
        }
        if(className.startsWith(NON_PRIMITIVE_ARRAYS_PREFIX) && className.endsWith(NON_PRIMITIVE_ARRAYS_SUFFIX)) {
            String qualifiedComponentClass = className.substring(NON_PRIMITIVE_ARRAYS_PREFIX.length(), className.length() - NON_PRIMITIVE_ARRAYS_SUFFIX.length());
            Class<?> componentClass = forName(qualifiedComponentClass, classLoader);
            return Array.newInstance(componentClass, 0).getClass();
        }

        ClassLoader classLoaderLocal = Optional.ofNullable(classLoader).orElse(getDefaultClassLoader());
        return Class.forName(className, true, classLoaderLocal);
    }

    private static boolean isStringPrimitive(String className) {
        return primitiveTypeCache.containsKey(className);
    }
    private static Class<?> resolvePrimitive(String className) {
        return primitiveTypeCache.get(className);
    }

    /**
     * enhance Class.isAssignableFrom(...).<br>
     * {@code Integer.class.isAssignableFrom(int.class)}调用返回false.
     * {@code int.class.isAssignableFrom(Integer.class)}调用返回false.
     * 使用如下方法，能够处理上述情况
     *
     * @see Class#isAssignableFrom(Class)
     * @see ClazzUtilsTest
     * @param left left
     * @param right right
     * @return whether assignable
     */
    public static boolean isAssignableFrom(Class<?> left, Class<?> right) {
        AssertUtils.notNull(left, "参数left不能为null");
        AssertUtils.notNull(right, "参数right不能为null");

        if(left.isAssignableFrom(right)) {
            return true;
        }

        if(left.isArray() && right.isArray()) {
            return isAssignableFrom(left.getComponentType(), right.getComponentType());
        }

        if(left.isPrimitive()) {
            Class<?> leftWrapper = primitive2WrapCache.get(left);
            return leftWrapper.isAssignableFrom(right);
        } else {
            Class<?> rightWrapper = primitive2WrapCache.get(right);
            if(rightWrapper == null) return false;
            return left.isAssignableFrom(rightWrapper);
        }

    }


    /**
     * 继承结构解析树
     * @see Component
     * 如果clazz是一个primitive，返回ClazzLeaf，封装此primitive的Class
     * 如果clazz是一个array，返回ClazzComponent,封装此array的Class，默认返回拥有三个成员(Object，Cloneable，Serializable)
     * 如果clazz is null, 返回null
     * @see Class#getSuperclass()
     * @see Class#getInterfaces()
     * @see ClazzUtilsTest
     * @param clazz class
     * @return 树
     */
    public static Component<Class<?>> clazzTree(Class<?> clazz) {
        if(clazz == null) return null;

        Class<?> superClazz = clazz.getSuperclass();
        Class<?>[] superInterfaces = clazz.getInterfaces();

        Composite result;
        if(superClazz==null && superInterfaces.length==0) {
            return new ClazzLeaf(clazz);
        } else {
            result = new ClazzComposite(clazz);
        }

        Component superClazzComponent = clazzTree(superClazz);
        if(superClazzComponent != null)
            result.addChild(superClazzComponent);
        for(Class<?> superInterface : superInterfaces) {
            Component superInterfaceComponent = clazzTree(superInterface);
            result.addChild(superInterfaceComponent);
        }

        return result;
    }



    /**
     * 获取clazz的所有基类
     * 如果clazz是一个declared class,enum，返回其所有基类
     * 如果clazz是一个interface,annotation,primitive,返回empty list
     * 如果clazz是一个array, 返回List[Object.class]
     * 如果clazz is null, 返回empty list
     * @param clazz class
     * @return clazz的所有基类
     */
    public static List<Class<?>> getAllSuperClass(Class<?> clazz) {
        Component<Class<?>> component = clazzTree(clazz);
        return getAllSuperClass(component);
    }

    /**
     * 获取clazz的所有基类
     * @param component empty list if component is null
     * @return clazz的所有基类
     */
    public static List<Class<?>> getAllSuperClass(Component<Class<?>> component) {
        if(component == null) return new ArrayList<>();
        ClazzComponentResolver componentResolver = new DefaultClazzComponentResolver(component);
        return componentResolver.getAllSuperClass();
    }

    /**
     * 获取clazz的所有基接口
     * 如果clazz是一个declared class,interface,annotation,返回其所有基接口
     * 如果clazz是一个enum,primitive,返回empty list
     * 如果clazz是一个array,返回List[Cloneable.class,Serializable.class]
     * @param clazz class
     * @return clazz的所有基接口
     */
    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        Component<Class<?>> component = clazzTree(clazz);
        return getAllInterfaces(component);
    }

    /**
     * 获取clazz的所有基接口
     * @param component empty list if component is null
     * @return clazz的所有基类
     */
    public static List<Class<?>> getAllInterfaces(Component<Class<?>> component) {
        if(component == null) return new ArrayList<>();
        ClazzComponentResolver componentResolver = new DefaultClazzComponentResolver(component);
        return componentResolver.getAllInterfaces();
    }

}
