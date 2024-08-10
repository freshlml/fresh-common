package com.fresh.common.utils.sql;


import java.util.Arrays;

public abstract class SqlUtils {
    static final char LEFT_PARENTHESES = '(';
    static final char RIGHT_PARENTHESES = ')';
    static final char SQL_TERMINAL = ';';
    static final char SINGLE_QUOTE = '\'';
    static final char DOUBLE_QUOTE = '\"';
    static final char BACK_QUOTE = '`';
    static final char NUL_CHAR = '\u0000';
    static final char SQL_COMMENT = '#';
    static final char MINUS_CHAR = '-';
    static final char LEFT_SLASH = '/';
    static final char BACK_SLASH = '\\';
    static final char STAR_CHAR = '*';
    static final char LOGIC_NOT_CHAR = '!';
    static final char PLUS_CHAR = '+';
    static final char SPACE = ' ';
    static final char LF = '\n';
    static final char CR = '\r';


    private SqlUtils() {}

    //comment:
    //  1. 以 '#' 开头，后接任意数量字符，以换行或者字符串末尾结束
    //  2. 以 '--' 开头，[后接 ASCII SP 和任意数量字符]，以换行或者文本末尾结束
    //  3. 以 '/*' 开头，后接任意数量字符，以 '*/' 结束
    //string literal 和 `` 之中的 '#'、'--'、'/*' 不作为 comment 标记
    //在原 comment 的位置插入 WhiteSpace 以保证其分隔特性
    public static String truncateComments(String sql) {
        //assert sql != null;
        EncloseContext encloseContext = new EncloseContext();

        StringBuilder sb = new StringBuilder();
        int prevPos = 0;
        int pos = 0;

        while(pos < sql.length()) {
            int entered = encloseContext.handle(sql, pos);

            if(entered == -1) { //when not in enclosing context
                int prefixType;
                if((prefixType = sqlCommentPrefix(sql, pos)) != -1) {  //found a sql comment prefix

                    int commentEndIdx = -1;
                    for(int i = pos + 1; i <= sql.length(); i++) {
                        if((commentEndIdx = sqlCommentSuffix(sql, prefixType, i)) != -1) {
                            break;
                        }
                    }
                    if(commentEndIdx != -1) {   //found a matched comment suffix
                        sb.append(sql, prevPos, pos).append(SPACE);
                        pos = commentEndIdx;
                        prevPos = pos;
                    } else {
                        if(prefixType == 0) {  //single line comment
                            sb.append(sql, prevPos, pos);
                            pos = sql.length();
                            prevPos = pos;
                        } else {
                            throw new IllFormedSqlException("no matched comment suffix for sql [ " + sql + " ], near '" + sql.substring(pos) + "'");
                            //pos = sql.length();
                        }
                    }
                } else {
                    pos++;
                }
            } else {
                pos++;
            }
        }

        sb.append(sql, prevPos, pos);
        return sb.toString();
    }

    private static int sqlCommentPrefix(String str, int idx) {
        //assert str != null;
        //assert 0 <= idx < str.length();
        char c = str.charAt(idx);
        if(c == MINUS_CHAR) {
            return (idx + 1) < str.length() && str.charAt(idx + 1) == MINUS_CHAR && ((idx + 2) >= str.length() ||
                    (str.charAt(idx + 2) == SPACE || lineTerminator(str, idx + 2) != -1)) ? 0 : -1;  //return 0 for single line comment
        } else if(c == LEFT_SLASH) {
            return (idx + 1) < str.length() && str.charAt(idx + 1) == STAR_CHAR && ((idx + 2) >= str.length() ||
                    ((c=str.charAt(idx + 2)) != LOGIC_NOT_CHAR && c != PLUS_CHAR)) ? 1 : -1;  //return 1 for multi line comment
        }
        return c == SQL_COMMENT ? 0 : -1;
    }

    private static int sqlCommentSuffix(String str, int prefixType, int idx) {
        //assert str != null;
        //assert 0 <= idx;
        if(prefixType == 0) {      //single line comment
            if(idx >= str.length()) return str.length(); //单行注释可以以文本末尾结束

            return lineTerminator(str, idx);
        } else if(prefixType == 1) {  //multi line comment
            if(idx >= str.length()) return -1;  //多行注释不可以以文本末尾结束

            char c = str.charAt(idx);
            if(c == STAR_CHAR) {
                return idx + 1 < str.length() && str.charAt(idx + 1) == LEFT_SLASH ? idx + 2 : -1;  //max value of (idx + 2) is str.length
            } else if(c == LEFT_SLASH) {
                return idx > 0 && str.charAt(idx - 1) == STAR_CHAR ? idx + 1 : -1;  //max value of (idx + 1) is str.length
            }
        }
        return -1;
    }

    public static int lineTerminator(String str, int idx) {
        //assert str != null;
        //assert 0 <= idx < str.length();
        char c = str.charAt(idx);
        if(c == LF) {
            return (idx > 0 && str.charAt(idx - 1) == CR) ? idx - 1 : idx;
        }
        return c == CR ? idx : -1;
    }

    private static short[] setPos(short[] sts, int v) {
        //assert sts != null && sts is not empty
        assert v >= 0 && v < 32768;
        short p = sts[0];  //p = p < 0 ? 0 : p;
        if(p == 32767) throw new ArithmeticException("integer overflow");

        if((++p) >= sts.length) {
            /*if(sts.length < (MAX_ARRAY_SIZE - 4)) {  //MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8
                int newLength = sts.length + 4; //increment by fixed step
            } else {
                overflow
            }*/
            
            //increment by double
            int newLength = Math.multiplyExact(sts.length, 2);  //ArithmeticException if overflow, or OutOfMemoryError if `newLength > MAX_ARRAY_SIZE`
            sts = Arrays.copyOf(sts, newLength);
        }
        sts[p] = (short) v;
        sts[0] = p;
        return sts;
    }


    private static short getPos(short[] sts) {
        //assert sts != null && sts is not empty
        assert sts[0] > 0;
        return sts[sts[0]];
    }

    private static short havePos(short[] sts) {
        //assert sts != null && sts is not empty
        return sts[0];
    }

    private static short popPos(short[] sts) {
        //assert sts != null && sts is not empty
        assert sts[0] > 0;
        short p = sts[0];
        short v = sts[p--];
        sts[0] = p;
        return v;
    }

    public static String truncate(String sql) {
        //assert sql != null;
        return truncate(sql, true);
    }

    public static String truncate(String sql, boolean existsComments) {
        //assert sql != null;
        return truncate(sql, existsComments, true);
    }

    //去除 sql 前置后置 whitespace 和多余的 () 对. 并确保 ";" 作为结束字符
    public static String truncate(String sql, boolean existsComments, boolean check) {
        //assert sql != null;
        if(existsComments)
            sql = truncateComments(sql);

        short[] sts = new short[4];
        int parentheses = 0;
        int pos = 0;
        for(; pos < sql.length(); pos++) {
            char c = sql.charAt(pos);   //cannot handle supplementary characters

            if(ASCII_alpha(c)) {
                break;
            } else if(c == LEFT_PARENTHESES) {
                sts = setPos(sts, pos);
                parentheses++;
            } else if(!ASCII_whitespace(c)) { //substitute with `!Character.isWhitespace(c)` if necessary
                throw new IllFormedSqlException("illegal leading character for sql [ " + sql + " ], near '" + sql.substring(pos) + "'");
            }
        }

        if(pos == sql.length()) throw new IllFormedSqlException("ill-formed sql [ " + sql + " ]");

        int endPos = sql.length() - 1;
        boolean tailWrong = false;
        int nonEmptyTail = pos;
        boolean tml = false;
        for(; endPos >= pos; endPos--) {
            char c = sql.charAt(endPos);

            if(c == SQL_TERMINAL && tailWrong) {
                throw new IllFormedSqlException("non-whitespace character for the tail of the ';' for sql [ " + sql + " ], near '" + sql.substring(endPos) + "'");
            } else if(c == SQL_TERMINAL) {
                tml = true;
            } else if(tml && !ASCII_whitespace(c)) {
                break;
            } else if(!tailWrong && !ASCII_whitespace(c)) {
                tailWrong = true;
                nonEmptyTail = endPos;
            }
        }
        if(endPos < pos) endPos = nonEmptyTail;

        if(parentheses != 0 || check) { //消减 parentheses 到 0 并且 check whether '(' match ')' at the range of [pos, endPos+1)
            sql = matchedPair(sql, pos, endPos + 1, LEFT_PARENTHESES, RIGHT_PARENTHESES, parentheses, sts);
        }

        sql = sql.substring(pos, endPos + 1) + SQL_TERMINAL;
        return sql;
    }

    private static String matchedPair(String sql, int begin, int end, char prefix, char suffix, int parentheses, short[] sts) {
        //assert sql != null
        //assert end >= 0
        //assert prefix != suffix
        StringBuilder sb = new StringBuilder(sql);
        EncloseContext encloseContext = new EncloseContext(false);

        int pos = begin;
        while(pos < end) {
            char c = sb.charAt(pos);
            int entered = encloseContext.handle(sb, pos);

            if(entered == -1) { //when not in enclosing context
                if(c == prefix) {
                    sts = setPos(sts, pos);
                } else if(c == suffix) {
                    if(havePos(sts) == 0) throw new IllFormedSqlException("no matched parentheses for sql [ " + sql + " ], near '" + sql.substring(pos) + "'");
                    popPos(sts);

                    if(havePos(sts) < parentheses) {
                        sb.replace(pos, pos + 1, " ");
                        parentheses--;
                    }
                } else if(c == SQL_TERMINAL) {
                    throw new IllFormedSqlException("unexpected sql terminal ';' for sql [ " + sql + " ], near '" + sql.substring(pos) + "'");
                }
            }

            pos++;
        }

        if(havePos(sts) != 0) throw new IllFormedSqlException("no matched parentheses for sql [ " + sql + " ], near '" + sql.substring(getPos(sts)) + "'");

        return sb.toString();
    }

    public static boolean matchedPair(String str, char prefix, char suffix) {
        //assert str != null
        //assert prefix != suffix
        return matchedPair(str, 0, str.length(), prefix, suffix);
    }

    public static boolean matchedPair(String str, int begin, int end, char prefix, char suffix) {
        //assert str != null
        //assert begin >= 0
        //assert prefix != suffix
        return matchedPair(str, begin, end, prefix, suffix, 0);
    }

    public static boolean matchedPair(String str, int begin, int end, char prefix, char suffix, int prefixArd) {
        //assert str != null
        //assert begin >= 0
        //assert prefix != suffix
        int predict = Math.max(prefixArd, 0);
        EncloseContext encloseContext = new EncloseContext();

        int pos = begin;
        while(pos < end) {
            char c = str.charAt(pos);

            if(encloseContext.handle(str, pos) == -1) { //when not in enclosing context
                if(c == prefix) {
                    predict++;
                } else if(c == suffix) {
                    if(predict == 0) return false;
                    else {
                        predict--;
                    }
                }
            }

            pos++;
        }
        return predict == 0;
    }

    public static boolean matchedPair(String str, String prefix, String suffix) {
        //assert str != null
        //assert prefix not equivalent to suffix
        return matchedPair(str, 0, str.length(), prefix, suffix);
    }

    public static boolean matchedPair(String str, int begin, int end, String prefix, String suffix) {
        //assert str != null
        //assert prefix not equivalent to suffix
        int predict = 0;
        int pos = begin;
        while(pos < end) {
            int prefixIdx = str.indexOf(prefix, pos);
            int suffixIdx = str.indexOf(suffix, pos);

            if(prefixIdx >= end) prefixIdx = -1;
            if(suffixIdx >= end) suffixIdx = -1;

            if(suffixIdx == -1) {
                return prefixIdx == -1 && predict == 0;
            } else {
                if(prefixIdx == -1 || prefixIdx > suffixIdx) {
                    if(predict > 0) {
                        predict--;
                        pos = suffixIdx + 1;
                    } else {
                        return false;
                    }
                } else {
                    predict++;
                    pos = prefixIdx + 1;
                }
            }
        }
        return predict == 0;
    }

    public static boolean ASCII_alpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean ASCII_digit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean ASCII_whitespace(char c) {
        return ASCII_whitespace(c, false);
    }

    public static boolean ASCII_whitespace(char c, boolean exactly) {
        if(exactly) {
            //exactly: ASCII SP (space): 0x20、US (unit separator): 0x1F、RS (record separator): 0x1E、GS (group separator): 0x1D、FS (file separator): 0x1C
            //    CR (carriage return): 0x0D、LF (NL line feed, new line): 0x0A、FF (NP form feed, new page): 0x0C、VT (vertical tab): 0x0B、HT (horizontal tab): 0x09

            /*return c == ' ' || c == '\u001F' || c == '\u001E' || c == '\u001D' || c == '\u001C' ||
                   c == '\r' || c == '\n' || c == '\f' || c == '\u000B' || c == '\t';*/

            return c <= ' ' && Character.isWhitespace(c);  //@link Character#isWhitespace(char ch)
        }
        return c <= ' ';  //compatible with String#trim()
    }

    private static class EncloseContext {
        char surroundChar = NUL_CHAR;
        boolean escape = false;
        final boolean prev;

        EncloseContext() {
            this.prev = false;
        }
        EncloseContext(boolean prevMode) {
            this.prev = prevMode;
        }
        EncloseContext(char surroundChar, boolean escape, boolean prevMode) {
            this.surroundChar = surroundChar;
            this.escape = escape;
            this.prev = prevMode;
        }

        //call this method when traverse backward
        int handle(CharSequence csq, int idx) {
            if(prev) throw new IllegalStateException("can not traverse backward in prev mode");

            char c = csq.charAt(idx);
            /*note: In MySQL, If the `ANSI_QUOTES` SQL mode is enabled, double quote will act as same as `.
            But this program do not handle it, and always think double quote is enclosing a string literal.*/
            if(surroundChar == SINGLE_QUOTE || surroundChar == DOUBLE_QUOTE) {
                /*note: In MySQL, If the `NO_BACKSLASH_ESCAPES` SQL mode is enabled,
                the escape meaning of backslash '\' will disabled.
                But this program do not handle it, and always think backslash '\' has escape meaning. */
                if(c == surroundChar && !escape) {
                    surroundChar = NUL_CHAR;
                    escape = false;
                    return 0;  //out
                } else if(c == surroundChar) {
                    escape = false;
                    return  3; //被转义 (inside)
                } else if(c == BACK_SLASH) {
                    escape = !escape;
                } else {
                    escape = false;
                }
            } else if(surroundChar == BACK_QUOTE && c == BACK_QUOTE) {
                surroundChar = NUL_CHAR;
                return 0;  //out
            } else if (surroundChar == NUL_CHAR && (c == SINGLE_QUOTE || c == DOUBLE_QUOTE || c == BACK_QUOTE)) {
                surroundChar = c;
                return 1;  //in
            }

            return surroundChar == NUL_CHAR ? -1 : 2;  //outside or inside
        }

        //call this method when traverse forward
        int handle(int idx, CharSequence csq) {
            if(!prev) throw new IllegalStateException("can not traverse forward in next mode");

            char c = csq.charAt(idx);
            if((surroundChar == SINGLE_QUOTE || surroundChar == DOUBLE_QUOTE) && c == surroundChar) {
                int pos = idx - 1;
                while(pos >= 0 && csq.charAt(pos) == BACK_SLASH) {
                    pos--;
                }
                if((idx - pos) % 2 != 0) {
                    surroundChar = NUL_CHAR;
                    return 0;  //out
                } else {
                    return 3;  //被转义 (inside)
                }
            } else if(surroundChar == BACK_QUOTE && c == BACK_QUOTE) {
                surroundChar = NUL_CHAR;
                return 0;  //out
            } else if (surroundChar == NUL_CHAR && (c == SINGLE_QUOTE || c == DOUBLE_QUOTE || c == BACK_QUOTE)) {
                surroundChar = c;
                return 1;  //in
            }

            return surroundChar == NUL_CHAR ? -1 : 2;  //outside or inside
        }
    }

    public static void main(String[] argv) {
        System.out.println(matchedPair(" ) ( ) (", '(', ')'));  //false
        System.out.println(matchedPair(" ( ( (  ')\\'' ) ) ) '('  ( ) ", '(', ')'));   //true

        System.out.println(matchedPair(" what ${  ${ placeholder  } } ", "${", "}"));  //true
        System.out.println(matchedPair(" single ' double \" ", "'", "\""));            //true

        /*

		((#fsdafsd
		#1f -- as-f--sd
		-- #  fsadf
(
select(3123), '# literals -\\\'- l# ll', 1 + 1 + 1 -- -- fsdfsd
as id-- hjkh
from city )
)
	)
	# kfjsafkj
	/*fasfas*/
        ;/*faskfsjdfsdfsd/
         */
        String sql = "\n" +
                "\t\t((#fsdafsd\n" +
                "\t\t#1f -- as-f--sd\n" +
                "\t\t-- #  fsadf\n" +
                "(\t\n" +
                "select(3123), '# literals -\\\\\\'- l# ll', 1 + 1 + 1 -- -- fsdfsd\n" +
                "as id-- hjkh\n" +
                "from city ) \n" +
                ") \n" +
                "\t)\n" +
                "\t# kfjsafkj\n" +
                "\t/*fasfas*/\n" +
                "        ;/*faskfsjdfsdfsd*/";
        System.out.println(truncate("select id /*!from*/ city;", true, true));

    }

}
