package ru.yandex.practicum.exceptions;

public class ExceedLengthDescriptionException extends Exception {
    public ExceedLengthDescriptionException(String massage) {
        super(massage);
    }
}
