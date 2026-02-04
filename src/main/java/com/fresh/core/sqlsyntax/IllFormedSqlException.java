package com.fresh.core.sqlsyntax;

public class IllFormedSqlException extends RuntimeException {
    public IllFormedSqlException(String message) {
        super(message);
    }
}
