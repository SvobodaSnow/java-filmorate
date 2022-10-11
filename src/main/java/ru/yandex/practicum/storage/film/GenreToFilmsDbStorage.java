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
public class GenreToFilmsDbStorage implements GenreToFilmsStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreToFilmsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean checkGenreToFilm(int genreId, int filmId) {
        String sqlChek = "SELECT * FROM genre_to_films WHERE film_id = ? AND genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlChek, filmId, genreId);
        return genreRow.next();
    }

    @Override
    public void addGenreToFilm(int genreId, int filmId) {
        String sql = "INSERT INTO genre_to_films (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public List<Integer> fillGenresListId(int id) {
        String sql = "SELECT genre_id FROM genre_to_films WHERE film_id = ? ORDER BY genre_id";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getGenreId(rs),
                id
        );
    }

    @Override
    public void deleteAllGenresToFilm(int filmId) {
        String sql = "DELETE FROM genre_to_films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private int getGenreId(ResultSet rs) throws SQLException {
        return rs.getInt("genre_id");
    }
}
