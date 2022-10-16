package ru.yandex.practicum.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.model.mpa.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Primary
@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;


    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("rating_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
