package com.fresh.common.sqlsyntax;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

public class SqlParserUtil {

    //去除 sql 前置后置空格，换行，制表符和多余的 () 对. 必要时追加 ";"
    public static String redundant(String sql, String key) {
        int pos = StringUtils.indexOfIgnoreCase(sql, key);
        int parentheses = 0;
        for(int i=0; i<pos; i++) {
            if(sql.charAt(i) == '(') {
                parentheses++;
            }
        }

        int endPos = sql.length() - 1;
        for(; endPos>=0; endPos--) {
            char c = sql.charAt(endPos);
            if(parentheses <= 0 && !(c == '\n' || c == '\r' || c == ' ' || c == '\t' || c == '\f' || c == ';')) {
                break;
            }
            if(c == ')') parentheses--;
        }

        sql = sql.substring(pos, endPos+1) + SqlSyntaxConstant.SQL_TERMINAL;  //IndexOutOfBoundsException if pos == -1

        return sql;
    }

    static boolean keywordLeftBound(String str, int idx) {
        char c;
        return (idx<=0 || ((c = str.charAt(idx-1)) == '\'' || c == '\"' || c == '`' || c == ')' || c == '*' ||
                c == '\n' || c == '\r' || c == ' ' || c == '\t' || c == '\f'));
    }

    static boolean keywordRightBound(String str, String keyword, int idx) {
        char c;
        return ((idx + keyword.length()) >= str.length() || ((c = str.charAt(idx + keyword.length())) == '`' || c == '(' ||
                c == '\n' || c == '\r' || c == ' ' || c == '\t' || c == '\f'));
    }

    static boolean keywordBound(String str, String keyword, int idx) {
        return keywordLeftBound(str, idx) && keywordRightBound(str, keyword, idx);
    }

    public static int findRelative(String sql, int prefixStart, String prefix, String... suffixs) {
        int pos = prefixStart;
        for(String suffix : suffixs) {
            int suffixIdx;

            while((suffixIdx = StringUtils.indexOfIgnoreCase(sql, suffix, pos)) != -1) {
                if(SqlSyntaxConstant.SQL_TERMINAL.equals(suffix)) break;

                if(isCharLiteral(sql, prefixStart, suffixIdx) || !keywordBound(sql, suffix, suffixIdx)) {
                    pos = suffixIdx + suffix.length();
                    continue;
                }
                if(isNested(sql, prefixStart, suffixIdx)) {
                    pos = findRelativeParentheses(sql, suffixIdx);
                    if(pos == -1) throw new IllFormedSqlException("not matched (");
                    continue;
                }

                break;
            }

            if(suffixIdx != -1) return suffixIdx;
        }
        return -1;
    }

    public static int findRelative(String sql, int prefixStart, String prefix, String suffix) {
        int pos = prefixStart + prefix.length();
        int predict = 1;
        while(pos < sql.length()) {
            int prefixIdx = StringUtils.indexOfIgnoreCase(sql, prefix, pos);
            int suffixIdx = StringUtils.indexOfIgnoreCase(sql, suffix, pos);

            if(suffixIdx == -1) {
                return -1;
            } else {
                if(prefixIdx == -1 || prefixIdx > suffixIdx) {
                    if(predict == 1) return suffixIdx;
                    else {
                        predict--;
                        pos = suffixIdx + suffix.length();
                    }
                } else {
                    predict++;
                    pos = prefixIdx + prefix.length();
                }
            }
        }
        return -1;
    }

    public static boolean isCharLiteral(String str, int begin, int end) {
        char surroundChar = '\u0000';

        for(int i = begin; i < end; i++) {
            char c = str.charAt(i);

            if(surroundChar == '\'' && c == '\'') {
                surroundChar = '\u0000';
            } else if(surroundChar == '\"' && c == '\"') {
                surroundChar = '\u0000';
            } else if(surroundChar == '`' && c == '`') {
                surroundChar = '\u0000';
            } else if (surroundChar == '\u0000' && (c == '\'' || c == '\"' || c == '`')) {
                surroundChar = c;
            }
            /*if(surroundChar != '\u0000') {
                if(surroundChar == '\'' && c == '\'') {
                    surroundChar = '\u0000';
                }
                if(surroundChar == '\"' && c == '\"') {
                    surroundChar = '\u0000';
                }
                if(surroundChar == '`' && c == '`') {
                    surroundChar = '\u0000';
                }
            } else {
                if (c == '\'' || c == '\"' || c == '`') {
                    surroundChar = c;
                }
            }*/
        }
        return surroundChar != '\u0000';
    }

    public static boolean isNested(String str, int begin, int end) {
        char surroundChar = '\u0000';
        int predict = 1;

        for(int i = end-1; i >= begin; i--) {
            char c = str.charAt(i);

            if (surroundChar == '\'' && c == '\'') {
                surroundChar = '\u0000';
            } else if (surroundChar == '\"' && c == '\"') {
                surroundChar = '\u0000';
            } else if(surroundChar == '`' && c == '`') {
                surroundChar = '\u0000';
            } else if (surroundChar == '\u0000' && (c == '\'' || c == '\"' || c == '`')) {
                surroundChar = c;
            }

            if(surroundChar == '\u0000') {
                if(c == '(' && predict == 1) {
                    return true;
                } else if(c == '(') {
                    predict--;
                } else if(c == ')') {
                    predict++;
                }
            }
        }
        return false;
    }

    public static int findRelativeParentheses(String str, int begin) {
        char surroundChar = '\u0000';
        int predict = 1;

        for(int i = begin; i < str.length(); i++) {
            char c = str.charAt(i);

            if(surroundChar == '\'' && c == '\'') {
                surroundChar = '\u0000';
            } else if(surroundChar == '\"' && c == '\"') {
                surroundChar = '\u0000';
            } else if(surroundChar == '`' && c == '`') {
                surroundChar = '\u0000';
            } else if (surroundChar == '\u0000' && (c == '\'' || c == '\"' || c == '`')) {
                surroundChar = c;
            }

            if(surroundChar == '\u0000'){
                if(c == ')' && predict == 1) {
                    return i;
                } else if(c == ')') {
                    predict--;
                } else if(c == '(') {
                    predict++;
                }
            }
        }
        return -1;
    }





    public static void main(String[] argv) {
        System.out.println(redundant("  (\t (\n\r\n\f sElEct * from table ) ) \r\n ;", SqlKeyword.SELECT.getValue()));

        System.out.println(findRelative("seLect seleCt frOm fRom (select * from table) ct;", 0, SqlKeyword.SELECT.getValue(), SqlKeyword.FROM.getValue()));

        System.out.println(findRelative("select 'select' as `from`, `from`, (select 'select') as no_from;", 0, SqlKeyword.SELECT.getValue(), SqlKeyword.FROM.getValue(), SqlSyntaxConstant.SQL_TERMINAL));

    }


}
