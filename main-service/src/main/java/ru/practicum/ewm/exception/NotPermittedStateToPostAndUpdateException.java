package ru.practicum.ewm.exception;

public class NotPermittedStateToPostAndUpdateException extends RuntimeException {
    public NotPermittedStateToPostAndUpdateException(String message) {
        super(message);
    }
}
