package ru.yandex.practicum.storage.film;

import ru.yandex.practicum.model.director.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.genre.Genre;
import ru.yandex.practicum.model.mpa.Mpa;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    Film updateRateFilm(Film film);

    void deleteFilmById(int filmId);

    List<Film> getFilmsByDirectorSortedByYear(int directorId);

    List<Film> getFilmsByDirectorSortedByLikes(int directorId);

    List<Film> getFilmOrderByYearAndGenre(Integer count, Integer genreId, Integer year);

    List<Film> getFilmOrderByYear(Integer count, Integer year);

    List<Film> getFilmOrderByGenre(Integer count, Integer genreId);

    List<Film> getFilmsSearchByDirector(String query);

    List<Film> getFilmsSearchByTitle(String query);

    List<Film> getFilmsSearchByDirectorAndTitle(String query);
}
