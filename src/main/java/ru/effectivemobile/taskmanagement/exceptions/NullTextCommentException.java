package ru.effectivemobile.taskmanagement.exceptions;

public class NullTextCommentException extends RuntimeException {
    public NullTextCommentException(String text) {
            super(text);
    }
}
