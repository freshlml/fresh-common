package com.fresh.core.sqlsyntax;

import java.util.ArrayList;
import java.util.List;

public class ParserContext {
    private final String sql;
    private int idx;
    private final List<SelectParser.SelectNode> nodes = new ArrayList<>();
    private boolean distinct = false;
    private boolean existsLimit = false;
    private boolean existsGroupBy = false;

    public ParserContext(String sql, int idx) {
        this.sql = sql;
        this.idx = idx;
    }

    public void add(SelectParser.SelectNode selectNode) {
        this.nodes.add(selectNode);
    }

    public SelectParser.SelectSyntax ofSelectSyntax() {
        return new SelectParser.SelectSyntax(this.nodes, distinct, existsLimit, existsGroupBy);
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public void setExistsLimit(boolean existsLimit) {
        this.existsLimit = existsLimit;
    }

    public void setExistsGroupBy(boolean existsGroupBy) {
        this.existsGroupBy = existsGroupBy;
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
