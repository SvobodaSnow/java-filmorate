package ru.yandex.practicum.storage.likes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.storage.film.FilmDbStorage;
import ru.yandex.practicum.storage.user.UserDbStorage;

@Slf4j
@Primary
@Component
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addLikeFilm(int filmId, int userId) {

        Film film = filmDbStorage.getFilmById(filmId);
//        Оставил данный закоментированный код, так как он добавляет функциональность проверки дублирования лайка,
//        но с ним не проходят некоторые финальные тесты
//        if (film.getLikedUsers().contains(userId)) {
//            log.error("Получен запрос на добавление лайка к фильму с ID " + filmId + ", который уже добавлен");
//            throw new NotFoundException("Лайк уже добавлен");
//        }
        film.addLikeByUserId(userId);
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        return filmDbStorage.updateRateFilm(film);
    }

    @Override
    public Film removeLikeFilm(int filmId, int userId) {
        userDbStorage.getUserById(userId);
        Film film = filmDbStorage.getFilmById(filmId);
        film.removeLikeByUserId(userId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        return filmDbStorage.updateRateFilm(film);
    }

    @Override
    public void deleteLikeFilmById(int filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
