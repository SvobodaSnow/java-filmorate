package ru.yandex.practicum.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.model.Film;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.service.FilmValidationService;

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
    private UserDbStorage userDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            film.setMpa(getMpaById(filmRow.getInt("rating_id")));
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
    public Film addLikeFilm(int filmId, int userId) {

        Film film = getFilmById(filmId);
        if (film.getLikedUsers().contains(userId)) {
            log.error("Получен запрос на добавление лайка к фильму с ID " + filmId + ", который уже добавлен");
            throw new NotFoundException("Лайк уже добавлен");
        }
        film.addLikeByUserId(userId);
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        return updateRateFilm(film);
    }

    @Override
    public Film removeLikeFilm(int filmId, int userId) {
        userDbStorage.getUserById(userId);
        Film film = getFilmById(filmId);
        film.removeLikeByUserId(userId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        return updateRateFilm(film);
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
    public void deleteLikeFilmById(int filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT name FROM mpa WHERE rating_id = ?";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sql, id);
        if (mpaRow.next()) {
            Mpa mpa = new Mpa();
            mpa.setName(mpaRow.getString("name"));
            mpa.setId(id);
            return mpa;
        } else {
            throw new NotFoundException("Рейтинг MPA не найден");
        }
    }

    @Override
    public List<Mpa> getMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT name FROM genre WHERE genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sql, id);
        if (genreRow.next()) {
            Genre genre = new Genre();
            genre.setName(genreRow.getString("name"));
            genre.setId(id);
            return genre;
        } else {
            throw new NotFoundException("Жанр с ID " + id + " не найден");
        }
    }

    @Override
    public List<Film> getFilmOrderByYearAndGenre(Integer count, Integer genreId, Integer year) {
        String sql = "select FILMS.FILM_ID AS film_id, FILMS.name as name, description, release_date, duration, rate, FILMS.RATING_ID as rating_id, " +
                "count(USER_ID) + RATE as CNT from FILMS left join LIKES L on FILMS.FILM_ID = L.FILM_ID " +
                "LEFT JOIN GENRE_TO_FILMS GF ON FILMS.FILM_ID = GF.FILM_ID INNER JOIN GENRE G on G.GENRE_ID = GF.GENRE_ID " +
                "where YEAR(FILMS.RELEASE_DATE) = ? AND GF.GENRE_ID = ? " +
                "group by FILMS.FILM_ID, FILMS.name, description, release_date, duration, FILMS.RATING_ID, RATE " +
                "order by CNT desc limit ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), year, genreId, count);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmOrderByYear(Integer count, Integer year) {
        String sql = "select FILMS.FILM_ID AS film_id, FILMS.name as name, description, release_date, duration, rate, FILMS.RATING_ID as rating_id, " +
                "count(USER_ID) + RATE as CNT from FILMS left join LIKES L on FILMS.FILM_ID = L.FILM_ID " +
                "LEFT JOIN GENRE_TO_FILMS GF ON FILMS.FILM_ID = GF.FILM_ID INNER JOIN GENRE G on G.GENRE_ID = GF.GENRE_ID " +
                "where YEAR(FILMS.RELEASE_DATE) = ? " +
                "group by FILMS.FILM_ID, FILMS.name, description, release_date, duration, FILMS.RATING_ID, RATE " +
                "order by CNT desc limit ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), year,  count);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilmOrderByGenre(Integer count, Integer genreId) {
        String sql = "select FILMS.FILM_ID AS film_id, FILMS.name as name, description, release_date, duration, rate, FILMS.RATING_ID as rating_id, " +
                "count(USER_ID) + RATE as CNT from FILMS left join LIKES L on FILMS.FILM_ID = L.FILM_ID " +
                "LEFT JOIN GENRE_TO_FILMS GF ON FILMS.FILM_ID = GF.FILM_ID INNER JOIN GENRE G on G.GENRE_ID = GF.GENRE_ID " +
                "where GF.GENRE_ID = ? " +
                "group by FILMS.FILM_ID, FILMS.name, description, release_date, duration, FILMS.RATING_ID, RATE " +
                "order by CNT desc limit ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilms(rs), genreId,  count);
        for (Film film : films) {
            fillFilmLikeListForFilm(film);
            fillGenresForFilm(film);
        }
        return films;
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Director addDirector(Director director) {
        if (director.getName().isBlank()) {
            log.error("Получен запрос с неверным именем режиссера");
            throw new ValidationException("Не верно указано имя режиссёра");
        }
        String sql = "INSERT INTO directors (name) VALUES (?)";
        jdbcTemplate.update(sql, director.getName());

        String sqlId = "SELECT director_id FROM directors ORDER BY director_id DESC LIMIT 1";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sqlId);
        if (directorRows.next()) {
            director.setId(directorRows.getInt("director_id"));
        }
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        getDirectorById(director.getId());
        if (director.getName().isBlank()) {
            log.error("Получен запрос с неверным именем режиссера");
            throw new ValidationException("Не верно указано имя режиссёра");
        }
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(int directorId) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql, directorId);
        Director director = new Director();
        if (directorRows.next()) {
            director.setId(directorId);
            director.setName(directorRows.getString("name"));
        } else {
            log.error("Получен запрос на получение режиссера с ID " + directorId + ", которого нет в списке");
            throw new NotFoundException("Режиссёра нет в списке");
        }
        return director;
    }

    @Override
    public void deleteDirecterById(int id) {
        getDirectorById(id);
        String sql = "DELETE FROM directors WHERE director_id = ? ";
        jdbcTemplate.update(sql, id);
        sql = "DELETE FROM director_to_films WHERE director_id = ? ";
        jdbcTemplate.update(sql, id);
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

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getInt("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    private Film makeFilms(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
        film.setMpa(getMpaById(rs.getInt("rating_id")));
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
            genresList.add(getGenreById(genreId));
        }
        film.setGenres(genresList);
    }

    private void fillDirectorsForFilm(Film film) {
        Set<Director> directors = new HashSet<>();
        for (Integer directorId : fillDirectorsListId(film.getId())) {
            directors.add(getDirectorById(directorId));
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

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("rating_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
