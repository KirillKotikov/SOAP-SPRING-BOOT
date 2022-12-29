package ru.kotikov.soapspringboot.exceptions;

public class FileIsBlankException extends RuntimeException{
    public FileIsBlankException(String message) {
        super(message);
    }
}
