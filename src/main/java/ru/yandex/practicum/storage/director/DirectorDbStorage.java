package ru.yandex.practicum.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.model.director.Director;
import ru.yandex.practicum.model.film.Film;
import ru.yandex.practicum.storage.film.DirectorToFilmsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Primary
@Component
public class DirectorDbStorage implements DirectorStorage{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private DirectorToFilmsStorage directorToFilmsStorage;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public void fillDirectorsForFilm(Film film) {
        Set<Director> directors = new HashSet<>();
        for (Integer directorId : directorToFilmsStorage.fillDirectorsListId(film.getId())) {
            directors.add(getDirectorById(directorId));
        }
        film.setDirectors(directors);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getInt("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
