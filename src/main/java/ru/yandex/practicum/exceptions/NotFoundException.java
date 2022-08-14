package ru.yandex.practicum.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String massage) {
        super(massage);
    }
}
