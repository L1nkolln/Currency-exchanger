package com.petprj.exeptions;

public class DatabaseCreateException extends RuntimeException {
    public DatabaseCreateException(String message) {
        super(message);
    }
}
