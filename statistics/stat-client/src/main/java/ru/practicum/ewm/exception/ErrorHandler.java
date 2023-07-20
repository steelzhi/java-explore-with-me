package ru.practicum.ewm.exception;


import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

/*    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePostNotFoundException(final UserAlreadyExistException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }*/
}