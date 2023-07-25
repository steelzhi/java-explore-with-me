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

}