package ru.yandex.practicum.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
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
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        checkFilmValidation(film);
        String sql = "INSERT INTO films (name, description, release_date, duration, rate, rating_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
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

        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmValidation(film);
        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?, rate = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId()
        );
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            deleteGenre(film.getId());
            for (Genre genre : genres) {
                addGenre(genre, film.getId());
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
        return film;
    }

    @Override
    public Film removeLikeFilm(int filmId, int userId) {
        userDbStorage.getUserById(userId);
        Film film = getFilmById(filmId);
        film.removeLikeByUserId(userId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        return film;
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

    public Genre makeGenre(ResultSet rs) throws SQLException {
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

    private int getGenreId(ResultSet rs) throws SQLException {
        return rs.getInt("genre_id");
    }

    private void fillGenresForFilm(Film film) {
        Set<Genre> genresList = new HashSet<>();
        for (Integer genreId : fillGenresListId(film.getId())) {
            genresList.add(getGenreById(genreId));
        }
        film.setGenres(genresList);
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

    private void deleteGenre(int id) {
        String sql = "DELETE FROM genre_to_films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("rating_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
