package com.fresh.core.utils;

import com.fresh.core.exception.BizException;

import java.util.function.Supplier;

/**
 * <p>assert util.</p>
 *
 * <p>assert the specified `obj` is null, otherwise throws BizException (not NullPointerException).</p>
 *
 * <p>assert the specified expression is true, otherwise throws BizException (not other common exception).</p>
 */
public final class AssertUtils {

    private AssertUtils() {}

    /**
     * 断定 expression 为 true, 如果 expression 不为 true, throw BizException
     * @param expression expression
     * @param message 异常信息
     */
    public static void isTrue(boolean expression, String message) {
        isTrue(expression, () -> message, null);
    }

    /**
     * 断定 expression 为 true, 如果 expression 不为 true, throw BizException
     * @param expression expression
     * @param message 异常信息
     * @param exceptionCode 异常 code
     */
    public static void isTrue(boolean expression,
                              Supplier<String> message,
                              Supplier<String> exceptionCode) {
        if (!expression) {
            throwsBizExp(message, exceptionCode);
        }
    }

    /**
     * 断定 obj 不为 null，如果 obj == null, throw BizException
     * @param obj obj
     * @param message 异常信息
     */
    public static void notNull(Object obj, String message) {
        notNull(obj, () -> message, null);
    }

    /**
     * 断定 obj 不为 null，如果 obj == null, 抛 BizException 异常
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
     * 如果 expression 为 true, 抛 BizException 异常
     * @param expression expression
     * @param message 异常信息
     */
    public static void ifTrue(boolean expression, String message) {
        ifTrue(expression, () -> message, null);
    }

    /**
     * 如果 expression 为 true, 抛 BizException 异常
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
