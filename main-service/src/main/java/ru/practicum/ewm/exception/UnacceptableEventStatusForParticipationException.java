package ru.practicum.ewm.exception;

public class UnacceptableEventStatusForParticipationException extends RuntimeException {
    public UnacceptableEventStatusForParticipationException(String message) {
        super(message);
    }
}
