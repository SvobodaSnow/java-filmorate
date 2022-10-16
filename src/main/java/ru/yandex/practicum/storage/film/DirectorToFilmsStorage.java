package ru.yandex.practicum.storage.film;

import java.util.List;

public interface DirectorToFilmsStorage {
    boolean checkDirectorToFilm(int directorId, int filmId);

    void addDirectorToFilm(int directorId, int filmId);

    List<Integer> fillDirectorsListId(int id);

    void deleteAllDirectorsToFilm(int filmId);
}
