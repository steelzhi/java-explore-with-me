package ru.practicum.ewm.exception;

public class DuplicateParticipationRequestException extends RuntimeException {
    public DuplicateParticipationRequestException(String message) {
        super(message);
    }
}
