package ru.yandex.practicum.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Primary
@Component
public class DirectorToFilmsDbStorage implements DirectorToFilmsStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorToFilmsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean checkDirectorToFilm(int directorId, int filmId) {
        String sqlCheck = "SELECT * FROM director_to_films WHERE film_id = ? AND director_id = ?";
        SqlRowSet directorRow = jdbcTemplate.queryForRowSet(sqlCheck, filmId, directorId);
        return directorRow.next();
    }

    @Override
    public void addDirectorToFilm(int directorId, int filmId) {
        String sql = "INSERT INTO director_to_films (director_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, directorId, filmId);
    }

    @Override
    public List<Integer> fillDirectorsListId(int id) {
        String sql = "SELECT director_id FROM director_to_films WHERE film_id = ? ORDER BY director_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getDirectorId(rs), id);
    }

    @Override
    public void deleteAllDirectorsToFilm(int filmId) {
        String sql = "DELETE FROM director_to_films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private int getDirectorId(ResultSet rs) throws SQLException {
        return rs.getInt("director_id");
    }
}
