package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectUserRequest(final IncorrectUserRequestException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "У добавляемого пользователя заданы некорректные данные",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleViolationUserRestrictions(final ViolationUserRestrictionsException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Нарушение целостности данных",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFound(final UserNotFoundException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectCategoryRequest(final IncorrectCategoryRequestException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "У добавляемого пользователя заданы некорректные данные",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotFound(final CategoryNotFoundException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicateCategoryName(final DuplicateCategoryNameException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Категория с таким именем уже существует",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectEventRequest(final IncorrectEventRequestException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Некорректная дата начала события",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlePatchRestriction(final PatchRestrictionException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Нельзя вносить изменения в уже опубликованные события",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleParticipationRequestNotFound(final ParticipationRequestNotFoundException e, long userId) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Request with id= " + userId + " was not found",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleParticipantLimitAchieved(final ParticipantLimitAchievedException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Уже достигнут лимит одобренных заявок на мероприятие",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleViolationParticipationRequestStatus(final ViolationParticipationRequestStatusException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Можно изменять статус только у заявок, находящихся в состоянии ожидания",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectParams(final IncorrectParamsException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Введены некорректные параметры запроса",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicateParticipationRequest(final DuplicateParticipationRequestException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Нельзя отправлять повторный запрос от того же пользователя на то же событие",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRequesterMatchesInitiator(final RequesterMatchesInitiatorException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Инициатор события не может участвовать в собственном событии",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUnacceptableEventStatusForParticipation(final UnacceptableEventStatusForParticipationException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Нельзя принять участие в еще не опубликованном событии",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

}