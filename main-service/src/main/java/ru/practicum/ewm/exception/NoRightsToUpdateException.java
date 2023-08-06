package ru.practicum.ewm.exception;

public class NoRightsToUpdateException extends RuntimeException {
    public NoRightsToUpdateException(String message) {
        super(message);
    }
}
