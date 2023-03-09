package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;

import java.util.Objects;

@RestControllerAdvice(value = "ru.yandex.practicum.filmorate.controllers")
@Slf4j
public class ExceptionController {

    @ExceptionHandler({ValidationException.class, BindException.class, DataIntegrityViolationException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final Exception e) {
        String message;
        String defaultMessage = "Неверный формат передаваемых данных: ";
        if (e.getClass().equals(DataIntegrityViolationException.class)) {
            message = defaultMessage + e.getCause();
        } else if (e instanceof BindException) {
            message = defaultMessage + Objects.requireNonNull(((BindException) e).getFieldError()).getField();
        } else if (e.getClass().equals(HttpMessageNotReadableException.class)) {
            message = defaultMessage + ((HttpMessageNotReadableException) e).getMostSpecificCause();
        } else {
            message = e.getMessage();
        }
        log.warn(message, e);
        return new ErrorResponse(message);
    }

    @ExceptionHandler({NullPointerException.class, ArgumentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final Exception e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}
