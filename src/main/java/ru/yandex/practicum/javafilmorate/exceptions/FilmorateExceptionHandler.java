package ru.yandex.practicum.javafilmorate.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice

public class FilmorateExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidPathVariableOrParameter(final InvalidPathVariableOrParameterException e) {

        return new ErrorResponse(e.getParam(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityDoesNotExistException(final EntityDoesNotExistException e) {

        return new ErrorResponse(e.getMessage());
    }
}
