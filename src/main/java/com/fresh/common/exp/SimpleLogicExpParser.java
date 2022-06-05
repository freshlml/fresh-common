package com.fresh.common.exp;

import com.fresh.common.utils.AssertUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public abstract class SimpleLogicExpParser {

    public static SimpleLogicExp parseExpression(String expression) {
        StringTokenizer tokenizer = new StringTokenizer(expression, "()&|!", true);
        SimpleLogicExp exp = tokenParser(tokenizer, expression, Context.NONE);
        return exp;
    }

    private static SimpleLogicExp tokenParser(StringTokenizer tokenizer, String expression, Context context) {
        List<SimpleLogicExp> list = new ArrayList<>();
        ParserState state = null;
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if(token.isEmpty()) continue;
            switch (token) {
                case "(":
                    SimpleLogicExp subExp = tokenParser(tokenizer, expression, Context.SUB);
                    if(context == Context.NOT) return subExp;
                    list.add(subExp);
                    break;
                case ")":
                    SimpleLogicExp merged = merge(list, state, expression);
                    //return merged;
                    if(context == Context.SUB) return merged;
                    list.clear();
                    list.add(merged);
                    state = null;
                    break;
                case "&":
                    AssertUtils.isTrue(state == null || state == ParserState.AND, () -> "invalid expression["+expression+"]: when the expression or sub-expression is using the |, here con not using &", null);
                    state = ParserState.AND;
                    break;
                case "|":
                    AssertUtils.isTrue(state == null || state == ParserState.OR, () -> "invalid expression["+expression+"]: when the expression or sub-expression is using the &, here con not using |", null);
                    state = ParserState.OR;
                    break;
                case "!":
                    SimpleLogicExp subExpNot = tokenParser(tokenizer, expression, Context.NOT);
                    SimpleLogicExp notSubExp = notPredicate -> !subExpNot.matches(notPredicate);
                    list.add(notSubExp);
                    break;
                default:
                    SimpleLogicExp exp = predicate -> predicate.test(token);
                    if(context == Context.NOT) return exp;
                    list.add(exp);
            }
        }
        return merge(list, state, expression);
    }

    private static SimpleLogicExp merge(List<SimpleLogicExp> list, ParserState state, String expression) {
        AssertUtils.isTrue(!list.isEmpty(), () -> "invalid expression["+expression+"]", null);
        if(list.size() == 1) return list.get(0);
        SimpleLogicExp exp = null;
        if(list.size() > 1) {
            SimpleLogicExp[] exps = list.toArray(new SimpleLogicExp[0]);
            if(state == ParserState.AND) {
                exp = predicate -> {
                    return Arrays.stream(exps).allMatch(flExp -> flExp.matches(predicate));
                };
            } else if(state == ParserState.OR) {
                exp = predicate -> {
                    return Arrays.stream(exps).anyMatch(flExp -> flExp.matches(predicate));
                };
            }
        }
        return exp;
    }


    private enum ParserState {
        AND, OR
    }
    private enum Context {
        NONE, NOT, SUB
    }
}
