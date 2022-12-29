package ru.kotikov.soapspringboot.exceptions.db;

public class EmptyResultException extends RuntimeException {

    public EmptyResultException(String message) {
        super(message);
    }

}
