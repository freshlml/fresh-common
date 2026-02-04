package com.fresh.core.sqlsyntax;

public enum SqlKeyword {
    SELECT("SELECT"),
    DISTINCT("DISTINCT"),
    FROM("FROM"),
    WHERE("WHERE"),
    GROUP("GROUP"),
    BY("BY"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER("ORDER"),
    ORDER_BY("ORDER BY"),
    LIMIT("LIMIT"),
    JOIN("JOIN"),
    TERMINAL("");

    private String value;
    SqlKeyword(String keyword) {
        this.value = keyword;
    }
    public String getValue() {
        return value;
    }
}
