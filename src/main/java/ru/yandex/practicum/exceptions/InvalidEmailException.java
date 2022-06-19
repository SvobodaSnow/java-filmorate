package ru.yandex.practicum.exceptions;

public class InvalidEmailException extends Exception{
    public InvalidEmailException (String massage) {
        super(massage);
    }
}
