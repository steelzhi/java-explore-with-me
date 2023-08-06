package ru.practicum.ewm.exception;

public class NoRightsToPostException extends RuntimeException {
    public NoRightsToPostException(String message) {
        super(message);
    }
}
