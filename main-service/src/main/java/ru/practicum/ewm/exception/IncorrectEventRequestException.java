package ru.practicum.ewm.exception;

public class IncorrectEventRequestException extends RuntimeException {
    public IncorrectEventRequestException(String message) {
        super(message);
    }
}
