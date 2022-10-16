package ru.yandex.practicum.storage.genre;

import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.genre.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenreById(int id);

    List<Genre> getGenres();

    void fillGenresForFilm(Film film);
}
