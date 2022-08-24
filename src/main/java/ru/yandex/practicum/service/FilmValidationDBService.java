package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.NotFoundException;

@Slf4j
@Service
public class FilmValidationDBService {
    private final JdbcTemplate jdbcTemplate;

    public FilmValidationDBService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void checkIdDatabase(int id) {
        String sql = "SELECT name FROM films WHERE film_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if(!userRows.next()) {
            log.error("Получен запрос на обновление фильма с ID " + id + ", которого нет в базе данных");
            throw new NotFoundException("Фильма нет в списке");
        }
    }

    public void checkLikeInDatabase(int filmId, int userId) {
        String sqlChek = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlChek, filmId, userId);
        if (genreRow.next()) {
            log.error("Получен запрос на добавление лайка к фильму с ID " + filmId + ", который уже добавлен");
            throw new NotFoundException("Лайк уже добавлен");
        }
    }
}
