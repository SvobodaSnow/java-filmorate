package ru.yandex.practicum.exceptions;

public class MissingElementException extends RuntimeException{
    public MissingElementException (String massage) {
        super(massage);
    }
}
