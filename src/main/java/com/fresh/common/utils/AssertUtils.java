package com.fresh.common.utils;

import com.fresh.common.exception.BizException;

import java.util.function.Supplier;

public abstract class AssertUtils {

    /**
     * 断定expression为true,如果expression不为true throw BizException
     * @param expression expression
     * @param message 异常信息
     */
    public static void isTrue(boolean expression, String message) {
        isTrue(expression, () -> message, null);
    }

    /**
     * 断定expression为true,如果expression不为true throw BizException
     * @param expression expression
     * @param message 异常信息
     * @param exceptionCode 异常code
     */
    public static void isTrue(boolean expression,
                              Supplier<String> message,
                              Supplier<String> exceptionCode) {
        if (!expression) {
            throwsBizExp(message, exceptionCode);
        }
    }

    /**
     * 断定obj不为null，如果obj==null,throw BizException
     * @param obj obj
     * @param message 异常信息
     */
    public static void notNull(Object obj, String message) {
        notNull(obj, () -> message, null);
    }

    /**
     * 断定obj不为null，如果obj==null,抛BizException异常
     * @param obj obj
     * @param message 异常信息
     * @param exceptionCode 异常码
     */
    public static void notNull(Object obj,
                              Supplier<String> message,
                              Supplier<String> exceptionCode) {
        if(obj == null) throwsBizExp(message, exceptionCode);
    }

    /**
     * 如果expression为true, 抛BizException异常
     * @param expression expression
     * @param message 异常信息
     */
    public static void ifTrue(boolean expression, String message) {
        ifTrue(expression, () -> message, null);
    }

    /**
     * 如果expression为true, 抛BizException异常
     * @param expression expression
     * @param message 异常信息
     * @param exceptionCode 异常码
     */
    public static void ifTrue(boolean expression,
                              Supplier<String> message,
                              Supplier<String> exceptionCode) {
        isTrue(!expression, message, exceptionCode);
    }

    private static Supplier<String> nullSafeGet(Supplier<String> supplier) {
        return supplier != null ? supplier : () -> "";
    }

    private static void throwsBizExp(Supplier<String> message, Supplier<String> exceptionCode) {
        throw new BizException(nullSafeGet(message), nullSafeGet(exceptionCode));
    }


}
