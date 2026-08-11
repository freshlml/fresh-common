package com.fresh.core.exp;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class LogicParser {

    private LogicParser() {}

    /**
     * Build a simple logic expression
     *
     * <p>p1 & p2 & p3、p1 | p2 | p3、! p1</p>                 //错误示例：& b, a ! & b, a ( & b
     * <p>p1 & p2 | p3 ==> (p1 & p2) | p3</p>                 //错误示例：a ! b, ( a ) ! b
     * <p>p1 & & p2  ==> p1 & p2</p>                          //错误示例：a ( b, ) (, ), ! ), ( ), ( a ) b
     * <p>p1 | | p2  ==> p1 | p2</p>                          //错误示例：(( a & b ), ((a & b) | ((c | d)
     * <p>! ! p1  ==> ! p1 (不是双重否定表肯定)</p>
     * <p>p1 & p2 & (p3 | p4)</p>
     * <p>p1 & p2 & (p3 | (p4 & !p5))</p>
     * <p>!(a & !b & !(c | d))</p>
     * <p>((p1 & p2 & (p3 | !(p4 & !p5)))) | ! p5 | (p6 ) | !(p7 & !p8 & !(c | d))</p>
     *
     * @param expression the specified logic expression
     * @return Logic
     * @throws NullPointerException      if the specified expression is null
     * @throws IllegalArgumentException  if the specified logic expression is invalid format
     */
    public static Logic of(String expression) {
        StringTokenizer stringTokenizer = new StringTokenizer(expression, "(&|!)", true);
        Logic[] logics = new Logic[16];    //todo
        boolean[] nots = new boolean[16];  //todo
        boolean not = false;
        int idx = 0;

        while(stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken().trim();
            if(token.isEmpty()) continue;

            Logic logicAtIdx = logics[idx];
            switch (token) {
                case "(":
                    if(not) {
                        nots[idx] = true;
                        not = false;
                    }
                    idx++;
                    break;
                case ")":
                    if(not) throw new IllegalArgumentException("格式错误: \"" + expression + "\"");

                    idx--;
                    merge(logics, idx, nots);
                    break;
                case "&":
                    if(logicAtIdx == null || not) throw new IllegalArgumentException("格式错误: \"" + expression + "\"");

                    AndLogic andLogic = new AndLogic();
                    andLogic.add(logicAtIdx);
                    logics[idx] = andLogic;
                    break;
                case "|":
                    if(logicAtIdx == null || not) throw new IllegalArgumentException("格式错误: \"" + expression + "\"");

                    OrLogic orLogic = new OrLogic();
                    orLogic.add(logicAtIdx);
                    logics[idx] = orLogic;
                    break;
                case "!":
                    not = true;
                    break;
                default:
                    Logic LiteralLogic = new LiteralLogic(token);
                    Logic literalOrNotLogic;
                    if(not) {
                        literalOrNotLogic = new NotLogic(LiteralLogic);
                        not = false;
                    } else {
                        literalOrNotLogic = LiteralLogic;
                    }

                    if(logicAtIdx == null) {
                        logics[idx] = literalOrNotLogic;
                    } else if(logicAtIdx instanceof BinaryLogic) {
                        ((BinaryLogic) logicAtIdx).add(literalOrNotLogic);
                    } else {
                        throw new IllegalArgumentException("格式错误: \"" + expression + "\"");
                    }
            }
        }
        
        List<Logic> rs = Arrays.stream(logics).filter(Objects::nonNull).collect(Collectors.toList());
        if(rs.size() != 1)
            throw new IllegalArgumentException("格式错误: \"" + expression + "\"");
        return rs.get(0);
    }

    private static void merge(Logic[] logics, int idx, boolean[] nots) {
        if(idx < 0) throw new IllegalArgumentException("格式错误");

        int i = idx + 1;
        Logic per = logics[i];

        if(per != null) {
            if (nots[idx])
                per = new NotLogic(per);

            Logic logicAtIdx = logics[idx];
            if (logicAtIdx == null) {
                logics[idx] = per;
            } else if (logicAtIdx instanceof BinaryLogic) {
                ((BinaryLogic) logicAtIdx).add(per);
            } else {
                throw new IllegalArgumentException("格式错误");
            }
        }

        logics[i] = null;
        nots[idx] = false;
    }

    public static void main(String[] argv) {
        String expression = "((a & b) | ((c | d)";
        Logic logic = of(expression);

        Predicate<String> predicate = str -> true;
        System.out.println(logic.apply(predicate));
    }

    @FunctionalInterface
    public interface Logic {
        boolean apply(Predicate<String> predicate);
    }

    static class LiteralLogic implements Logic {
        private final String literal;

        public LiteralLogic(String literal) {
            this.literal = literal;
        }

        @Override
        public boolean apply(Predicate<String> predicate) {
            return predicate.test(literal);
        }
    }

    interface BinaryLogic extends Logic {
        void add(Logic logic);
    }

    static class AndLogic implements BinaryLogic {
        private final List<Logic> exps;

        public AndLogic() {
            this.exps = new ArrayList<>();
        }

        @Override
        public void add(Logic logic) {
            //assert logic != null
            this.exps.add(logic);
        }

        @Override
        public boolean apply(Predicate<String> predicate) {
            for(Logic exp : exps) {
                if(!exp.apply(predicate)) return false;
            }
            return true;
        }
    }

    static class OrLogic implements BinaryLogic {
        private final List<Logic> exps;

        public OrLogic() {
            this.exps = new ArrayList<>();
        }

        @Override
        public void add(Logic logic) {
            //assert logic != null
            this.exps.add(logic);
        }

        @Override
        public boolean apply(Predicate<String> predicate) {
            for(Logic exp : exps) {
                if(exp.apply(predicate)) return true;
            }
            return false;
        }
    }

    static class NotLogic implements Logic {
        private final Logic logic;

        public NotLogic(Logic logic) {
            //assert logic != null
            this.logic = logic;
        }

        @Override
        public boolean apply(Predicate<String> predicate) {
            return !logic.apply(predicate);
        }
    }

}
