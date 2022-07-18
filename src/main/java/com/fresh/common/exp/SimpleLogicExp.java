package com.fresh.common.exp;

import java.util.function.Predicate;

/*
 * provide a simple logic expression: & | !
 * using "()" to contains a sub-expression:  (p1 &p2 & (p2-1 | p2-2)) | ! p3 | (p4 ) | !(p5 & p6)
 * eg: {expression="p1 & p2 & p3"
 *       return true only when predicate("p1")=true and  predicate("p2")=true and predicate("p3")=true
 *     }
 * if expression="p1 & p2 | p3", its meanless and were throw IllegalArgumentException
 * for this, we can using its in sub-expression,eg: {expression="(p1 & p2) | p3"
 *                                           the whole expression is "(p1 & p2) | p3"
 *                                           where using "()" contains sub-expression
 *                                      }
 */
@FunctionalInterface
public interface SimpleLogicExp {
    boolean matches(Predicate<String> predicate);

    static SimpleLogicExp of(String expression) {
        return SimpleLogicExpParser.parseExpression(expression);
    }
}
