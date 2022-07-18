package com.fresh.common.utils;

import com.fresh.common.component.*;

import java.lang.reflect.Array;
import java.util.*;

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

    /*
     * thread context ClassLoader; load ClazzUtils's ClassLoader; System ClassLoader; null
     * @return
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

    /*
     * 设置Thread的ClassLoader
     * @param classLoader
     * @return thread原来的ClassLoader或者null(没有reset该Thread的ClassLoader)
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

    /*
     * enhance Class.forName(...)
     * 如果是primitive, eg: className=int
     * 如果是array, eg: className=int[]; className=java.lang.Integer[]; className=com.sc.common.vo.JsonResult[]; className=[Lcom.sc.common.vo.JsonResult;
     * 如果是declared class,enum,interface,annotation
     *    @see Class#forName(String, boolean, ClassLoader)
     * @param className
     * @param classLoader
     * @exception ClassNotFoundException if the class cannot be located
     * @return
     */
    public static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
        AssertUtils.ifNull(className, () -> "参数className不能为null", null);

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
        try {
            return Class.forName(className, true, classLoaderLocal);
        } catch (ClassNotFoundException e) {
            throw e;
        }

    }

    private static boolean isStringPrimitive(String className) {
        return primitiveTypeCache.containsKey(className);
    }
    private static Class<?> resolvePrimitive(String className) {
        return primitiveTypeCache.get(className);
    }

    /*
     * enhance Class.isAssignableFrom(...)
     * 如果参数left,right是primitive,则将之转化为其包装类型后在调用isAssignableFrom(...)
     * note: array(primitive),eg int[].class, Integer[].class is not assignable
     * @see Class#isAssignableFrom(Class)
     * @param left
     * @param right
     * @return
     */
    public static boolean isAssignableFrom(Class<?> left, Class<?> right) {
        AssertUtils.ifNull(left, () -> "参数left不能为空", null);
        AssertUtils.ifNull(right, () -> "参数right不能为空", null);

        if(left.isAssignableFrom(right)) {
            return true;
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

    /*
     * 返回此clazz所在package的资源
     * 如果clazz=null,primitive,array primitive;clazz所在的package设置为"",表示直接使用relativeResourceName指定的资源
     * 否则从clazz.getName()解析出所在的package,relativeResourceName则表示此packagePath下面的资源
     * @param clazz
     * @param relativeResourceName 资源名称
     * @return classpath，能够直接被加载
     *   @see ClassLoader#getResource(String)
     */
    public static String getClassPathOfCurrentPackage(Class<?> clazz, String relativeResourceName) {

        relativeResourceName = Optional.ofNullable(relativeResourceName).orElse(PATH_SEP);
        if(!relativeResourceName.startsWith(PATH_SEP)) relativeResourceName = PATH_SEP + relativeResourceName;

        //String path = Optional.ofNullable(clazz).map(c -> c.getName()).orElse("");
        String path = null;
        if(clazz == null) {
            path = "";
        } else if(clazz.isPrimitive()) {
            path = "";
        } else if(clazz.isArray()) {
            Class<?> componentTypeClass = clazz.getComponentType();
            if(componentTypeClass.isPrimitive()) path = "";
            else path = componentTypeClass.getName();
        } else {
            path = clazz.getName();
        }
        int idx = path.lastIndexOf(PACKAGE_SEP);
        String packagePath = null;
        if(idx != -1) {
            packagePath = path.substring(0, idx);
        } else {
            packagePath = "";
        }
        packagePath = packagePath.replace(PACKAGE_SEP, PATH_SEP);

        String pathReturn = packagePath + relativeResourceName;
        if(pathReturn.startsWith(PATH_SEP)) return pathReturn.substring(PATH_SEP.length());
        return pathReturn;
    }


    /*
     * 类或接口的 继承，实现 树形结构
     * @see Component
     * 如果clazz是一个primitive，返回ClazzLeaf，封装此primitive的Class Object
     * 如果clazz是一个array，返回ClazzComponent,封装此array的Class Object，默认返回拥有三个成员(Object，Cloneable，Serializable)
     *   @see Class#getSuperclass()
     *   @see Class#getInterfaces()
     * @param clazz null if clazz is null
     * @return 类或接口的继承，实现 树形结构
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
        result.addClazzParent(superClazzComponent);
        for(Class<?> superInterface : superInterfaces) {
            Component superInterfaceComponent = clazzTree(superInterface);
            result.addClazzParent(superInterfaceComponent);
        }

        return result;
    }

    /*
     * 获取clazz的所有基类
     * 如果clazz是一个declared class,enum，返回其所有基类
     * 如果clazz是一个interface,annotation,primitive,返回empty list
     * 如果clazz是一个array, 返回List[Object.class]
     * @param clazz empty list if clazz is null
     * @return
     */
    public static List<Class<?>> getAllSuperClass(Class<?> clazz) {
        Component<Class<?>> component = clazzTree(clazz);
        return getAllSuperClass(component);
    }

    /*
     * 获取clazz的所有基类
     * @param component empty list if component is null
     * @return
     */
    public static List<Class<?>> getAllSuperClass(Component<Class<?>> component) {
        if(component == null) return new ArrayList<>();
        ClazzComponentResolver componentResolver = new DefaultClazzComponentResolver(component);
        return componentResolver.getAllSuperClass();
    }

    /*
     * 获取clazz的所有基接口
     * 如果clazz是一个declared class,interface,annotation,返回其所有基接口
     * 如果clazz是一个enum,primitive,返回empty list
     * 如果clazz是一个array,返回List[Cloneable.class,Serializable.class]
     * @param clazz
     * @return
     */
    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        Component<Class<?>> component = clazzTree(clazz);
        return getAllInterfaces(component);
    }

    /*
     * 获取clazz的所有基接口
     * @param component empty list if component is null
     * @return
     */
    public static List<Class<?>> getAllInterfaces(Component<Class<?>> component) {
        if(component == null) return new ArrayList<>();
        ClazzComponentResolver componentResolver = new DefaultClazzComponentResolver(component);
        return componentResolver.getAllInterfaces();
    }

}
