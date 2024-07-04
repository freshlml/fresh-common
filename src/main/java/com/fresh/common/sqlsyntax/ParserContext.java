package com.fresh.common.sqlsyntax;

public class ParserContext {
    private final String sql;
    private int idx;

    public ParserContext(String sql, int idx) {
        this.sql = sql;
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getSql() {
        return sql;
    }
}
