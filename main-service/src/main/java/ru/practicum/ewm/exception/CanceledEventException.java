package ru.practicum.ewm.exception;

public class CanceledEventException extends RuntimeException {
    public CanceledEventException(String message) {
        super(message);
    }
}
