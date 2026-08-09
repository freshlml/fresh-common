package com.fresh.core.utils;

import com.fresh.core.component.*;
import com.fresh.core.component.clazz.ClazzComponentResolver;
import com.fresh.core.component.clazz.ClazzComposite;
import com.fresh.core.component.clazz.ClazzLeaf;
import com.fresh.core.component.clazz.DefaultClazzComponentResolver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.*;

@Slf4j
public final class ClazzUtils {

    private ClazzUtils() {}

    /** 数组后缀 */
    private static final String ARRAYS_SUFFIX = "[]";
    /** 非 primitive 的 array class 的前缀 */
    private static final String NON_PRIMITIVE_ARRAYS_PREFIX = "[L";
    /** 非 primitive 的 array class 的 name 的后缀 */
    private static final String NON_PRIMITIVE_ARRAYS_SUFFIX = ";";
    /** binary name 分隔符 */
    private static final String INNER_CLASS_SEP = "$";
    /** path 分隔符 */
    private static final String PATH_SEP = "/";
    /** package 分隔符 */
    private static final String PACKAGE_SEP = ".";

    /**
     * primitive keyword - Class 缓存
     * eg: boolean->boolean.class;
     *     int->int.class; byte->byte.class; short->short.class; long->long.class
     *     char->char.class;
     *     double->double.class; float->float.class
     *     void->void.class
     */
    private static final Map<String, Class<?>> primitiveTypeCache = new HashMap<>(32);
    /**
     * primitive Class 与包装类型缓存
     * eg: boolean.class->Boolean.class
     *     int.class->Integer.class; byte.class->Byte.class; short.class->Short.class; long.class->Long.class
     *     char.class->Character.class;
     *     double.class->Double.class; float.class->Float.class
     *     void.class->Void.class
     */
    private static final Map<Class<?>, Class<?>> primitive2WrapCache = new HashMap<>(32);
    /**
     * 包装类型与 primitive Class 缓存
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
     * thread context ClassLoader; load ClazzUtils's ClassLoader; System ClassLoader
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
     * Set the specified classLoader, if not null, to current Thread Object.
     *
     * @param classLoader ClassLoader, may null
     * @return the old thread
     */
    public static ClassLoader resetThreadContextClassLoader(ClassLoader classLoader) {
        Thread currentThread = Thread.currentThread();
        ClassLoader oldClassLoader = currentThread.getContextClassLoader();

        if(classLoader != null && !classLoader.equals(oldClassLoader)) {
            currentThread.setContextClassLoader(classLoader);
        }
        return oldClassLoader;
    }

    /**
     * <p>enhance Class.forName(...). 新增支持 primitive keyword 和新的数组写法，如 int[]，java.lang.Integer[][]，com.sc.common.vo.JsonResult[]。</p>
     *
     * <ul>
     *     <li>declared class, enum, interface, annotation: {@link Class#forName(String, boolean, ClassLoader)}</li>
     *     <li>array: [Z, [B, [C, [S, [I, [J, [F, [D, [LgetName();: {@link Class#forName(String, boolean, ClassLoader)}</li>
     *     <li>array 新增：int[]，java.lang.Integer[][]，com.sc.common.vo.JsonResult[]，...</li>
     *     <li>primitive keyword: int，long，...</li>
     * </ul>
     *
     * @see Class#forName(String, boolean, ClassLoader)
     * @param name name
     * @param initialize initialize
     * @param classLoader ClassLoader
     * @return Class or null if can not find
     * @throws NullPointerException if the specified clazz is null
     * @exception ClassNotFoundException if the class cannot be located by the specified class loader
     */
    public static Class<?> forName(String name, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        Assert.notNull(name, "参数 className 不能为 null");

        if(isPrimitiveKeyword(name)) {
            return resolvePrimitive(name);
        }
        if(name.endsWith(ARRAYS_SUFFIX)) {
            String qualifiedComponentClass = name.substring(0, name.length() - ARRAYS_SUFFIX.length());
            Class<?> componentClass = forName(qualifiedComponentClass, initialize, classLoader);
            return Array.newInstance(componentClass, 0).getClass();
        }

        ClassLoader classLoaderLocal = Optional.ofNullable(classLoader).orElse(getDefaultClassLoader());
        return Class.forName(name, initialize, classLoaderLocal);
    }

    private static boolean isPrimitiveKeyword(String className) {
        return primitiveTypeCache.containsKey(className);
    }
    private static Class<?> resolvePrimitive(String className) {
        return primitiveTypeCache.get(className);
    }

    /**
     * <p>enhance Class.isAssignableFrom(...). 新增 boxing conversion, unboxing conversion</p>
     *
     * @see Class#isAssignableFrom(Class)
     * @param left left
     * @param right right
     * @return whether right is assignable to the left
     * @throws NullPointerException if the specified left or right is null
     */
    public static boolean isAssignableFrom(Class<?> left, Class<?> right) {
        Assert.notNull(left, "参数 left 不能为 null");
        Assert.notNull(right, "参数 right 不能为 null");

        if(left.isAssignableFrom(right)) {
            return true;
        }

        /*if(left.isArray() && right.isArray()) {
            return isAssignableFrom(left.getComponentType(), right.getComponentType());
        }*/

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
     * 如果 clazz 是一个 primitive，返回 ClazzLeaf
     * 如果 clazz 是一个 array，返回 ClazzComposite, 拥有三个成员(Object，Cloneable，Serializable)
     * 如果 clazz is null, 返回 null
     *
     * @see Component
     * @see Class#getSuperclass()
     * @see Class#getInterfaces()
     * @param clazz class
     * @return 树
     */
    public static Component<Class<?>> clazzTree(Class<?> clazz) {
        if(clazz == null) return null;

        Class<?> superClazz = clazz.getSuperclass();
        Class<?>[] superInterfaces = clazz.getInterfaces();

        if(superClazz == null && superInterfaces.length == 0)
            return new ClazzLeaf(clazz);

        Composite<Class<?>> result = new ClazzComposite(clazz);

        Component<Class<?>> superClazzComponent = clazzTree(superClazz);
        if(superClazzComponent != null)
            result.addChild(superClazzComponent);

        for(Class<?> superInterface : superInterfaces) {
            Component<Class<?>> superInterfaceComponent = clazzTree(superInterface);
            result.addChild(superInterfaceComponent);
        }

        return result;
    }


    /**
     * 获取 clazz 的所有基类
     * 如果 clazz 是一个 declared class, enum，返回其所有基类
     * 如果 clazz 是一个 interface, annotation, primitive, 返回 empty list
     * 如果 clazz 是一个 array, 返回 List[Object.class]
     * 如果 clazz is null, 返回 empty list
     * @param clazz class
     * @return clazz 的所有基类
     */
    public static List<Class<?>> getAllSuperClass(Class<?> clazz) {
        Component<Class<?>> component = clazzTree(clazz);
        return getAllSuperClass(component);
    }

    /**
     * 获取 clazz 的所有基类
     * @param component empty list if component is null
     * @return clazz 的所有基类
     */
    public static List<Class<?>> getAllSuperClass(Component<Class<?>> component) {
        if(component == null) return new ArrayList<>();
        ClazzComponentResolver componentResolver = new DefaultClazzComponentResolver(component);
        return componentResolver.getAllSuperClass();
    }

    /**
     * 获取 clazz 的所有基接口
     * 如果 clazz 是一个 declared class, interface, annotation 返回其所有基接口
     * 如果 clazz 是一个 enum, primitive 返回 empty list
     * 如果 clazz 是一个 array 返回 List[Cloneable.class,Serializable.class]
     * @param clazz class
     * @return clazz 的所有基接口
     */
    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        Component<Class<?>> component = clazzTree(clazz);
        return getAllInterfaces(component);
    }

    /**
     * 获取 clazz 的所有基接口
     * @param component empty list if component is null
     * @return clazz 的所有基类
     */
    public static List<Class<?>> getAllInterfaces(Component<Class<?>> component) {
        if(component == null) return new ArrayList<>();
        ClazzComponentResolver componentResolver = new DefaultClazzComponentResolver(component);
        return componentResolver.getAllInterfaces();
    }

}
