package com.fresh.common.utils;

public abstract class StringUtils {

    /**路径分隔符*/
    private static final String PATH_SEP = "/";
    private static final String WINDOWS_PATH_SEPARATOR = "\\";
    /**包分隔符*/
    private static final String PACKAGE_SEP = ".";
    /**类文件后缀*/
    private static final String CLASS_SUFFIX = "class";


    /**
     * 将className 转化为 classpath
     * eg:
     *     com.sc.common.vo.JsonResult   ->  com/sc/common/vo/JsonResult.class
     *     com.sc.common.vo              ->  com/sc/common/vo
     * @param className 包名 或者 类的qualified name
     * @param suffix 是否添加.class后缀
     * @return classpath for ClassLoader#getResource(String)
     */
    public static String className2classpath(String className, boolean suffix) {
        if(!hasLength(className)) return className;

        String classpath = className.replace(PACKAGE_SEP, PATH_SEP);
        if(suffix) classpath += PACKAGE_SEP + CLASS_SUFFIX;
        return classpath;
    }

    /**
     * 将classpath 转化为 className
     * eg:
     *      com/sc/common/vo/JsonResult.class  ->  com.sc.common.vo.JsonResult
     *      com/sc/common/vo                   ->  com.sc.common.vo
     * @param classpath classpath
     * @return className包名 或者 类的qualified name
     */
    public static String classpath2ClassName(String classpath) {
        if(!hasLength(classpath)) return classpath;

        while(classpath.startsWith("/")) {
            classpath = classpath.substring(1);
        }
        if(classpath.endsWith(PACKAGE_SEP + CLASS_SUFFIX)) {
            classpath = classpath.substring(0, classpath.length()-(PACKAGE_SEP + CLASS_SUFFIX).length());
        }
        return classpath.replace(PATH_SEP, PACKAGE_SEP);
    }

    /*
     * 是否是16进制
     */
    public static boolean isHexNumber(String str) {
        if(str == null) return false;
        int index = (str.startsWith("-") ? 1 : 0);
        return (str.startsWith("0x", index) || str.startsWith("0X", index) || str.startsWith("#", index));
    }


    /**
     * @param str string
     * @return true of false
     */
    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

    /*
     * 去除所有whitespace
     */
    public static String trimAllWhitespace(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /*
     * Check whether the given object (possibly a {@code String}) is empty.
     * <p>This method accepts any Object as an argument, comparing it to
     * {@code null} and the empty String. As a consequence, this method
     * will never return {@code true} for a non-null non-String object.
     * <p>The Object signature is useful for general attribute handling code
     * that commonly deals with Strings but generally has to iterate over
     * Objects since attributes may e.g. be primitive value objects as well.
     */
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }
}
