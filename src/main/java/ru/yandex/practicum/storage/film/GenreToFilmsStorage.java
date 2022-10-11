package ru.yandex.practicum.storage.film;

import java.util.List;

public interface GenreToFilmsStorage {
    boolean checkGenreToFilm(int genreId, int filmId);

    void addGenreToFilm(int genreId, int filmId);

    List<Integer> fillGenresListId(int id);

    void deleteAllGenresToFilm(int filmId);
}
