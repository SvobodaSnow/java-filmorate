package ru.yandex.practicum.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.director.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.genre.Genre;
import ru.yandex.practicum.model.mpa.Mpa;
import ru.yandex.practicum.service.film.FilmService;

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
        Film newFilm = filmService.addFilm(film);
        log.info("Запрос на добавление фильма c ID " + newFilm.getId());
        return newFilm;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        Film newFilm = filmService.updateFilm(film);
        log.info("Запрос на обновление фильма c ID " + newFilm.getId());
        return newFilm;
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        Film film = filmService.getFilmById(id);
        log.info("Фильм c ID " + id + " успешно получен");
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        Film film = filmService.addLike(id, userId);
        log.info("Лайк для фильма с ID " + id + " успешно добавлен");
        return film;
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        Film film = filmService.removeLike(id, userId);
        log.info("Лайк фильму с id = " + id + "от пользователя " + userId + " успешно удален");
        return film;
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                          @RequestParam(required = false) Integer genreId,
                                          @RequestParam(required = false) Integer year) {
        log.info("Список из " + count + " самых популярных фильмов сформирован");
        return filmService.returnFilmOrderByYearAndGenre(count, genreId, year);
    }

    @DeleteMapping("/films/{filmId}")
    public void deleteFilmById(@PathVariable int filmId) {
        filmService.deleteFilmById(filmId);
        log.info("Фильм c " + filmId + " успешно удален");
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa getMpa(@PathVariable int mpaId) {
        Mpa mpa = filmService.getMPAById(mpaId);
        log.info("Рейтинг c id = " + mpaId + " MPA успешно сформирован");
        return mpa;
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        List<Mpa> mpaAllFilms = filmService.getMpa();
        log.info("Рейтинг MPA успешно сформирован");
        return mpaAllFilms;
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenre(@PathVariable int genreId) {
        Genre genre = filmService.getGenreById(genreId);
        log.info("Жанр с id = " + genreId + " успешно сформирован");
        return genre;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        List<Genre> genres = filmService.getGenres();
        log.info("Рейтинг MPA успешно сформирован");
        return genres;
    }

    @PostMapping("/directors")
    public Director addDirector(@RequestBody Director director) {
        Director newDirector = filmService.addDirector(director);
        log.info("Режиссёр " + director.getName() +  " успешно добавлен");
        return newDirector;
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        Director newDirector = filmService.updateDirector(director);
        log.info("Режиссер с id = " + director.getId() + " успешно обновлен");
        return newDirector;
    }

    @GetMapping("/directors")
    public List<Director> getAllDirectors() {
        List<Director> directors = filmService.getDirectors();
        log.info("Список режиссёров сформирован");
        return directors;
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        Director director = filmService.getDirectorById(id);
        log.info("Получен запрос на получение режиссёра с ID " + id);
        return director;
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirecterById(@PathVariable int id) {
        filmService.deleteDirecterById(id);
        log.info("Получен запрос на удаление режиссёра с ID " + id);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        List<Film> films = filmService.getFilmsByDirector(directorId, sortBy);
        log.info("Список фильмов успешно свормирован");
        return films;
    }

    @GetMapping("/films/search")
    public List<Film> getFilmBySearch(@RequestParam String query, @RequestParam String by) {
        log.info("Получен запрос на поиск фильма");
        return filmService.getFilmsBySearch(query, by);
    }
}
