package ru.yandex.practicum.exceptions;

public class InvalidReleaseDateException extends Exception{
    public InvalidReleaseDateException (String massage) {
        super(massage);
    }
}
