package com.fresh.common.sqlsyntax;


import com.fresh.common.utils.sql.IllFormedSqlException;
import com.fresh.common.utils.sql.SqlUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectParser {
    //SelectSyntaxSample defined in SqlSyntax_sample.md
    private SelectSyntax selectSyntax;

    public SelectParser parse(String sql) {
        //sql = SqlParserUtil.redundant(sql, SqlKeyword.SELECT.getValue());
        sql = SqlUtils.truncate(sql);
        ParserContext parserContext = new ParserContext(sql, 0);

        parseSlt(parserContext);

        parseTlt(parserContext);

        parseWhe(parserContext);

        parseGby(parserContext);

        parseHv(parserContext);

        parseOby(parserContext);

        parseLmt(parserContext);

        parseEnd(parserContext);

        this.selectSyntax = parserContext.ofSelectSyntax();
        return this;
    }

    public String countSql() {
        return selectSyntax.countSql();
    }

    public String pageSql() {
        return selectSyntax.pageSql();
    }

    private boolean parseEnd(ParserContext parserContext) {
        //END
        String sql = parserContext.getSql();

        int endIdx = StringUtils.indexOf(sql, SqlConstant.SQL_TERMINAL, parserContext.getIdx());
        if(endIdx == -1) {
            throw new IllFormedSqlException("non-terminal sql [" + sql + "], idx=" + endIdx);
        }
        if(endIdx != sql.length()-1) {
            throw new IllFormedSqlException("ill-formed sql [" + sql + "], idx=" + endIdx);
        }
        parserContext.setIdx(endIdx+1);

        SelectNode selectNode =  new SelectNode(SqlKeyword.TERMINAL, SqlConstant.SQL_TERMINAL, null);
        parserContext.add(selectNode);
        return true;
    }

    private boolean parseLmt(ParserContext parserContext) {
        //LIMIT
        String sql = parserContext.getSql();

        int limitIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.LIMIT.getValue(), parserContext.getIdx());
        if(limitIdx == -1 && sql.charAt(parserContext.getIdx()) == SqlConstant.SQL_TERMINAL.charAt(0)) {
            return false;
        } else if(limitIdx == -1) {
            throw new IllFormedSqlException("ill-formed sql [" + sql + "], idx=" + limitIdx);
        }

        int limitIdxEnd = SqlParserUtil.findRelative(sql, limitIdx, SqlKeyword.LIMIT.getValue(), SqlConstant.SQL_TERMINAL);
        if(limitIdxEnd == -1) throw new IllFormedSqlException("no limit condition [" + sql + "], idx=" + limitIdx);

        String limit_condition = sql.substring(limitIdx + SqlKeyword.LIMIT.getValue().length(), limitIdxEnd);
        parserContext.setIdx(limitIdxEnd);
        LimitCondition lcd = new LimitCondition(limit_condition);

        SelectNode selectNode =  new SelectNode(SqlKeyword.LIMIT, limit_condition, lcd);
        parserContext.setExistsLimit(true);
        parserContext.add(selectNode);
        return true;
    }

    private boolean parseOby(ParserContext parserContext) {
        //ORDER_BY_LIST
        String sql = parserContext.getSql();

        int orderByIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.ORDER_BY.getValue(), parserContext.getIdx());
        if(orderByIdx == -1) return false;

        int orderByIdxEnd = SqlParserUtil.findRelative(sql, orderByIdx, SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlConstant.SQL_TERMINAL);
        if(orderByIdxEnd == -1) throw new IllFormedSqlException("no order by list [" + sql + "], idx=" + orderByIdx);

        String order_by_list = sql.substring(orderByIdx + SqlKeyword.ORDER_BY.getValue().length(), orderByIdxEnd);
        parserContext.setIdx(orderByIdxEnd);

        SelectNode selectNode =  new SelectNode(SqlKeyword.ORDER_BY, order_by_list, null);
        parserContext.add(selectNode);
        return true;
    }

    private boolean parseHv(ParserContext parserContext) {
        //HAVING_CONDITION
        String sql = parserContext.getSql();

        int havingIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.HAVING.getValue(), parserContext.getIdx());
        if(havingIdx == -1) return false;

        int havingIdxEnd = SqlParserUtil.findRelative(sql, havingIdx, SqlKeyword.HAVING.getValue(), SqlKeyword.ORDER_BY.getValue(),
                                                                      SqlKeyword.LIMIT.getValue(), SqlConstant.SQL_TERMINAL);
        if(havingIdxEnd == -1) throw new IllFormedSqlException("no having condition [" + sql + "], idx=" + havingIdx);

        String having_condition = sql.substring(havingIdx + SqlKeyword.HAVING.getValue().length(), havingIdxEnd);
        parserContext.setIdx(havingIdxEnd);

        SelectNode selectNode =  new SelectNode(SqlKeyword.HAVING, having_condition, null);
        parserContext.add(selectNode);
        return true;
    }

    private boolean parseGby(ParserContext parserContext) {
        //GROUP_BY_LIST
        String sql = parserContext.getSql();

        int groupByIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.GROUP_BY.getValue(), parserContext.getIdx());
        if(groupByIdx == -1) return false;

        int groupByIdxEnd = SqlParserUtil.findRelative(sql, groupByIdx, SqlKeyword.GROUP_BY.getValue(), SqlKeyword.HAVING.getValue(),
                                               SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlConstant.SQL_TERMINAL);
        if(groupByIdxEnd == -1) throw new IllFormedSqlException("no group by list [" + sql + "], idx=" + groupByIdx);

        String group_by_list = sql.substring(groupByIdx + SqlKeyword.GROUP_BY.getValue().length(), groupByIdxEnd);
        parserContext.setIdx(groupByIdxEnd);

        SelectNode selectNode =  new SelectNode(SqlKeyword.GROUP_BY, group_by_list, null);
        parserContext.setExistsGroupBy(true);
        parserContext.add(selectNode);
        return true;
    }

    private boolean parseWhe(ParserContext parserContext) {
        //WHERE_CONDITION
        String sql = parserContext.getSql();

        int whereIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.WHERE.getValue(), parserContext.getIdx());
        if(whereIdx == -1) return false;

        int whereIdxEnd = SqlParserUtil.findRelative(sql, whereIdx, SqlKeyword.WHERE.getValue(), SqlKeyword.GROUP_BY.getValue(),
                                             SqlKeyword.HAVING.getValue(), SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlConstant.SQL_TERMINAL);
        if(whereIdxEnd == -1) throw new IllFormedSqlException("no where condition [" + sql + "], idx=" + whereIdx);

        String where_condition = sql.substring(whereIdx + SqlKeyword.WHERE.getValue().length(), whereIdxEnd);
        parserContext.setIdx(whereIdxEnd);

        SelectNode selectNode =  new SelectNode(SqlKeyword.WHERE, where_condition, null);
        parserContext.add(selectNode);
        return true;
    }

    private boolean parseTlt(ParserContext parserContext) {
        //TABLE_LIST
        String sql = parserContext.getSql();

        int fromIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.FROM.getValue(), parserContext.getIdx());
        if(fromIdx == -1 && sql.charAt(parserContext.getIdx()) == SqlConstant.SQL_TERMINAL.charAt(0)) {
            return false;
        } else if(fromIdx == -1) {
            throw new IllFormedSqlException("ill-formed sql [" + sql + "], idx=" + fromIdx);
        }

        int fromIdxEnd = SqlParserUtil.findRelative(sql, fromIdx, SqlKeyword.FROM.getValue(), SqlKeyword.WHERE.getValue(), SqlKeyword.GROUP_BY.getValue(),
                                            SqlKeyword.HAVING.getValue(), SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlConstant.SQL_TERMINAL);
        if(fromIdxEnd == -1) throw new IllFormedSqlException("no table list [" + sql + "], idx=" + fromIdx);

        String table_list = sql.substring(fromIdx + SqlKeyword.FROM.getValue().length(), fromIdxEnd);
        parserContext.setIdx(fromIdxEnd);
        TableList tableList = new TableList(table_list);

        SelectNode selectNode =  new SelectNode(SqlKeyword.FROM, table_list, tableList);
        parserContext.add(selectNode);
        return true;
    }

    private boolean parseSlt(ParserContext parserContext) {
        //SELECT_LIST
        String sql = parserContext.getSql();

        int selectIdx = parserContext.getIdx();   //StringUtils.indexOfIgnoreCase(sql, SqlKeyword.SELECT.getValue(), parserContext.getIdx());
        if(selectIdx == -1) throw new IllFormedSqlException("no select [" + sql + "], idx=" + selectIdx);

        int selectIdxEnd = SqlParserUtil.findRelative(sql, selectIdx, SqlKeyword.SELECT.getValue(), SqlKeyword.FROM.getValue(), SqlKeyword.ORDER_BY.getValue(),
                                                                      SqlKeyword.LIMIT.getValue(), SqlConstant.SQL_TERMINAL);
        if(selectIdxEnd == -1) throw new IllFormedSqlException("no select list [" + sql + "], idx=" + selectIdx);

        String select_list = sql.substring(selectIdx + SqlKeyword.SELECT.getValue().length(), selectIdxEnd);
        SelectList slt = new SelectList(select_list);
        SelectNode selectNode = new SelectNode(SqlKeyword.SELECT, select_list, slt);

        parserContext.setIdx(selectIdxEnd);
        parserContext.setDistinct(slt.distinct);
        parserContext.add(selectNode);
        return true;
    }

    @Override
    public String toString() {
        return selectSyntax.toString();
    }

    static class SelectSyntax {
        private final List<SelectNode> nodes;
        private final boolean distinct;
        private final boolean existsLimit;
        private final boolean existsGroupBy;

        public SelectSyntax(List<SelectNode> nodes, boolean distinct, boolean existsLimit, boolean existsGroupBy) {
            this.nodes = nodes;
            this.distinct = distinct;
            this.existsLimit = existsLimit;
            this.existsGroupBy = existsGroupBy;
        }

        public String countSql() {
            StringBuilder sb = new StringBuilder();
            for(SelectNode node : nodes) {
                sb.append(node.countSql(distinct, existsLimit, existsGroupBy)).append(SqlConstant.LF);
            }

            return distinct || existsLimit || existsGroupBy ? SqlKeyword.SELECT.getValue()
                               + SqlConstant.SPACE + SqlConstant.COUNT_SQ
                               + SqlConstant.SPACE + SqlKeyword.FROM.getValue()
                               + SqlConstant.SPACE + SqlConstant.LEFT_PARENTHESES
                               + SqlConstant.LF + sb.toString()
                               + SqlConstant.LF + SqlConstant.RIGHT_PARENTHESES
                               + SqlConstant.SPACE + "__alias__count__" + SqlConstant.SQL_TERMINAL
                   : sb.toString();
        }

        public String pageSql() {
            StringBuilder sb = new StringBuilder();

            for(SelectNode node : nodes) {
                if(node.keyword == SqlKeyword.TERMINAL) {
                    break;
                }
                sb.append(node).append(SqlConstant.LF);
            }

            return existsLimit ? SqlKeyword.SELECT.getValue()
                                  + SqlConstant.SPACE + SqlConstant.ALL_COLUMN
                                  + SqlConstant.SPACE + SqlKeyword.FROM.getValue()
                                  + SqlConstant.SPACE + SqlConstant.LEFT_PARENTHESES
                                  + SqlConstant.LF + sb.toString()
                                  + SqlConstant.LF + SqlConstant.RIGHT_PARENTHESES
                                  + SqlConstant.SPACE + "__alias__page__"
                                  + SqlConstant.LF + SqlConstant.MYSQL_PAGE_SQ + SqlConstant.LF + SqlConstant.SQL_TERMINAL
                   : sb.toString() + SqlConstant.LF + SqlConstant.MYSQL_PAGE_SQ + SqlConstant.LF + SqlConstant.SQL_TERMINAL;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int last = nodes.size() - 1;
            for(int i = 0; i < nodes.size(); i++) {
                sb.append(nodes.get(i));
                if(i != last) {
                    sb.append(SqlConstant.LF);
                }
            }
            return sb.toString();
        }
    }

    static class SelectNode {
        static final String SEP = SqlConstant.SPACE;
        SqlKeyword keyword;
        String nodeValueStr;
        NodeValue nodeValue;

        public SelectNode(SqlKeyword keyword, String nodeValueStr, NodeValue nodeValue) {
            this.keyword = keyword;
            this.nodeValueStr = nodeValueStr;
            this.nodeValue = nodeValue;
        }

        public String countSql(boolean distinct, boolean existsLimit, boolean existsGroupBy) {
            switch(keyword) {
                case SELECT:
                    if(distinct) {
                        return toString();
                    } else if(existsLimit || existsGroupBy) {
                        return keyword.getValue() + SqlConstant.SPACE + SqlConstant.COUNT_SQ_INNER;
                    } else {
                        return keyword.getValue() + SqlConstant.SPACE + SqlConstant.COUNT_SQ;
                    }
                case FROM:
                case WHERE:
                case GROUP_BY:
                case HAVING:
                case LIMIT:
                    return toString();
                case ORDER_BY:
                    return "";
                case TERMINAL:
                    return distinct || existsLimit || existsGroupBy ? "" : StringUtils.trim(nodeValueStr);
            }
            throw new IllFormedSqlException("unexpected sql keyword [" + keyword.getValue() + "]");
        }

        @Override
        public String toString() {
            return keyword.getValue() + SEP + (nodeValue != null ? nodeValue : StringUtils.trim(nodeValueStr));
        }
    }

    abstract static class NodeValue {
        String nodeValue;

        public NodeValue(String nodeValue) {
            this.nodeValue = nodeValue;
        }

        @Override
        public String toString() {
            return nodeValue;
        }
    }

    static class LimitCondition extends NodeValue {
        String current;
        String pageSize;

        public LimitCondition(String limit_condition) {
            super(limit_condition);
            int idx = limit_condition.indexOf(SqlConstant.COMMA);
            if(idx == -1) {
                current = "";
                pageSize = StringUtils.trim(limit_condition);
            } else {
                current = StringUtils.trim(limit_condition.substring(0, idx));
                pageSize = StringUtils.trim(limit_condition.substring(idx+1));
            }
        }

        @Override
        public String toString() {
            return StringUtils.isEmpty(current) ? pageSize : current + SqlConstant.COMMA + SqlConstant.SPACE + pageSize;
        }
    }

    static class TableList extends NodeValue {
        static final String[] PREFIX = {SqlConstant.FULL_JOIN_PREFIX, SqlConstant.CROSS_JOIN_PREFIX,
                SqlConstant.INNER_JOIN_PREFIX, SqlConstant.LEFT_JOIN_PREFIX, SqlConstant.RIGHT_JOIN_PREFIX,
                SqlConstant.LEFT_OUTER_JOIN_PREFIX, SqlConstant.RIGHT_OUTER_JOIN_PREFIX};

        static final String JOIN = SqlKeyword.JOIN.getValue();

        final List<TableElement> tableElements = new ArrayList<>();

        public TableList(String nodeValueStr) {
            super(nodeValueStr);
            int idx = 0;
            int nc;
            String joinRl = "";
            while((nc = nextTable(nodeValueStr, idx, idx)) != -1) {
                int ji = joinType(nodeValueStr, nc);
                String tableTailingStr = StringUtils.trim(nodeValueStr.substring(idx, ji));
                TableElement tableElement = new TableElement(tableTailingStr, joinRl);
                tableElements.add(tableElement);
                idx = nc + JOIN.length();
                joinRl = StringUtils.trim(nodeValueStr.substring(ji, idx));
            }
            if(idx < nodeValueStr.length()) { //idx ~ end
                String tableTailingStr = StringUtils.trim(nodeValueStr.substring(idx));
                TableElement tableElement = new TableElement(tableTailingStr, joinRl);
                tableElements.add(tableElement);
            }
        }

        private static int nextTable(String str, int from, int prevIdx) {
            int idx = StringUtils.indexOfIgnoreCase(str, JOIN, from);
            if(idx == -1) return -1;

            if(SqlParserUtil.isCharLiteral(str, prevIdx, idx) || joinType(str, idx) == -1) {
                idx = nextTable(str, idx + 1, prevIdx);
                return idx;
                //if(idx == -1) return -1;
            }

            if(SqlParserUtil.isNested(str, prevIdx, idx)) {
                int rc = SqlParserUtil.findRelativeParentheses(str, idx);
                if(rc == -1) throw new IllFormedSqlException("not matched ( [" + str + "], idx=" + rc);
                idx = nextTable(str, rc, prevIdx);
            }

            return idx;
        }

        private static int joinType(String str, int idx) {
            if(!SqlParserUtil.keywordRightBound(str, JOIN, idx)) return -1;

            char c;
            if(idx > 0 && ((c = str.charAt(idx-1)) == '_')) {
                int sidx = StringUtils.lastIndexOfIgnoreCase(str, SqlConstant.STRAIGHT_JOIN_PREFIX, idx-1);

                if(sidx > -1 && (sidx + SqlConstant.STRAIGHT_JOIN_PREFIX.length()) == idx - 1 && SqlParserUtil.keywordLeftBound(str, sidx)) {
                    return sidx;
                } else {
                    return -1;
                }
            } else if(idx > 0 && (SqlUtils.ASCII_whitespace(str.charAt(idx - 1)))) {
                int iidx = idx-1;
                while(iidx >= 0 && (SqlUtils.ASCII_whitespace(str.charAt(iidx)))) {
                    iidx--;
                }
                if(iidx >= 0) {
                    for (String prefix : PREFIX) {
                        int sidx = StringUtils.lastIndexOfIgnoreCase(str, prefix, iidx);

                        if (sidx > -1 && (sidx + prefix.length()) == (iidx + 1) && SqlParserUtil.keywordLeftBound(str, sidx)) {
                            return sidx;
                        }
                    }
                }

                return idx;
            }

            return SqlParserUtil.keywordLeftBound(str, idx) ? idx : -1;
        }

        @Deprecated
        private static boolean isJoinType(String str, int idx) {
            if(!SqlParserUtil.keywordRightBound(str, JOIN, idx)) return false;

            char c;
            if(idx > 0 && ((c = str.charAt(idx-1)) == '_')) {
                int sidx = StringUtils.lastIndexOfIgnoreCase(str, SqlConstant.STRAIGHT_JOIN_PREFIX, idx-1);
                return sidx > -1 && (sidx + SqlConstant.STRAIGHT_JOIN_PREFIX.length()) == idx - 1 && SqlParserUtil.keywordLeftBound(str, sidx);

            } else if(idx > 0 && ((c = str.charAt(idx-1)) == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f')) {
                int iidx = idx-1;
                while(iidx >= 0 && ((c = str.charAt(iidx)) == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f')) {
                    iidx--;
                }
                if(iidx >= 0) {
                    for (String prefix : PREFIX) {
                        int sidx = StringUtils.lastIndexOfIgnoreCase(str, prefix, iidx);
                        if (sidx > -1 && (sidx + prefix.length()) == (iidx + 1)) {
                            return SqlParserUtil.keywordLeftBound(str, sidx);
                        }
                    }
                }

                return true;
            }

            return SqlParserUtil.keywordLeftBound(str, idx);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int last = tableElements.size() - 1;
            for(int i = 0; i < tableElements.size(); i++) {
                sb.append(tableElements.get(i));
                if(i != last) {
                    sb.append(SqlConstant.LF).append(SqlConstant.SPACE);
                }
            }
            return sb.toString();
        }
    }

    static class TableElement {
        String tableElementStr;
        String joinType = "";
        String tableName;
        SelectSyntax subQuery;
        String as = "";
        String tableAlias;
        Expression onCondition;

        public TableElement(String tableTailingStr, String joinType) {
            this.joinType = joinType;
            this.tableElementStr = joinType + SqlConstant.SPACE + tableTailingStr;
        }

        @Override
        public String toString() {
            return tableElementStr;
        }
    }

    static class SelectList extends NodeValue {
        final List<Column> columns = new ArrayList<>();
        final boolean distinct;

        public SelectList(String nodeValueStr) {
            super(nodeValueStr);
            this.distinct = StringUtils.startsWithIgnoreCase(nodeValueStr.trim(), SqlKeyword.DISTINCT.getValue());

            int idx = 0;
            int nc;
            while((nc = nextComma(nodeValueStr, idx, idx)) != -1) {
                String columnStr = StringUtils.trim(nodeValueStr.substring(idx, nc));
                Column column = new Column(columnStr);
                columns.add(column);
                idx = nc + 1;
            }
            if(idx < nodeValueStr.length()) { //idx ~ end
                String columnStr = StringUtils.trim(nodeValueStr.substring(idx));
                Column column = new Column(columnStr);
                columns.add(column);
            }
        }

        private static int nextComma(String str, int from, int prevIdx) {
            int idx = str.indexOf(SqlConstant.COMMA, from);
            if(idx == -1) return -1;

            if(SqlParserUtil.isCharLiteral(str, prevIdx, idx)) {
                idx = nextComma(str, idx + 1, prevIdx);
                return idx;
                //if(idx == -1) return -1;
            }

            if(SqlParserUtil.isNested(str, prevIdx, idx)) {
                int rc = SqlParserUtil.findRelativeParentheses(str, idx);
                if(rc == -1) throw new IllFormedSqlException("not matched ( [" + str + "], idx=" + rc);
                idx = nextComma(str, rc, prevIdx);
            }

            return idx;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int last = columns.size() - 1;
            for(int i = 0; i < columns.size(); i++) {
                sb.append(columns.get(i));
                if(i != last) {
                    sb.append(SqlConstant.COMMA + SqlConstant.SPACE);
                }
            }
            return sb.toString();
        }
    }
    static class Column {
        String columnStr;
        Expression expression;
        String as = "";
        String columnAlias;

        public Column(String columnStr) {
            this.columnStr = columnStr;
        }

        @Override
        public String toString() {
            return columnStr;
        }
    }

    static class Expression {

    }

    public static void main(String[] argv) {
        /*

select DISTINCT ct . `id` `i'd","d'd`, (1) as '2"(,1', 'select' as `from`, 12.34 n_q, (1 + 1 )  + 1 exp,
        `CONCAT` (CONCAT('-"(,"-', ')', ','), ct. `name`, "',", 'sdf"from"') AS `low name`,
        (select id from city where id in (1000101, 1000102) limit 1) as `temp`,
				(select 'select') as no_from,
				(1+232) + (1<2) * (1 AND 1) al_op

from `shape` . `city` as `ct` left join ((select * from city as `123qwe_123`)) as aa_join on (ct.id in (((select id from city)))) and (ct.`name` LIKE '%市')
														  STRAIGHT_JOIN (select id as ',,,,join,,,,' from city) `inner join` on 1=1
															cross      join (select nct1.id from course nct1 join course nct2) as jjj

where CONCAT(ct.`name`, `ct`.id) in ('鼠标市1000101')

group by 123.4567, 7 DESC, '123', ct.id=123, (select `name` from city where 1=1 limit 1)

having ct.id=123

order by ct.id,  `ct`.`name` DESC

limit 1, 222
;

         */
        String sql = "\n" +
                "select DISTINCT ct . `id` `i'd\",\"d'd`, (1) as '2\"(,1', 'select' as `from`, 12.34 n_q, (1 + 1 )  + 1 exp, \n" +
                "        `CONCAT` (CONCAT('-\"(,\"-', ')', ','), ct. `name`, \"',\", 'sdf\"from\"') AS `low name`, \n" +
                "        (select id from city where id in (1000101, 1000102) limit 1) as `temp`,\n" +
                "\t\t\t\t(select 'select') as no_from,\n" +
                "\t\t\t\t(1+232) + (1<2) * (1 AND 1) al_op \n" +
                "\t\t\t\t\n" +
                "from `shape` . `city` as `ct` left join ((select * from city as `123qwe_123`)) as aa_join on (ct.id in (((select id from city)))) and (ct.`name` LIKE '%市')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t  STRAIGHT_JOIN (select id as ',,,,join,,,,' from city) `inner join` on 1=1  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tcross      join (select nct1.id from course nct1 join course nct2) as jjj\n" +
                "\n" +
                "where CONCAT(ct.`name`, `ct`.id) in ('鼠标市1000101')\n" +
                "\n" +
                "group by 123.4567, 7 DESC, '123', ct.id=123, (select `name` from city where 1=1 limit 1)\n" +
                "\n" +
                "having ct.id=123\n" +
                "\n" +
                "order by ct.id,  `ct`.`name` DESC\n" +
                "\n" +
                "limit 1, 222\n" +
                ";\n";

        SelectParser selectParser = new SelectParser().parse(sql);

        System.out.println(selectParser);

        System.out.println(selectParser.countSql());

        System.out.println(selectParser.pageSql());
    }

}
