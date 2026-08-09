package com.fresh.core.utils;

/**
 * Assertion utility.
 */
public final class Assert {

    private Assert() {}

    /**
     * Assert a boolean expression is true, throwing an {@code IllegalStateException}
     * if the expression evaluates to {@code false}.
     *
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws IllegalStateException if {@code expression} is {@code false}
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Assert a boolean expression is true, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     *
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if {@code expression} is {@code false}
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is not {@code null}, throwing an {@code NullPointerException}
     * if the object is {@code null}.
     *
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws NullPointerException if the object is {@code null}
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

}
