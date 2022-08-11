package ru.yandex.practicum.storage;

import ru.yandex.practicum.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);
}
