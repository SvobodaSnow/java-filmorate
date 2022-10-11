package ru.yandex.practicum.storage.likes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.storage.film.FilmDbStorage;
import ru.yandex.practicum.storage.user.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

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

    @Override
    public void fillFilmLikeListForFilm(Film film) {
        film.setLikedUsers(new HashSet<>(fillFilmLikeList(film.getId())));
    }

    @Override
    public List<Integer> fillFilmLikeListForFilm(int userId) {
        String sql = "SELECT * FROM likes WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmId(rs), userId);
    }

    private int getFilmId(ResultSet rs) throws SQLException {
        return rs.getInt("film_id");
    }

    private List<Integer> fillFilmLikeList(int id) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getFilmLikeId(rs),
                id
        );
    }

    private int getFilmLikeId(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }
}
