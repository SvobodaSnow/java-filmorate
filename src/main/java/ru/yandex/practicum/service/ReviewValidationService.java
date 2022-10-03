package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Reviews;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.storage.FilmStorage;
import ru.yandex.practicum.storage.UserStorage;

@Slf4j
@Service
public class ReviewValidationService {
    @Autowired
    UserStorage userStorage;
    @Autowired
    FilmStorage filmStorage;

    public void checkReviews(Reviews reviews) {
        userStorage.getUserById(reviews.getUserId());
        filmStorage.getFilmById(reviews.getFilmId());

        if (reviews.getContent().isBlank()) {
            throw new ValidationException("Текст отзыва отсутствует");
        }
    }

    public void checkFilmId(int filmId) {
        filmStorage.getFilmById(filmId);
    }

    public void checkUserId(int userId) {
        userStorage.getUserById(userId);
    }
}
