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
    private GenreStorage genreStorage;
    @Autowired
    private DirectorStorage directorStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmValidationService filmValidationService, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmValidationService = filmValidationService;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
            fillDirectorsForFilm(film);
        }
        return films;
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

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                addGenre(genre, film.getId());
            }
        }

        Set<Director> directors = film.getDirectors();
        if (directors != null) {
            for (Director director : directors) {
                addDirectorForFilm(director, film.getId());
            }
        }

        return getFilmById(film.getId());
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

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            deleteGenre(film.getId());
            for (Genre genre : genres) {
                addGenre(genre, film.getId());
            }
        }

        Set<Director> directors = film.getDirectors();
        if (directors != null) {
            deleteDirector(film.getId());
            for (Director director : directors) {
                addDirectorForFilm(director, film.getId());
            }
        }

        return getFilmById(film.getId());
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
        fillFilmLikeListForFilm(film);
        fillGenresForFilm(film);
        fillDirectorsForFilm(film);
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
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), year, genreId, count);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
        }
        return films;
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
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), year,  count);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
        }
        return films;
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
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), genreId,  count);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(int directorId) {
        String sql = "SELECT f.* " +
                     "FROM DIRECTORS d " +
                     "JOIN DIRECTOR_TO_FILMS dtf ON d.DIRECTOR_ID = dtf.DIRECTOR_ID " +
                     "JOIN FILMS f ON dtf.FILM_ID = f.FILM_ID " +
                     "WHERE d.DIRECTOR_ID = ? " +
                     "ORDER BY release_date";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), directorId);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
            fillDirectorsForFilm(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(int directorId) {
        String sql = "SELECT f.* " +
                "FROM DIRECTORS d " +
                "JOIN DIRECTOR_TO_FILMS dtf ON d.DIRECTOR_ID = dtf.DIRECTOR_ID " +
                "JOIN FILMS f ON dtf.FILM_ID = f.FILM_ID " +
                "WHERE d.DIRECTOR_ID = ? " +
                "ORDER BY rate";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), directorId);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
            fillDirectorsForFilm(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmsSearchByDirector(String query) {
        String sql = "SELECT f.*, d.name AS director_name " +
                "FROM DIRECTORS d " +
                "JOIN DIRECTOR_TO_FILMS dtf ON d.DIRECTOR_ID = dtf.DIRECTOR_ID " +
                "JOIN FILMS f ON dtf.FILM_ID = f.FILM_ID " +
                "WHERE LOWER(d.name) LIKE LOWER('%" + query + "%') " +
                "ORDER BY rate DESC";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
            fillDirectorsForFilm(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmsSearchByTitle(String query) {
        String sql = "SELECT * FROM films WHERE LOWER(name) LIKE LOWER('%" + query + "%') ORDER BY rate DESC";
        log.info(sql);
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
            fillDirectorsForFilm(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmsSearchByDirectorAndTitle(String query) {
        String sql = "SELECT f.*, d.name AS director_name " +
                "FROM DIRECTORS d " +
                "RIGHT JOIN DIRECTOR_TO_FILMS dtf ON d.DIRECTOR_ID = dtf.DIRECTOR_ID " +
                "RIGHT JOIN FILMS f ON dtf.FILM_ID = f.FILM_ID " +
                "WHERE LOWER(d.name) LIKE LOWER('%" + query + "%') OR LOWER(f.name) LIKE LOWER('%" + query + "%') " +
                "ORDER BY rate DESC";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs));
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
            fillDirectorsForFilm(film);
        }
        return films;
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

    private void fillFilmLikeListForFilm(Film film) {
        film.setLikedUsers(new HashSet<>(fillFilmLikeList(film.getId())));
    }

    private List<Integer> fillGenresListId(int id) {
        String sql = "SELECT genre_id FROM genre_to_films WHERE film_id = ? ORDER BY genre_id";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> getGenreId(rs),
                id
        );
    }

    private List<Integer> fillDirectorsListId(int id) {
        String sql = "SELECT director_id FROM director_to_films WHERE film_id = ? ORDER BY director_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getDirectorId(rs), id);
    }

    private int getGenreId(ResultSet rs) throws SQLException {
        return rs.getInt("genre_id");
    }

    private int getDirectorId(ResultSet rs) throws SQLException {
        return rs.getInt("director_id");
    }

    private void fillGenresForFilm(Film film) {
        Set<Genre> genresList = new HashSet<>();
        for (Integer genreId : fillGenresListId(film.getId())) {
            genresList.add(genreStorage.getGenreById(genreId));
        }
        film.setGenres(genresList);
    }

    private void fillDirectorsForFilm(Film film) {
        Set<Director> directors = new HashSet<>();
        for (Integer directorId : fillDirectorsListId(film.getId())) {
            directors.add(directorStorage.getDirectorById(directorId));
        }
        film.setDirectors(directors);
    }

    private void checkFilmValidation(Film film) {
        filmValidationService.checkFilmName(film);
        filmValidationService.checkLengthDescription(film);
        filmValidationService.checkDateCreationFilm(film);
        filmValidationService.checkFilmDuration(film);
        filmValidationService.checkMPAAvailability(film);
    }

    private void addGenre(Genre genre, int filmId) {
        String sqlChek = "SELECT * FROM genre_to_films WHERE film_id = ? AND genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlChek, filmId, genre.getId());
        if (genreRow.next()) {
            return;
        }
        String sql = "INSERT INTO genre_to_films (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genre.getId());
    }

    private void addDirectorForFilm(Director director, int filmId) {
        String sqlCheck = "SELECT * FROM director_to_films WHERE film_id = ? AND director_id = ?";
        SqlRowSet directorRow = jdbcTemplate.queryForRowSet(sqlCheck, filmId, director.getId());
        if (directorRow.next()) {
            return;
        }
        String sql = "INSERT INTO director_to_films (director_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, director.getId(), filmId);
    }

    private void deleteGenre(int id) {
        String sql = "DELETE FROM genre_to_films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private void deleteDirector(int id) {
        String sql = "DELETE FROM director_to_films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
