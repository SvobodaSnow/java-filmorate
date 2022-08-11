package ru.yandex.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exceptions.MissingElementException;
import ru.yandex.practicum.exceptions.ValidationException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerValidationException (final ValidationException e) {
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler({MissingElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlerMissingElementException (final MissingElementException e) {
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handlerAllException (final Exception e) {
        return Map.of("Error", e.getMessage());
    }
}
