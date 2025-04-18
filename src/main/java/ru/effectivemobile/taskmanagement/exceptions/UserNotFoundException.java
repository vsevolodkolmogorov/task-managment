package ru.effectivemobile.taskmanagement.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String text) {
        super(text);
    }
}
