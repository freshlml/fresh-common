package com.fresh.common.sqlsyntax;

public class IllFormedSqlException extends RuntimeException {
    public IllFormedSqlException(String message) {
        super(message);
    }
}
