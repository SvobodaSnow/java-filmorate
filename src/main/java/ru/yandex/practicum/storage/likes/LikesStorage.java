package ru.yandex.practicum.storage.likes;

import ru.yandex.practicum.model.film.Film;

public interface LikesStorage {
    Film addLikeFilm(int filmId, int userId);

    Film removeLikeFilm(int filmId, int userId);

    void deleteLikeFilmById(int filmId);
}
