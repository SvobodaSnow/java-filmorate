package ru.yandex.practicum.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.model.genre.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Primary
@Component
public class GenreDbStorage implements GenreStorage{

    private final JdbcTemplate jdbcTemplate;


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

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
