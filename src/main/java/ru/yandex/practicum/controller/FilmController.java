package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.service.FilmService;

import java.util.*;

@Slf4j
@RestController
public class FilmController {
    @Autowired
    private FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAll() {
        log.info("Получен запрос на получение списка всех фильмов");
        return filmService.findAll();
    }

    @PostMapping("/films")
    public Film crete(@RequestBody Film film) {
        log.info("Получен запрос на добавление фильма");
        Film newFilm = filmService.addFilm(film);
        log.info("Фильм добавлен c ID " + newFilm.getId());
        return newFilm;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма");
        Film newFilm = filmService.updateFilm(film);
        log.info("Фильм c ID " + newFilm.getId() + " обновлен");
        return newFilm;
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Получен запрос на получение вильма с ID " + id);
        Film film = filmService.getFilmById(id);
        log.info("Фильм c ID " + id + " успешно получен");
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на дабавление лайка");
        Film film = filmService.addLike(id, userId);
        log.info("Количество лайков для фильма с ID " + id + " успешно увеличено");
        return film;
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос на удаление лайка");
        Film film = filmService.removeLike(id, userId);
        log.info("Лайк успешно удален");
        return film;
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                          @RequestParam(required = false) Integer genreId,
                                          @RequestParam(required = false) Integer year) {
        log.info("Получен запрос на получение " + count + " самых популярных фильма");
        log.info("Список самых популярных фильмов сформирован");
        return filmService.returnFilmOrderByYearAndGenre(count, genreId, year);
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa getMpa(@PathVariable int mpaId) {
        log.info("Получен запрос на получение рейтинга MPA с ID " + mpaId);
        Mpa mpa = filmService.getMPAById(mpaId);
        log.info("Рейтинг MPA успешно сформирован");
        return mpa;
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        log.info("Получен запрос на получение рейтинга MPA");
        List<Mpa> mpaAllFilms = filmService.getMpa();
        log.info("Рейтинг MPA успешно сформирован");
        return mpaAllFilms;
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenre(@PathVariable int genreId) {
        log.info("Получен запрос на получение жанра с ID " + genreId);
        Genre genre = filmService.getGenreById(genreId);
        log.info("Список жанров успешно сформирован");
        return genre;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение жанров для всех фильмов");
        List<Genre> genres = filmService.getGenres();
        log.info("Рейтинг MPA успешно сформирован");
        return genres;
    }
}
