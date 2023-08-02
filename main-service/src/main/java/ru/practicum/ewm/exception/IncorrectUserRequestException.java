package ru.practicum.ewm.exception;

public class IncorrectUserRequestException extends RuntimeException {
    public IncorrectUserRequestException(String message) {
        super(message);
    }
}
