package ru.kotikov.soapspringboot.exceptions.db;

public class DbReadException extends Exception {
    public DbReadException(String message) {
        super(message);
    }
}