package com.petprj.exeptions;

public class DatabaseException extends RuntimeException {

    public DatabaseException(Throwable cause) {
        super("Ошибка базы данных", cause);
    }
}
