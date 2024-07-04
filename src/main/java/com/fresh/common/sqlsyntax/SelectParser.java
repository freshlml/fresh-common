package com.fresh.common.sqlsyntax;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectParser {

    private SelectSyntax selectSyntax;

    public SelectParser parse(String sql) {
        List<SelectNode> nodes = new ArrayList<>();
        sql = SqlParserUtil.redundant(sql, SqlKeyword.SELECT.getValue());
        ParserContext parserContext = new ParserContext(sql, 0);

        SelectNode selectNode = parseSlt(parserContext);
        nodes.add(selectNode);

        SelectNode selectNodeTlt = parseTlt(parserContext);
        if(selectNodeTlt != null) {
            nodes.add(selectNodeTlt);
        }

        SelectNode selectNodeWhe = parseWhe(parserContext);
        if(selectNodeWhe != null) {
            nodes.add(selectNodeWhe);
        }

        SelectNode selectNodeGb = parseGby(parserContext);
        if(selectNodeGb != null) {
            nodes.add(selectNodeGb);
        }

        SelectNode selectNodeHv = parseHv(parserContext);
        if(selectNodeHv != null) {
            nodes.add(selectNodeHv);
        }

        SelectNode selectNodeOb = parseOby(parserContext);
        if(selectNodeOb != null) {
            nodes.add(selectNodeOb);
        }

        SelectNode selectNodeLt = parseLmt(parserContext);
        if(selectNodeLt != null) {
            nodes.add(selectNodeLt);
        }

        SelectNode selectNodeEnd = parseEnd(parserContext);
        nodes.add(selectNodeEnd);

        this.selectSyntax = new SelectSyntax(nodes);
        return this;
    }

    public String countSql() {
        return selectSyntax.countSql();
    }

    public String pageSql() {
        return selectSyntax.pageSql();
    }

    private SelectNode parseEnd(ParserContext parserContext) {
        //END
        String sql = parserContext.getSql();

        int endIdx = StringUtils.indexOf(sql, SqlSyntaxConstant.SQL_TERMINAL, parserContext.getIdx());
        if(endIdx == -1) {
            throw new IllFormedSqlException("non-terminal sql [" + sql + "], idx=" + endIdx);
        }
        if(endIdx != sql.length()-1) {
            throw new IllFormedSqlException("ill-formed sql [" + sql + "], idx=" + endIdx);
        }
        parserContext.setIdx(endIdx+1);

        return new SelectNode(SqlKeyword.TERMINAL, SqlSyntaxConstant.SQL_TERMINAL, null);
    }

    private SelectNode parseLmt(ParserContext parserContext) {
        //LIMIT
        String sql = parserContext.getSql();

        int limitIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.LIMIT.getValue(), parserContext.getIdx());
        if(limitIdx == -1 && sql.charAt(parserContext.getIdx()) == SqlSyntaxConstant.SQL_TERMINAL.charAt(0)) {
            return null;
        } else if(limitIdx == -1) {
            throw new IllFormedSqlException("ill-formed sql [" + sql + "], idx=" + limitIdx);
        }

        int limitIdxEnd = SqlParserUtil.findRelative(sql, limitIdx, SqlKeyword.LIMIT.getValue(), SqlSyntaxConstant.SQL_TERMINAL);
        if(limitIdxEnd == -1) throw new IllFormedSqlException("no limit condition [" + sql + "], idx=" + limitIdx);

        String limit_condition = sql.substring(limitIdx + SqlKeyword.LIMIT.getValue().length(), limitIdxEnd);
        parserContext.setIdx(limitIdxEnd);
        LimitCondition lcd = new LimitCondition(limit_condition);

        return new SelectNode(SqlKeyword.LIMIT, limit_condition, lcd);
    }

    private SelectNode parseOby(ParserContext parserContext) {
        //ORDER_BY_LIST
        String sql = parserContext.getSql();

        int orderByIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.ORDER_BY.getValue(), parserContext.getIdx());
        if(orderByIdx == -1) return null;

        int orderByIdxEnd = SqlParserUtil.findRelative(sql, orderByIdx, SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlSyntaxConstant.SQL_TERMINAL);
        if(orderByIdxEnd == -1) throw new IllFormedSqlException("no order by list [" + sql + "], idx=" + orderByIdx);

        String order_by_list = sql.substring(orderByIdx + SqlKeyword.ORDER_BY.getValue().length(), orderByIdxEnd);
        parserContext.setIdx(orderByIdxEnd);

        return new SelectNode(SqlKeyword.ORDER_BY, order_by_list, null);
    }

    private SelectNode parseHv(ParserContext parserContext) {
        //HAVING_CONDITION
        String sql = parserContext.getSql();

        int havingIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.HAVING.getValue(), parserContext.getIdx());
        if(havingIdx == -1) return null;

        int havingIdxEnd = SqlParserUtil.findRelative(sql, havingIdx, SqlKeyword.HAVING.getValue(), SqlKeyword.ORDER_BY.getValue(),
                                                                      SqlKeyword.LIMIT.getValue(), SqlSyntaxConstant.SQL_TERMINAL);
        if(havingIdxEnd == -1) throw new IllFormedSqlException("no having condition [" + sql + "], idx=" + havingIdx);

        String having_condition = sql.substring(havingIdx + SqlKeyword.HAVING.getValue().length(), havingIdxEnd);
        parserContext.setIdx(havingIdxEnd);

        return new SelectNode(SqlKeyword.HAVING, having_condition, null);
    }

    private SelectNode parseGby(ParserContext parserContext) {
        //GROUP_BY_LIST
        String sql = parserContext.getSql();

        int groupByIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.GROUP_BY.getValue(), parserContext.getIdx());
        if(groupByIdx == -1) return null;

        int groupByIdxEnd = SqlParserUtil.findRelative(sql, groupByIdx, SqlKeyword.GROUP_BY.getValue(), SqlKeyword.HAVING.getValue(),
                                               SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlSyntaxConstant.SQL_TERMINAL);
        if(groupByIdxEnd == -1) throw new IllFormedSqlException("no group by list [" + sql + "], idx=" + groupByIdx);

        String group_by_list = sql.substring(groupByIdx + SqlKeyword.GROUP_BY.getValue().length(), groupByIdxEnd);
        parserContext.setIdx(groupByIdxEnd);

        return new SelectNode(SqlKeyword.GROUP_BY, group_by_list, null);
    }

    private SelectNode parseWhe(ParserContext parserContext) {
        //WHERE_CONDITION
        String sql = parserContext.getSql();

        int whereIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.WHERE.getValue(), parserContext.getIdx());
        if(whereIdx == -1) return null;

        int whereIdxEnd = SqlParserUtil.findRelative(sql, whereIdx, SqlKeyword.WHERE.getValue(), SqlKeyword.GROUP_BY.getValue(),
                                             SqlKeyword.HAVING.getValue(), SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlSyntaxConstant.SQL_TERMINAL);
        if(whereIdxEnd == -1) throw new IllFormedSqlException("no where condition [" + sql + "], idx=" + whereIdx);

        String where_condition = sql.substring(whereIdx + SqlKeyword.WHERE.getValue().length(), whereIdxEnd);
        parserContext.setIdx(whereIdxEnd);

        return new SelectNode(SqlKeyword.WHERE, where_condition, null);
    }

    private SelectNode parseTlt(ParserContext parserContext) {
        //TABLE_LIST
        String sql = parserContext.getSql();

        int fromIdx = StringUtils.indexOfIgnoreCase(sql, SqlKeyword.FROM.getValue(), parserContext.getIdx());
        if(fromIdx == -1 && sql.charAt(parserContext.getIdx()) == SqlSyntaxConstant.SQL_TERMINAL.charAt(0)) {
            return null;
        } else if(fromIdx == -1) {
            throw new IllFormedSqlException("ill-formed sql [" + sql + "], idx=" + fromIdx);
        }

        int fromIdxEnd = SqlParserUtil.findRelative(sql, fromIdx, SqlKeyword.FROM.getValue(), SqlKeyword.WHERE.getValue(), SqlKeyword.GROUP_BY.getValue(),
                                            SqlKeyword.HAVING.getValue(), SqlKeyword.ORDER_BY.getValue(), SqlKeyword.LIMIT.getValue(), SqlSyntaxConstant.SQL_TERMINAL);
        if(fromIdxEnd == -1) throw new IllFormedSqlException("no table list [" + sql + "], idx=" + fromIdx);

        String table_list = sql.substring(fromIdx + SqlKeyword.FROM.getValue().length(), fromIdxEnd);
        parserContext.setIdx(fromIdxEnd);
        TableList tableList = new TableList(table_list);

        return new SelectNode(SqlKeyword.FROM, table_list, tableList);
    }

    private SelectNode parseSlt(ParserContext parserContext) {
        //SELECT_LIST
        String sql = parserContext.getSql();

        int selectIdx = parserContext.getIdx();   //StringUtils.indexOfIgnoreCase(sql, SqlKeyword.SELECT.getValue(), parserContext.getIdx());
        if(selectIdx == -1) throw new IllFormedSqlException("no select [" + sql + "], idx=" + selectIdx);

        int selectIdxEnd = SqlParserUtil.findRelative(sql, selectIdx, SqlKeyword.SELECT.getValue(), SqlKeyword.FROM.getValue(), SqlKeyword.ORDER_BY.getValue(),
                                                                      SqlKeyword.LIMIT.getValue(), SqlSyntaxConstant.SQL_TERMINAL);
        if(selectIdxEnd == -1) throw new IllFormedSqlException("no select list [" + sql + "], idx=" + selectIdx);

        String select_list = sql.substring(selectIdx + SqlKeyword.SELECT.getValue().length(), selectIdxEnd);
        parserContext.setIdx(selectIdxEnd);
        SelectList slt = new SelectList(select_list);

        return new SelectNode(SqlKeyword.SELECT, select_list, slt);
    }

    @Override
    public String toString() {
        return selectSyntax.toString();
    }

    static class SelectSyntax {
        List<SelectNode> nodes;

        public SelectSyntax(List<SelectNode> nodes) {
            this.nodes = nodes;
        }

        public String countSql() {
            boolean distinct = false;
            StringBuilder sb = new StringBuilder();

            for(SelectNode node : nodes) {
                if(node.keyword == SqlKeyword.SELECT) {
                    distinct = ((SelectList) node.nodeValue).distinct;
                }

                sb.append(node.countSql(distinct)).append(SqlSyntaxConstant.LF);
            }

            return distinct ? SqlKeyword.SELECT.getValue()
                               + SqlSyntaxConstant.SPACE + SqlSyntaxConstant.COUNT_SQ
                               + SqlSyntaxConstant.SPACE + SqlKeyword.FROM.getValue()
                               + SqlSyntaxConstant.SPACE + SqlSyntaxConstant.LEFT_PARENTHESES
                               + SqlSyntaxConstant.LF + sb.toString()
                               + SqlSyntaxConstant.LF + SqlSyntaxConstant.RIGHT_PARENTHESES
                               + SqlSyntaxConstant.SPACE + "__alias__count__" + SqlSyntaxConstant.SQL_TERMINAL
                   : sb.toString();
        }

        public String pageSql() {
            boolean existsLimit = false;
            int last = nodes.size() - 1;
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < nodes.size(); i++) {
                SelectNode node = nodes.get(i);

                if(i == last) continue;

                if(node.keyword == SqlKeyword.LIMIT) {
                    existsLimit = true;
                }
                sb.append(node).append(SqlSyntaxConstant.LF);
            }
            return existsLimit ? SqlKeyword.SELECT.getValue()
                                  + SqlSyntaxConstant.SPACE + SqlSyntaxConstant.ALL_COLUMN
                                  + SqlSyntaxConstant.SPACE + SqlKeyword.FROM.getValue()
                                  + SqlSyntaxConstant.SPACE + SqlSyntaxConstant.LEFT_PARENTHESES
                                  + SqlSyntaxConstant.LF + sb.toString()
                                  + SqlSyntaxConstant.LF + SqlSyntaxConstant.RIGHT_PARENTHESES
                                  + SqlSyntaxConstant.SPACE + "__alias__page__" + SqlSyntaxConstant.SQL_TERMINAL
                   : sb.toString() + SqlSyntaxConstant.LF + SqlSyntaxConstant.PAGE_SQ + SqlSyntaxConstant.LF + SqlSyntaxConstant.SQL_TERMINAL;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int last = nodes.size() - 1;
            for(int i = 0; i < nodes.size(); i++) {
                sb.append(nodes.get(i));
                if(i != last) {
                    sb.append(SqlSyntaxConstant.LF);
                }
            }
            return sb.toString();
        }
    }

    static class SelectNode {
        static final String SEP = SqlSyntaxConstant.SPACE;
        SqlKeyword keyword;
        String nodeValueStr;
        NodeValue nodeValue;

        public SelectNode(SqlKeyword keyword, String nodeValueStr, NodeValue nodeValue) {
            this.keyword = keyword;
            this.nodeValueStr = nodeValueStr;
            this.nodeValue = nodeValue;
        }

        public String countSql(boolean distinct) {
            switch(keyword) {
                case SELECT:
                    return distinct ? keyword.getValue() + SqlSyntaxConstant.SPACE + StringUtils.trim(nodeValueStr)
                            : keyword.getValue() + SqlSyntaxConstant.SPACE + SqlSyntaxConstant.COUNT_SQ;
                case FROM:
                case WHERE:
                case GROUP_BY:
                case HAVING:
                case LIMIT:
                    return keyword.getValue() + SqlSyntaxConstant.SPACE + StringUtils.trim(nodeValueStr);
                case ORDER_BY:
                    return "";
                case TERMINAL:
                    return distinct ? "" : StringUtils.trim(nodeValueStr);
            }
            throw new IllFormedSqlException("unexpected sql keyword [" + keyword.getValue() + "]");
        }

        @Override
        public String toString() {
            return keyword.getValue() + SEP + (nodeValue != null ? nodeValue : StringUtils.trim(nodeValueStr));
        }
    }

    abstract static class NodeValue {
        @Override
        public abstract String toString();
    }

    static class LimitCondition extends NodeValue {
        String current;
        String pageSize;

        public LimitCondition(String limit_condition) {
            int idx = limit_condition.indexOf(SqlSyntaxConstant.COMMA);
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
            return current + SqlSyntaxConstant.COMMA + SqlSyntaxConstant.SPACE + pageSize;
        }
    }

    static class TableList extends NodeValue {
        static final String[] PREFIX = {SqlSyntaxConstant.FULL_JOIN_PREFIX, SqlSyntaxConstant.CROSS_JOIN_PREFIX,
                SqlSyntaxConstant.INNER_JOIN_PREFIX, SqlSyntaxConstant.LEFT_JOIN_PREFIX, SqlSyntaxConstant.RIGHT_JOIN_PREFIX};

        static final String JOIN = SqlKeyword.JOIN.getValue();

        final List<TableElement> tables = new ArrayList<>();

        public TableList(String nodeValueStr) {
            int idx = 0;
            int nc;
            String joinRl = "";
            while((nc = nextTable(nodeValueStr, idx, idx)) != -1) {
                int ji = joinType(nodeValueStr, nc);
                String originalTable = StringUtils.trim(nodeValueStr.substring(idx, ji));
                TableElement tableElement = new TableElement(originalTable, joinRl);
                tables.add(tableElement);
                idx = nc + JOIN.length();
                joinRl = StringUtils.trim(nodeValueStr.substring(ji, idx));
            }
            if(idx < nodeValueStr.length()) { //idx ~ end
                String originalTable = StringUtils.trim(nodeValueStr.substring(idx));
                TableElement tableElement = new TableElement(originalTable, joinRl);
                tables.add(tableElement);
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
                int sidx = StringUtils.lastIndexOfIgnoreCase(str, SqlSyntaxConstant.STRAIGHT_JOIN_PREFIX, idx-1);

                if(sidx > -1 && (sidx + SqlSyntaxConstant.STRAIGHT_JOIN_PREFIX.length()) == idx - 1 && SqlParserUtil.keywordLeftBound(str, sidx)) {
                    return sidx;
                } else {
                    return -1;
                }
            } else if(idx > 0 && ((c = str.charAt(idx-1)) == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f')) {
                int iidx = idx-1;
                while(iidx >= 0 && ((c = str.charAt(iidx)) == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f')) {
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
                int sidx = StringUtils.lastIndexOfIgnoreCase(str, SqlSyntaxConstant.STRAIGHT_JOIN_PREFIX, idx-1);
                return sidx > -1 && (sidx + SqlSyntaxConstant.STRAIGHT_JOIN_PREFIX.length()) == idx - 1 && SqlParserUtil.keywordLeftBound(str, sidx);

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
            int last = tables.size() - 1;
            for(int i = 0; i < tables.size(); i++) {
                sb.append(tables.get(i));
                if(i != last) {
                    sb.append(SqlSyntaxConstant.SPACE);
                }
            }
            return sb.toString();
        }
    }

    static class TableElement {
        String originalTable;

        public TableElement(String originalTable, String joinType) {
            this.originalTable = joinType + SqlSyntaxConstant.SPACE + originalTable;
        }

        @Override
        public String toString() {
            return originalTable;
        }
    }

    static class SelectList extends NodeValue {
        final List<ColumnElement> columns = new ArrayList<>();
        final boolean distinct;

        public SelectList(String nodeValueStr) {
            this.distinct = StringUtils.startsWithIgnoreCase(nodeValueStr.trim(), SqlKeyword.DISTINCT.getValue());

            int idx = 0;
            int nc;
            while((nc = nextComma(nodeValueStr, idx, idx)) != -1) {
                String originalColumn = StringUtils.trim(nodeValueStr.substring(idx, nc));
                ColumnElement columnElement = new ColumnElement(originalColumn);
                columns.add(columnElement);
                idx = nc + 1;
            }
            if(idx < nodeValueStr.length()) { //idx ~ end
                String originalColumn = StringUtils.trim(nodeValueStr.substring(idx));
                ColumnElement columnElement = new ColumnElement(originalColumn);
                columns.add(columnElement);
            }
        }

        private static int nextComma(String str, int from, int prevIdx) {
            int idx = str.indexOf(SqlSyntaxConstant.COMMA, from);
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
                    sb.append(SqlSyntaxConstant.COMMA + SqlSyntaxConstant.SPACE);
                }
            }
            return sb.toString();
        }
    }
    static class ColumnElement {
        String originalColumn;
        String columnName;
        ColumnType columnType;
        boolean as;
        String columnAlias;
        SelectSyntax subQuery;

        public ColumnElement(String originalColumn) {
            this.originalColumn = originalColumn;
        }

        @Override
        public String toString() {
            return originalColumn;
        }
    }

    public static void main(String[] argv) {
        //@link SqlSyntax:
        String sql = "select ct . `id` `i'd\",\"d'd`, 1 as '2\"(,1', 'select' as `from`, 12.34 n_q, \n" +
                "        `CONCAT` (CONCAT('-\"(,\"-', ')', ','), ct. `name`, \"',\", 'sdf\"from\"') AS `low name`, \n" +
                "        (select id from city where id in (1000101, 1000102) limit 1) as `temp`,\n" +
                "\t\t\t\t(select 'select') as no_from\n" +
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
                ";";

        SelectParser selectParser = new SelectParser().parse(sql);

        System.out.println(selectParser);

        System.out.println(selectParser.countSql());

        System.out.println(selectParser.pageSql());
    }

}
