package ru.practicum.ewm.exception;

public class IncorrectCategoryRequestException extends RuntimeException {
    public IncorrectCategoryRequestException(String message) {
        super(message);
    }
}
