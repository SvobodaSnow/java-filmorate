package ru.yandex.practicum.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.model.director.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.genre.Genre;
import ru.yandex.practicum.service.film.FilmValidationService;
import ru.yandex.practicum.storage.director.DirectorStorage;
import ru.yandex.practicum.storage.genre.GenreStorage;
import ru.yandex.practicum.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Primary
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private FilmValidationService filmValidationService;
    @Autowired
    private MpaStorage mpaStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
    }

    @Override
    public Film addFilm(Film film) {
        checkFilmValidation(film);
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        String sqlId = "SELECT film_id FROM films ORDER BY film_id DESC LIMIT 1";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlId);
        if (filmRow.next()) {
            film.setId(filmRow.getInt("film_id"));
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmValidation(film);
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        return film;
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sql, id);
        Film film = new Film();
        if (filmRow.next()) {
            film.setId(id);
            film.setName(filmRow.getString("name"));
            film.setDescription(filmRow.getString("description"));
            film.setReleaseDate(filmRow.getDate("release_date").toLocalDate());
            film.setDuration(filmRow.getInt("duration"));
            film.setRate(filmRow.getInt("rate"));
            film.setMpa(mpaStorage.getMpaById(filmRow.getInt("rating_id")));
        } else {
            log.error("Получен запрос на обновление фильма с ID " + id + ", которого нет в базе данных");
            throw new NotFoundException("Фильма нет в списке");
        }
        return film;
    }

    @Override
    public Film updateRateFilm(Film film) {
        String sql = "UPDATE films SET rate = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getRate(), film.getId());
        return film;
    }

    @Override
    public void deleteFilmById(int filmId) {
        getFilmById(filmId);
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> getFilmOrderByYearAndGenre(Integer count, Integer genreId, Integer year) {
        String sql = "SELECT films.film_id AS film_id, " +
                "films.name AS name, " +
                "description, " +
                "release_date, " +
                "duration, " +
                "rate, " +
                "films.rating_id AS rating_id, " +
                "COUNT(user_id) + rate AS cnt " +
                "FROM films LEFT JOIN likes L ON films.film_id = L.film_id " +
                "LEFT JOIN genre_to_films GF ON films.film_id = GF.film_id " +
                "INNER JOIN genre G ON G.genre_id = GF.genre_id " +
                "WHERE YEAR(films.release_date) = ? AND GF.genre_id = ? " +
                "GROUP BY films.film_id, films.name, description, release_date, duration, films.rating_id, rate " +
                "ORDER BY cnt DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), year, genreId, count);
    }

    @Override
    public List<Film> getFilmOrderByYear(Integer count, Integer year) {
        String sql = "SELECT films.film_id AS film_id, " +
                "films.name AS name, " +
                "description, " +
                "release_date, " +
                "duration, " +
                "rate, " +
                "films.rating_id AS rating_id, " +
                "COUNT(user_id) + rate as cnt " +
                "FROM films LEFT JOIN likes L ON films.film_id = L.film_id " +
                "LEFT JOIN genre_to_films GF ON films.film_id = GF.film_id " +
                "INNER JOIN genre G ON G.genre_id = GF.genre_id " +
                "WHERE YEAR(films.release_date) = ? " +
                "GROUP BY films.film_id, films.name, description, release_date, duration, films.rating_id, rate " +
                "ORDER BY cnt DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), year,  count);
    }

    @Override
    public List<Film> getFilmOrderByGenre(Integer count, Integer genreId) {
        String sql = "SELECT films.film_id AS film_id, " +
                "films.name AS name, " +
                "description, " +
                "release_date, " +
                "duration, " +
                "rate, " +
                "films.rating_id AS rating_id, " +
                "COUNT(user_id) + rate AS cnt " +
                "FROM films LEFT JOIN likes L ON films.film_id = L.film_id " +
                "LEFT JOIN genre_to_films GF ON films.film_id = GF.film_id " +
                "INNER JOIN genre G ON G.genre_id = GF.genre_id " +
                "WHERE GF.GENRE_ID = ? " +
                "GROUP BY films.film_id, films.name, description, release_date, duration, films.rating_id, rate " +
                "ORDER BY cnt DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), genreId,  count);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(int directorId) {
        String sql = "SELECT f.* " +
                     "FROM directors d " +
                     "JOIN director_to_films dtf ON d.director_id = dtf.director_id " +
                     "JOIN films f ON dtf.film_id = f.film_id " +
                     "WHERE d.director_id = ? " +
                     "ORDER BY release_date";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(int directorId) {
        String sql = "SELECT f.* " +
                "FROM directors d " +
                "JOIN director_to_films dtf ON d.director_id = dtf.director_id " +
                "JOIN films f ON dtf.film_id = f.film_id " +
                "WHERE d.director_id = ? " +
                "ORDER BY rate";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), directorId);
    }

    @Override
    public List<Film> getFilmsSearchByDirector(String query) {
        String sql = "SELECT f.*, d.name AS director_name " +
                "FROM directors d " +
                "JOIN director_to_films dtf ON d.director_id = dtf.director_id " +
                "JOIN films f ON dtf.film_id = f.film_id " +
                "WHERE LOWER(d.name) LIKE LOWER('%" + query + "%') " +
                "ORDER BY rate DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
    }

    @Override
    public List<Film> getFilmsSearchByTitle(String query) {
        String sql = "SELECT * FROM films WHERE LOWER(name) LIKE LOWER('%" + query + "%') ORDER BY rate DESC";
        log.info(sql);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
    }

    @Override
    public List<Film> getFilmsSearchByDirectorAndTitle(String query) {
        String sql = "SELECT f.*, d.name AS director_name " +
                "FROM directors d " +
                "RIGHT JOIN director_to_films dtf ON d.director_id = dtf.director_id " +
                "RIGHT JOIN films f ON dtf.film_id = f.film_id " +
                "WHERE LOWER(d.name) LIKE LOWER('%" + query + "%') OR LOWER(f.name) LIKE LOWER('%" + query + "%') " +
                "ORDER BY rate DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
    }

    private Film makeFilms(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
        film.setMpa(mpaStorage.getMpaById(rs.getInt("rating_id")));
        return film;
    }

    private void checkFilmValidation(Film film) {
        filmValidationService.checkFilmName(film);
        filmValidationService.checkLengthDescription(film);
        filmValidationService.checkDateCreationFilm(film);
        filmValidationService.checkFilmDuration(film);
        filmValidationService.checkMPAAvailability(film);
    }
}
