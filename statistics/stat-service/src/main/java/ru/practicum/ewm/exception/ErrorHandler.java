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
    public ApiError handleIncorrectDateRequest(final IncorrectDateException e) {
        return new ApiError(e.getStackTrace(),
                e.getMessage(),
                "Введен некорректный диапазон дат",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }


}