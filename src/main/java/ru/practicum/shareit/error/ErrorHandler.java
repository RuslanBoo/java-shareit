package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.error.model.ErrorResponse;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException badRequestException) {
        log.error("Error code: 400.", badRequestException);
        return new ErrorResponse(badRequestException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException methodArgumentNotValidException) {
        FieldError error = methodArgumentNotValidException.getBindingResult().getFieldErrors().get(0);
        log.error("Error code: 400. " + error.getDefaultMessage(), error.getDefaultMessage());
        return new ErrorResponse(error.getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFoundException(final DataNotFoundException dataNotFoundException) {
        log.error("Error code: 404.", dataNotFoundException);
        return new ErrorResponse(dataNotFoundException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException entityNotFoundException) {
        log.error("Error code: 404.", entityNotFoundException);
        return new ErrorResponse(entityNotFoundException.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException conflictException) {
        log.error("Error code: 409.", conflictException);
        return new ErrorResponse(conflictException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final Throwable throwable) {
        log.error("Error code: 500.", throwable);
        return new ErrorResponse(throwable.getMessage());
    }

}
