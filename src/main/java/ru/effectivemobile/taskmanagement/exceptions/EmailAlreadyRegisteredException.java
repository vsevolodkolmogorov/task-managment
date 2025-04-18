package ru.effectivemobile.taskmanagement.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String text) {
        super(text);
    }
}
