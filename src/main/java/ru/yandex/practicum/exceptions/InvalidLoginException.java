package ru.yandex.practicum.exceptions;

public class InvalidLoginException extends Exception {
    public InvalidLoginException(String massage) {
        super(massage);
    }
}
