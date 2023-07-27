package ru.practicum.ewm.exception;

public class ParticipantLimitAchievedException extends RuntimeException {
    public ParticipantLimitAchievedException(String message) {
        super(message);
    }
}
