package com.fresh.core.utils;

public final class StringUtils {

    private StringUtils() {}

    /**路径分隔符*/
    private static final String PATH_SEP = "/";
    private static final String WINDOWS_PATH_SEPARATOR = "\\";
    /**包分隔符*/
    private static final String PACKAGE_SEP = ".";
    /**类文件后缀*/
    private static final String CLASS_SUFFIX = "class";

    /**
     * 将 binary name（top level type） 转化为 classpath
     * <ul>
     *     <li>
     *         top level type: packageName.typeName  ->  packageName/typeName
     *     </li>
     *     <li>
     *         member type: enclosing type + '$' + typeName
     *     </li>
     *     <li>
     *         local class: enclosing type + '$' + non-empty sequence of digits + typeName
     *     </li>
     *     <li>
     *         anonymous class: enclosing type + '$' + non-empty sequence of digits
     *     </li>
     *     <li>
     *         Type Variable: enclosing type + '$' + typeName
     *     </li>
     *     <li>...</li>
     * </ul>
     *
     * @param classname binary name
     * @param suffix true-添加 ".class" 后缀
     * @return classpath for ClassLoader#getResource(String)
     */
    public static String className2classpath(String classname, boolean suffix) {
        if(!hasLength(classname)) return classname;

        String classpath = classname.replace(PACKAGE_SEP, PATH_SEP);
        if(suffix)
            classpath += PACKAGE_SEP + CLASS_SUFFIX;
        return classpath;
    }

    /**
     * 将 classpath 转化为 className
     * eg:
     *      com/sc/common/vo/JsonResult.class  ->  com.sc.common.vo.JsonResult
     *      com/sc/common/vo                   ->  com.sc.common.vo
     * @param classpath classpath
     * @return className
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
     * 是否是 16 进制
     */
    public static boolean isHexNumber(String str) {
        if(str == null) return false;
        int index = (str.startsWith("-") || str.startsWith("+") ? 1 : 0);
        return (str.startsWith("0x", index) || str.startsWith("0X", index) || str.startsWith("#", index));
    }


    /**
     * @param str string
     * @return true of false
     */
    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * <p>Remove all whitespace according to Character#isWhitespace(char).</p>
     *
     * <p>无需处理高位代理和低位代理，因为码点值大于 65535 的 unicode 字符不存在 whitespace.</p>
     *
     * @param str the specified string
     * @return string with all whitespace removed
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
    
}
