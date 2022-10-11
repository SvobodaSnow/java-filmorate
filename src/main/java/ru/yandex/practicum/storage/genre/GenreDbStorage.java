package ru.yandex.practicum.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.model.genre.Genre;
import ru.yandex.practicum.storage.film.GenreToFilmsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Primary
@Component
public class GenreDbStorage implements GenreStorage{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private GenreToFilmsStorage genreToFilmsStorage;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public void fillGenresForFilm(Film film) {
        Set<Genre> genresList = new HashSet<>();
        for (Integer genreId : genreToFilmsStorage.fillGenresListId(film.getId())) {
            genresList.add(getGenreById(genreId));
        }
        film.setGenres(genresList);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
