package ru.yandex.practicum.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String massage) {
        super(massage);
    }
}
