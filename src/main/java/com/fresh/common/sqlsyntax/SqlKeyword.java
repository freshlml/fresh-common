package com.fresh.common.sqlsyntax;

public enum SqlKeyword {
    SELECT("SELECT"),
    DISTINCT("DISTINCT"),
    DISTINCTROW("DISTINCTROW"),
    FROM("FROM"),
    ON("ON"),
    WHERE("WHERE"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    LIMIT("LIMIT"),
    AS("AS"),
    JOIN("JOIN"),
    FULL_JOIN("FULL JOIN"),
    CROSS_JOIN("CROSS JOIN"),
    LEFT_JOIN("LEFT JOIN"),
    LEFT_OUTER_JOIN("LEFT OUTER JOIN"),
    RIGHT_JOIN("RIGHT JOIN"),
    RIGHT_OUTER_JOIN("RIGHT OUTER JOIN"),
    INNER_JOIN("INNER JOIN"),
    STRAIGHT_JOIN("STRAIGHT_JOIN"),
    AND("AND"),
    OR("OR"),
    IN("IN"),
    NOT("NOT"),
    IS_NULL("IS NULL"),
    LIKE("LIKE"),
    REGEXP("REGEXP"),
    DESC("DESC"),
    ASC("ASC"),
    TERMINAL("");

    private String value;
    SqlKeyword(String keyword) {
        this.value = keyword;
    }
    public String getValue() {
        return value;
    }
}
