package ru.yandex.practicum.exceptions;

public class FilmAlreadyExistException extends Exception {
    public FilmAlreadyExistException(String massage) {
        super(massage);
    }
}
