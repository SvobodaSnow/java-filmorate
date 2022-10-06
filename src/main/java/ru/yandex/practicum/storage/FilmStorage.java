package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    Film addLikeFilm(int filmId, int userId);

    Film removeLikeFilm(int filmId, int userId);

    Film updateRateFilm(Film film);

    Mpa getMpaById(int id);

    List<Mpa> getMpa();

    Genre getGenreById(int id);

    List<Genre> getGenres();

    void deleteFilmById(int filmId);

    void deleteLikeFilmById(int filmId);
}
