package com.projecki.economy.store;

public class DatabaseException extends Exception {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(Exception cause) {
        super(cause);
    }

}
