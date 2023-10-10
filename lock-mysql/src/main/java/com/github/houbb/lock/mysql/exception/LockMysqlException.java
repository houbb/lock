package com.github.houbb.lock.mysql.exception;

public class LockMysqlException extends RuntimeException {

    public LockMysqlException() {
    }

    public LockMysqlException(String message) {
        super(message);
    }

    public LockMysqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockMysqlException(Throwable cause) {
        super(cause);
    }

    public LockMysqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
