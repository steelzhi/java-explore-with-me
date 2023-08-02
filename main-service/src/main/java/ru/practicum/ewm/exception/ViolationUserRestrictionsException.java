package ru.practicum.ewm.exception;

public class ViolationUserRestrictionsException extends RuntimeException {
    public ViolationUserRestrictionsException(String message) {
        super(message);
    }
}
