package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.exceptions.*;
import ru.yandex.practicum.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
public class FilmController {
    private List<Film> films = new ArrayList<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Получен запрос на получение списка всех фильмов");
        return films;
    }

    @PostMapping("/films")
    public Film crete(@RequestBody Film film) throws
            InvalidNameException, ExceedLengthDescriptionException,
            InvalidReleaseDateException, InvalidDurationException{
        log.info("Получен запрос на добавление фильма");
        if (film.getName() == null) {
            log.debug("Получен запрос на добаление фильма без названия");
            throw new InvalidNameException("Имя фильма не указано");
        }
        if (film.getName().isBlank()) {
            log.debug("Получен запрос на добаление фильма без названия");
            throw new InvalidNameException("Имя фильма не указано");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Получен запрос на добавление фильма, описание которого больше 200 символов");
            throw new ExceedLengthDescriptionException("Превышена длина описания");
        }
        LocalDate localDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(localDate) || film.getReleaseDate() == null){
            log.debug("Получен запрос на добавление фильма раньше 28 декабря 1895 года");
            throw new InvalidReleaseDateException("Не верная дата выпуска фильма");
        }
        if (film.getDuration() <= 0) {
            log.debug("Получен запрос на добавление фильма с отрицательной продолжительностью");
            throw new InvalidDurationException("Продолжительность должна быть больше нуля");
        }
        film.setId(films.size() + 1);
        films.add(film);
        log.info("Фильм добавлен");
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) throws
            InvalidNameException, ExceedLengthDescriptionException,
            InvalidReleaseDateException, InvalidDurationException {
        log.info("Получен запрос на обновление фильма");
        if (film.getName() == null) {
            log.debug("Получен запрос на добаление фильма без названия");
            throw new InvalidNameException("Имя фильма не указано");
        }
        if (film.getName().isBlank()) {
            log.debug("Получен запрос на обновление фильма без названия");
            throw new InvalidNameException("Имя фильма не указано");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Получен запрос на обновление фильма, описание которого больше 200 символов");
            throw new ExceedLengthDescriptionException("Превышена длина описания");
        }
        LocalDate localDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(localDate) || film.getReleaseDate() == null){
            log.debug("Получен запрос на обновление фильма раньше 28 декабря 1895 года");
            throw new InvalidReleaseDateException("Не верная дата выпуска фильма");
        }
        if (film.getDuration() <= 0) {
            log.debug("Получен запрос на обновление фильма с отрицательной продолжительностью");
            throw new InvalidDurationException("Продолжительность должна быть больше нуля");
        }
        films.set(film.getId() - 1, film);
        log.info("Фильм обновлен");
        return film;
    }
}
