package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.service.FilmValidationService;
import ru.yandex.practicum.service.IdGenerator;

import java.util.*;


@Slf4j
@RestController
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();
    private final FilmValidationService filmValidationService = new FilmValidationService();

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Получен запрос на получение списка всех фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film crete(@RequestBody Film film) {
        log.info("Получен запрос на добавление фильма");
        filmValidationService.checkFilmName(film);
        filmValidationService.checkLengthDescription(film);
        filmValidationService.checkDateCreationFilm(film);
        filmValidationService.checkFilmDuration(film);
        film.setId(idGenerator.generate());
        films.put(film.getId(), film);
        log.info("Фильм добавлен c ID " + film.getId());
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма");
        filmValidationService.checkMovieAvailability(films, film);
        filmValidationService.checkFilmName(film);
        filmValidationService.checkLengthDescription(film);
        filmValidationService.checkDateCreationFilm(film);
        filmValidationService.checkFilmDuration(film);
        films.put(film.getId(), film);
        log.info("Фильм c ID " + film.getId() + " обновлен");
        return film;
    }
}
